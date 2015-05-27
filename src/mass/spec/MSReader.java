package mass.spec;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.apache.poi.xssf.usermodel.*;
import org.jfree.chart.*;
import org.jfree.chart.entity.*;
import org.jfree.data.xy.*;
import org.uncommons.maths.random.MersenneTwisterRNG;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
        

public class MSReader extends javax.swing.JFrame {

    public MassSpectrum currentMS;
    private Properties properties;
    File documents=null, msreaderFiles=null, bin=null, temp=null;
    public ArrayList<MassSpectrum> loadedMS = new ArrayList();
    public MSChrom currentMSC = null;
//    private MSChrom tempMSC = null;
    public boolean smoothed = false;
//    public ArrayList<String> exchangeRunTitles = new ArrayList();
//    public ExchangePts exchange;
    public static final int CHROM_TYPE_EIC = 2;
    public static final int CHROM_TYPE_BPC = 1;
    public static final int CHROM_TYPE_TIC = 0;
    public int chromatogramType = CHROM_TYPE_TIC;
    public double[] XICrange;
    public MSChrom controlScan;
    public boolean autoBSB = false;
    public boolean autoSmooth = false;
    public static final int SMOOTH_MOVING_AVG = 0;
    public static final int SMOOTH_SAV_GOL = 1;
    private double stepSize;
    private SwingWorker worker;
    private PeptideList peptides_;
    public static final int COLOR_RED = 0;
    public static final int COLOR_BLUE = 1;
    
    // Singleton instance of MSReader
    private static MSReader instance;
    private static HDExchange hdExchangeInstance;
    private static MersenneTwisterRNG rng;
    
    public MSReader() {
//        try { setIconImage(ImageIO.read(getClass().getResource("/resources/C2 domain epitopes.png"))); } 
//        catch (IOException ex) { ex.printStackTrace(); }
        instance = this;
        // Initialize random number generator
        rng = new MersenneTwisterRNG();
        initComponents();
        checkOS();
        checkConfig();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        splitPane.setDividerLocation(.5);
        splitPane.setResizeWeight(.5);
        jPanel2.setMinimumSize(new Dimension(10, 10));
        resize();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                resize();
            }
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resize();
            }
        });
        chromOptions.setAccelerator(KeyStroke.getKeyStroke("control E"));
        openFile.setAccelerator(KeyStroke.getKeyStroke("control O"));
        addSpectrum.setAccelerator(KeyStroke.getKeyStroke("control A"));
        viewHelp.setAccelerator(KeyStroke.getKeyStroke("control H"));
        peptideList.setAccelerator(KeyStroke.getKeyStroke("control P"));
        fileChooser.setVisible(false);
        startUpCharts();
        populatePeptides();
    }
    
    public static MSReader getInstance () {
        return instance;
    }
    
    public static HDExchange getHDExchangeInstance () {
        if ( hdExchangeInstance == null ) hdExchangeInstance = new HDExchange();
        return hdExchangeInstance;
    }
    
    public static MersenneTwisterRNG getRNG () {
        if ( rng == null ) rng = new MersenneTwisterRNG();
        return rng;
    }
    
    public PeptideList getPeptides() { return peptides_; }
    
    public void setPeptides( PeptideList list ) { peptides_ = list; }
    
    public void addPeptidetoList ( Peptide pept ) { peptides_.addPeptide( pept ); }
    
    public void removePeptideFromList ( int index ) { peptides_.removePeptide( index ); }
    
    public String getProperty( String str ) { return properties.getProperty( str ); }
    
    public void setProperty( String key, String value ) { properties.setProperty( key, value ); }
    
    private void checkOS() {
        try {
            documents = new File (Utils.osjoin(System.getProperty("user.home"), "Documents"));

            msreaderFiles = Utils.osjoin(documents, "MSReader files");
            bin = Utils.osjoin(msreaderFiles, "bin");
            temp = Utils.osjoin(msreaderFiles, "temp");
            if (!msreaderFiles.exists()) msreaderFiles.mkdir();
            if (!temp.exists()) temp.mkdir();
            if (!bin.exists()) bin.mkdir();

        } catch ( Exception exc ) {
            Utils.showErrorMessage( "Unable to configure settings");
        }
    }
    
    private void populatePeptides() {
        File peptidepath = new File (properties.getProperty("peptidepath"));
        if (!peptidepath.exists()) {
            peptides_ = new PeptideList();
        } else {
            try {
                ObjectInputStream in = new ObjectInputStream ( 
                        new FileInputStream ( peptidepath ) 
                );
                try {
                    peptides_ = (PeptideList)in.readObject();
                    peptides_.setSortKey( 1 );
                } finally {
                    in.close();
                }
            } catch ( IOException exc ) {
                Utils.logException( exc, "Could not load peptide list" );
            } catch ( ClassNotFoundException ex) {
                Utils.logException( ex, "Could not load peptide list" );
            }
        }
    }
     
    private void checkConfig () {
        File f = Utils.osjoin(bin, "config.properties");
        if (f.exists()) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream (Utils.osjoin(bin, "config.properties")));
            } catch (IOException io) {
                initConfig();
            }
        } else {
            initConfig();
        }
    }
    
    public void initConfig() {
        properties = new Properties();
        //set appropriate properties here
        properties.setProperty("filter", "5");
        properties.setProperty("SGfilter", "5");
        properties.setProperty("SGdegree", "4");
        properties.setProperty("smoothType", ""+SMOOTH_SAV_GOL);
        properties.setProperty("windowSize", "10");
        properties.setProperty("peptidepath", "");
        properties.setProperty("heatmapcolor", ""+COLOR_RED);
        properties.setProperty("pymolpath", "");
        properties.setProperty("pdbfetchpath", temp.toString());
        properties.setProperty("gradientpath", "");
    
        saveProperties();
    }
    
    public void saveProperties () {
        try {
            FileOutputStream out = new FileOutputStream( 
                    Utils.osjoin( bin, "config.properties") );
            try {
                properties.store(new FileOutputStream(Utils.osjoin(bin, "config.properties")), null);
            } finally {
                out.close();
            }
        } catch ( IOException exc ) {
            Utils.showErrorMessage( "Unable to save properties" );
            Utils.logException( exc, "Unable to save properties");
        }
    }
    
    public int getIntProperty ( String key ) {
        return Integer.parseInt(properties.getProperty(key));
    }
    
    private void resize() {
        int frameW = this.getWidth();
        int frameH = this.getHeight();
        jPanel3.setLocation(15, jPanel3.getY());
        jPanel3.setSize((int)(frameW - 45), (int)(frameH*.6));
        splitPane.setSize(jPanel3.getWidth(), jPanel3.getHeight());
        jPanel1.setSize(splitPane.getWidth()/2, splitPane.getHeight());
        jPanel2.setSize(splitPane.getWidth()/2, splitPane.getHeight());
        jPanel2.revalidate();
        jPanel1.revalidate();
        splitPane.revalidate();
        zoomToPeak.setLocation(jPanel3.getX() + jPanel3.getWidth()/2, jPanel3.getY()+jPanel3.getHeight() + 10);
        peak.setLocation(zoomToPeak.getX() + zoomToPeak.getWidth() + 2, zoomToPeak.getY());
        resetPanel2.setLocation(zoomToPeak.getX(), zoomToPeak.getY() + zoomToPeak.getHeight() + 10);
        getSpecAtTime.setLocation(jPanel3.getX()+5, jPanel3.getY()+jPanel3.getHeight() + 10);
        spectrumattime.setLocation(getSpecAtTime.getX() + getSpecAtTime.getWidth() + 2, getSpecAtTime.getY());
        resetPanel1.setLocation(getSpecAtTime.getX(), getSpecAtTime.getY() + getSpecAtTime.getHeight() + 10);
        jLabel1.setLocation(spectrumattime.getX() + spectrumattime.getWidth() + 5, spectrumattime.getY());
        jTextField1.setLocation(jLabel1.getX() + jLabel1.getWidth() + 5, jLabel1.getY());
    }
    
//   public void setExchangePt (String key, double centroid) {
//       exchange.changePt(key, centroid);
//   }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        resetPanel2 = new javax.swing.JButton();
        zoomToPeak = new javax.swing.JButton();
        getSpecAtTime = new javax.swing.JButton();
        resetPanel1 = new javax.swing.JButton();
        spectrumattime = new javax.swing.JTextField();
        peak = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        fileMenuBar = new javax.swing.JMenuBar();
        file = new javax.swing.JMenu();
        openFile = new javax.swing.JMenuItem();
        peptideList = new javax.swing.JMenuItem();
        clearAllWindows = new javax.swing.JMenuItem();
        checkPeptideAccuracy = new javax.swing.JMenuItem();
        closeFrame = new javax.swing.JMenuItem();
        editMenuBar = new javax.swing.JMenu();
        smoothSpectrum = new javax.swing.JMenuItem();
        exportToCSV = new javax.swing.JMenuItem();
        smoothingOptions = new javax.swing.JMenuItem();
        chromOptions = new javax.swing.JMenuItem();
        hdExchangeMenu = new javax.swing.JMenu();
        viewHDExchangeMenu = new javax.swing.JMenuItem();
        addSpectrum = new javax.swing.JMenuItem();
        openHDX = new javax.swing.JMenuItem();
        exportHDXToExcel = new javax.swing.JMenuItem();
        menu = new javax.swing.JMenu();
        abort = new javax.swing.JMenuItem();
        help = new javax.swing.JMenu();
        viewHelp = new javax.swing.JMenuItem();
        experimentalMenu = new javax.swing.JMenu();
        autoHDX = new javax.swing.JMenuItem();
        manualHDX = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        sequenceEngine = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MS Reader");

        resetPanel2.setText("Reset");
        resetPanel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPanel2ActionPerformed(evt);
            }
        });

        zoomToPeak.setMnemonic('Z');
        zoomToPeak.setText("Zoom to Peak");
        zoomToPeak.setLocation(jPanel2.getX(), jPanel2.getY() + jPanel2.getHeight() + 25);
        zoomToPeak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomToPeakActionPerformed(evt);
            }
        });

        getSpecAtTime.setMnemonic('G');
        getSpecAtTime.setText("Get Spectrum at Time");
        getSpecAtTime.setToolTipText("");
        getSpecAtTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getSpecAtTimeActionPerformed(evt);
            }
        });

        resetPanel1.setText("Reset");
        resetPanel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPanel1ActionPerformed(evt);
            }
        });

        jPanel3.setLayout(new java.awt.BorderLayout());

        splitPane.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        splitPane.setDividerLocation(splitPane.getWidth()/2);
        splitPane.setResizeWeight(0.5);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.setSize(splitPane.getWidth()/2, splitPane.getHeight());
        splitPane.setLeftComponent(jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.setSize(splitPane.getWidth()/2, splitPane.getHeight());
        splitPane.setRightComponent(jPanel2);

        jPanel3.add(splitPane, java.awt.BorderLayout.CENTER);

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        jLabel1.setText("to");

        file.setText("File");

        openFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openFile.setText("Open file");
        openFile.setToolTipText("Open .cdf file");
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });
        file.add(openFile);

        peptideList.setText("Show Peptide List");
        peptideList.setToolTipText("View list of peptides");
        peptideList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peptideListActionPerformed(evt);
            }
        });
        file.add(peptideList);

        clearAllWindows.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        clearAllWindows.setText("Clear Windows");
        clearAllWindows.setToolTipText("Close all external windows");
        clearAllWindows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllWindowsActionPerformed(evt);
            }
        });
        file.add(clearAllWindows);

        checkPeptideAccuracy.setText("Check peptide accuracy");
        checkPeptideAccuracy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkPeptideAccuracyActionPerformed(evt);
            }
        });
        file.add(checkPeptideAccuracy);

        closeFrame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        closeFrame.setText("Close");
        closeFrame.setToolTipText("Close MSReader");
        closeFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFrameActionPerformed(evt);
            }
        });
        file.add(closeFrame);

        fileMenuBar.add(file);

        editMenuBar.setText("Edit");

        smoothSpectrum.setText("Smooth Spectrum");
        smoothSpectrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smoothSpectrumActionPerformed(evt);
            }
        });
        editMenuBar.add(smoothSpectrum);

        exportToCSV.setText("Export spectrum to CSV");
        exportToCSV.setToolTipText("Export currently loaded spectrum");
        exportToCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportToCSVActionPerformed(evt);
            }
        });
        editMenuBar.add(exportToCSV);

        smoothingOptions.setText("Options");
        smoothingOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smoothingOptionsActionPerformed(evt);
            }
        });
        editMenuBar.add(smoothingOptions);

        chromOptions.setText("Chromatogram Options");
        chromOptions.setToolTipText("View options for chromatogram viewing");
        chromOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chromOptionsActionPerformed(evt);
            }
        });
        editMenuBar.add(chromOptions);

        fileMenuBar.add(editMenuBar);

        hdExchangeMenu.setText("HD Exchange");

        viewHDExchangeMenu.setText("View HD Exchange");
        viewHDExchangeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewHDExchangeMenuActionPerformed(evt);
            }
        });
        hdExchangeMenu.add(viewHDExchangeMenu);

        addSpectrum.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        addSpectrum.setText("Add Loaded Spectrum");
        addSpectrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSpectrumActionPerformed(evt);
            }
        });
        hdExchangeMenu.add(addSpectrum);

        openHDX.setText("Open .hdx file");
        openHDX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openHDXActionPerformed(evt);
            }
        });
        hdExchangeMenu.add(openHDX);

        exportHDXToExcel.setText("Export .hdx to Excel");
        exportHDXToExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportHDXToExcelActionPerformed(evt);
            }
        });
        hdExchangeMenu.add(exportHDXToExcel);

        fileMenuBar.add(hdExchangeMenu);

        menu.setText("Abort");

        abort.setText("Abort process");
        abort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abortActionPerformed(evt);
            }
        });
        menu.add(abort);

        fileMenuBar.add(menu);

        help.setText("Help");

        viewHelp.setText("Help menu");
        viewHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewHelpActionPerformed(evt);
            }
        });
        help.add(viewHelp);

        fileMenuBar.add(help);

        experimentalMenu.setText("Advanced");

        autoHDX.setText("Auto HDX");
        autoHDX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoHDXActionPerformed(evt);
            }
        });
        experimentalMenu.add(autoHDX);

        manualHDX.setText("Manual HDX");
        manualHDX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualHDXActionPerformed(evt);
            }
        });
        experimentalMenu.add(manualHDX);

        jMenuItem2.setText("Generate heat map");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        experimentalMenu.add(jMenuItem2);

        sequenceEngine.setText("Sequencing engine");
        sequenceEngine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sequenceEngineActionPerformed(evt);
            }
        });
        experimentalMenu.add(sequenceEngine);

        fileMenuBar.add(experimentalMenu);

        setJMenuBar(fileMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 1306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(getSpecAtTime)
                            .addGap(67, 67, 67)
                            .addComponent(spectrumattime, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(48, 48, 48)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(146, 146, 146)
                                    .addComponent(resetPanel2)
                                    .addGap(34, 34, 34)
                                    .addComponent(zoomToPeak)
                                    .addGap(10, 10, 10)
                                    .addComponent(peak, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGap(59, 59, 59)
                                    .addComponent(resetPanel1)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(297, 297, 297)
                        .addComponent(jLabel1)))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(145, 145, 145)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(resetPanel1)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(getSpecAtTime)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spectrumattime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(resetPanel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(zoomToPeak))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(peak, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        setSize(new java.awt.Dimension(1332, 722));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
     
    private void startUpCharts() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = Utils.drawChart( dataset, "Load a CDF file to begin...","time","" );
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(785, 440));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();      
        JFreeChart chart2 = Utils.drawChart( dataset, "Spectrum", "m/z", "" );
        ChartPanel chartPanel2 = new ChartPanel(chart2);
        jPanel2.add(chartPanel2, BorderLayout.CENTER);
        jPanel2.revalidate();
    }

    public final void refreshChromatogram() {
        if (currentMSC == null) {
            startUpCharts();
            return;
        }
        if (chromatogramType == CHROM_TYPE_TIC) {
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(FormatChange.ArrayToXYSeries(currentMSC.TIC));
            JFreeChart chart = Utils.drawChart( dataset, currentMSC.title + " Total Ion Chromatogram","time(min)","" );
            ChartPanel chartPanel = new ChartPanel (chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(785, 440));
            jPanel1.removeAll();
            jPanel1.add(chartPanel, BorderLayout.CENTER);
            jPanel1.revalidate();
            final MSChrom cf = currentMSC;
            final XYSeriesCollection specData = new XYSeriesCollection();
            chartPanel.addChartMouseListener(new ChartMouseListener () {
                @Override
                public void chartMouseClicked(ChartMouseEvent cme) {
                    jPanel2.removeAll();
                    ChartEntity entity = cme.getEntity();
                    if (entity != null && entity instanceof XYItemEntity) {
                        XYItemEntity ent = (XYItemEntity) entity;
                        int iindex = ent.getItem();
                        currentMS = cf.spectra[iindex];
                        XYSeries spec;
                        if (cf.SPECTRA_UNIFORM) spec = FormatChange.ArrayToXYSeries(cf.mz_values, currentMS.yvals);
                        else spec = FormatChange.ArrayToXYSeries(currentMS.msValues);
                        specData.removeAllSeries();
                        specData.addSeries(spec);
                        JFreeChart chart2 = Utils.drawChart( specData, cf.spectra[iindex].msTitle, 
                                "m/z", "intensity");
                        ChartPanel chartPanel2 = new ChartPanel(chart2);
                        chartPanel2.setMouseWheelEnabled(false);
                        jPanel2.add(chartPanel2, BorderLayout.CENTER);
                        jPanel2.revalidate();
                        smoothed = false;
                    }
                }

                @Override
                public void chartMouseMoved(ChartMouseEvent cme) {
                }
            });
        } 
        if (chromatogramType == CHROM_TYPE_BPC) {
            double[][] bpc = currentMSC.getBPC();
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(FormatChange.ArrayToXYSeries(bpc));
            JFreeChart chart = Utils.drawChart( dataset, currentMSC.title + " Base Peak Chromatogram","time(min)","" );
            ChartPanel chartPanel = new ChartPanel (chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(785, 440));
            jPanel1.removeAll();
            jPanel1.add(chartPanel, BorderLayout.CENTER);
            jPanel1.revalidate();  
            final MSChrom cf = currentMSC;
            final XYSeriesCollection specData = new XYSeriesCollection();
            XYSeries spec; 
            if (currentMS.msValues == null) {
                spec = FormatChange.ArrayToXYSeries(cf.mz_values, currentMS.yvals);
            } else {
                spec = FormatChange.ArrayToXYSeries(currentMS.msValues);
            }
            specData.addSeries(spec);
            JFreeChart chart2 = Utils.drawChart( specData, currentMS.msTitle, "m/z", "intensity" );
            ChartPanel cp = new ChartPanel(chart2);
            cp.setMouseWheelEnabled(false);
            jPanel2.add(cp, BorderLayout.CENTER);
            jPanel2.revalidate();
            chartPanel.addChartMouseListener(new ChartMouseListener () {
                @Override
                public void chartMouseClicked(ChartMouseEvent cme) {
                    jPanel2.removeAll();
                    ChartEntity entity = cme.getEntity();
                    if (entity != null && entity instanceof XYItemEntity) {
                        XYItemEntity ent = (XYItemEntity) entity;
                        int iindex = ent.getItem();
                        currentMS = cf.spectra[iindex];
                        XYSeries spec; 
                        if (currentMS.msValues == null) {
                            spec = FormatChange.ArrayToXYSeries(cf.mz_values, currentMS.yvals);
                        } else {
                            spec = FormatChange.ArrayToXYSeries(currentMS.msValues);
                        }
                        specData.removeAllSeries();
                        specData.addSeries(spec);
                        JFreeChart chart2 = Utils.drawChart( specData, currentMS.msTitle, "m/z", "intensity" );
                        ChartPanel chartPanel2 = new ChartPanel(chart2);
                        chartPanel2.setMouseWheelEnabled(false);
                        jPanel2.add(chartPanel2, BorderLayout.CENTER);
                        jPanel2.revalidate();
                        smoothed = false;
                    }
                }

                @Override
                public void chartMouseMoved(ChartMouseEvent cme) {
                }
            });
        }
        if (chromatogramType == CHROM_TYPE_EIC) {
            double[][] eic = currentMSC.getEIC(XICrange[0], XICrange[1]);
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(FormatChange.ArrayToXYSeries(eic));
            JFreeChart chart = Utils.drawChart( dataset, currentMSC.title + " EIC Range " + XICrange[0] + "-" + XICrange[1],"time(min)","" );
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(785, 440));
            jPanel1.removeAll();
            jPanel1.add(chartPanel, BorderLayout.CENTER);
            jPanel1.revalidate();  
            final MSChrom cf = currentMSC;
            final XYSeriesCollection specData = new XYSeriesCollection();
            XYSeries spec;
            if (currentMS.msValues == null) {
                spec = FormatChange.ArrayToXYSeries(cf.mz_values, currentMS.yvals);
            } else {
                spec = FormatChange.ArrayToXYSeries(currentMS.msValues);
            }
            
            specData.addSeries(spec);
            JFreeChart chart2 = Utils.drawChart( specData, currentMS.msTitle, "m/z", "intensity" );
            ChartPanel cp = new ChartPanel(chart2);
            cp.setMouseWheelEnabled(false);
            jPanel2.add(cp, BorderLayout.CENTER);
            jPanel2.revalidate();
            chartPanel.addChartMouseListener(new ChartMouseListener () {
                @Override
                public void chartMouseClicked(ChartMouseEvent cme) {
                    jPanel2.removeAll();
                    ChartEntity entity = cme.getEntity();
                    if (entity != null && entity instanceof XYItemEntity) {
                        XYItemEntity ent = (XYItemEntity) entity;
                        int iindex = ent.getItem();
                        currentMS = cf.spectra[iindex];
                        XYSeries spec;
                        if (currentMS.msValues == null) {
                            spec = FormatChange.ArrayToXYSeries(cf.mz_values, currentMS.yvals);
                        } else {
                            spec = FormatChange.ArrayToXYSeries(currentMS.msValues);
                        }
                        specData.removeAllSeries();
                        specData.addSeries(spec);
                        JFreeChart chart2 = Utils.drawChart( specData, currentMS.msTitle, "m/z", "intensity" );
                        ChartPanel chartPanel2 = new ChartPanel(chart2);
                        chartPanel2.setMouseWheelEnabled(false);
                        jPanel2.add(chartPanel2, BorderLayout.CENTER);
                        jPanel2.revalidate();
                        smoothed = false;
                    }
                }
                @Override
                public void chartMouseMoved(ChartMouseEvent cme) {
                }
            });
        }
    }
       
    
    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        smoothed = false;
        fileChooser.setFileFilter(ExtensionFilter.msreadablefilter);
        fileChooser.setCurrentDirectory(documents);
        fileChooser.setSelectedFile(new File(""));
        fileChooser.setVisible(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        final File f = fileChooser.getSelectedFile();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String extension;
        if ( f.toString().toLowerCase().endsWith(".cdf") ) extension = "CDF";
        else if ( f.toString().toLowerCase().endsWith(".mzml" ) ) extension = "MZML";
        else {
            Utils.showErrorMessage("Error: not a valid CDF or MZML file");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        final String file_type = extension;
        final LoadingDialog ld = new LoadingDialog(this, false);
        ld.setLocationRelativeTo(this);
        ld.setVisible(true);
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    currentMSC = new MSChrom(f, file_type);
                } catch ( MzMLUnmarshallerException exc ) {
                    Utils.showErrorMessage("Error reading MZML file");
                    return null;
                }
                if (autoBSB) {
                    try {
                        currentMSC.backgroundSubtraction(controlScan);
                    } catch (DifferentResolutionsException e) {
                        Utils.showErrorMessage("Could not subtract background - "
                                + "make sure control and sample have "
                                + " same resolution");
                    } 
                }
                refreshChromatogram();
                final XYSeriesCollection specData = new XYSeriesCollection();
                currentMS = currentMSC.spectra[0];
                XYSeries spec;
                if (currentMSC.SPECTRA_UNIFORM) {
                    stepSize = currentMSC.mz_values[50] - currentMSC.mz_values[49];
                    spec = FormatChange.ArrayToXYSeries(currentMSC.mz_values, currentMS.yvals);
                } else {
                    stepSize = currentMS.msValues[0][50] - currentMS.msValues[0][49];
                    spec = FormatChange.ArrayToXYSeries(currentMS.msValues);
                }
                specData.addSeries(spec);
                JFreeChart chart2 = Utils.drawChart( specData, currentMS.msTitle, "m/z", "intensity" );
                ChartPanel chartPanel2 = new ChartPanel(chart2);
                jPanel2.removeAll();
                jPanel2.add(chartPanel2, BorderLayout.CENTER);
                jPanel2.revalidate();
                refreshChromatogram();
                return null; 
            }
            @Override
            protected void done() {
                try  {
                    ld.dispose();
                    get();
                } catch (InterruptedException e) {
                    Utils.showErrorMessage("Process cancelled"); 
                    Utils.logException(bin, e, "Cancellation error while loading file");
                } catch (ExecutionException ex) {
                    Utils.showErrorMessage("Process cancelled"); 
                    Utils.logException(bin, ex, "Cancellation error while loading file");
                } 
            }
        };
        worker.execute();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_openFileActionPerformed
    
    private void closeFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFrameActionPerformed
        System.exit(0);
    }//GEN-LAST:event_closeFrameActionPerformed

    private void smoothSpectrumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smoothSpectrumActionPerformed
        if (currentMS == null) {
            Utils.showErrorMessage("No MS file loaded");
            return;
        }
        if (smoothed) {
           int choice = JOptionPane.showConfirmDialog(this, "Spectrum has already been smoothed. Do you wish to smooth again?");
           if (choice > 0) return; 
        }
        final LoadingDialog ld = new LoadingDialog(this, false);
         worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                if (getIntProperty("smoothType") == SMOOTH_MOVING_AVG) {
                    currentMS.smoothMovingAverage(getIntProperty("filter"));
                    
                    XYSeries series = (currentMSC.SPECTRA_UNIFORM) ? FormatChange.ArrayToXYSeries(currentMSC.mz_values, currentMS.yvals) 
                        : FormatChange.ArrayToXYSeries(currentMS.msValues);
                    XYSeriesCollection dataset = new XYSeriesCollection();
                    dataset.addSeries(series);
                    JFreeChart chart = Utils.drawChart( dataset, currentMS.msTitle, "m/z", "" ); 
                    ChartPanel chartPanel = new ChartPanel (chart);
                    jPanel2.removeAll();
                    jPanel2.add(chartPanel, BorderLayout.CENTER);
                    jPanel2.revalidate();

                    smoothed = true;
                } else {
                        int SGfilter = getIntProperty("SGfilter");
                        int SGdegree = getIntProperty("SGdegree");
                    if (currentMS.msValues == null) {
                        currentMS.smoothSavitzkyGolay (currentMSC.mz_values, SGfilter, SGdegree);
                    } else {
                        currentMS.smoothSavitzkyGolay(SGfilter, SGdegree);
                    }
                        
                    XYSeries series = (currentMSC.SPECTRA_UNIFORM) ? FormatChange.ArrayToXYSeries(currentMSC.mz_values, currentMS.yvals) 
                        : FormatChange.ArrayToXYSeries(currentMS.msValues);
                    XYSeriesCollection dataset = new XYSeriesCollection();
                    dataset.addSeries(series);
                    JFreeChart chart = Utils.drawChart( dataset, currentMS.msTitle, "m/z", "" ); 
                    ChartPanel chartPanel = new ChartPanel (chart);
                    jPanel2.removeAll();
                    jPanel2.add(chartPanel, BorderLayout.CENTER);
                    jPanel2.revalidate();
                    
                    smoothed = true;
                }
                
                return null;
            }
            @Override
            protected void done() {
                ld.dispose();
            }
            };
         ld.setText("Smoothing...");
         ld.setLocationRelativeTo(this);
         ld.setVisible(true);
         worker.execute();
         
    }//GEN-LAST:event_smoothSpectrumActionPerformed
    
    private void smoothingOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smoothingOptionsActionPerformed
        OptionsMenu so = new OptionsMenu(this, true);
        so.setLocationRelativeTo(this);
        so.setVisible(true);
    }//GEN-LAST:event_smoothingOptionsActionPerformed

    private void resetPanel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPanel2ActionPerformed
        if (currentMSC != null) {
            XYSeries series = (currentMSC.SPECTRA_UNIFORM) ? FormatChange.ArrayToXYSeries(currentMSC.mz_values, currentMS.yvals) 
                    : FormatChange.ArrayToXYSeries(currentMS.msValues);
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);
            JFreeChart chart = Utils.drawChart( dataset, currentMS.msTitle, "m/z", "intensity" ); 
            ChartPanel chartPanel = new ChartPanel (chart);
            jPanel2.removeAll();
            jPanel2.add(chartPanel, BorderLayout.CENTER);
            jPanel2.revalidate();
        }
    }//GEN-LAST:event_resetPanel2ActionPerformed
 
    private void addSpectrumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSpectrumActionPerformed
        if (currentMS == null) {
            Utils.showErrorMessage("No MS file currently loaded");
            return;
        }
        HDExchangeSetup hdes = new HDExchangeSetup( true );
        hdes.setLocationRelativeTo(this);
        hdes.setVisible(true); 
    }//GEN-LAST:event_addSpectrumActionPerformed
    
    private void zoomToPeakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomToPeakActionPerformed
        if (!MSMath.isDouble(peak.getText())) {
           Utils.showMessage("Not a valid input.");
            return;
        }
        double peakNo = Double.parseDouble(peak.getText());
        double[][] data = new double [2][];
        if (currentMS.msValues == null) {
            int peakIndex = Utils.binarySearch(currentMSC.mz_values, peakNo);
            stepSize = currentMSC.mz_values[50] - currentMSC.mz_values[49];
            int windowSize = getIntProperty("windowSize");
            int startIndex = peakIndex - (int) (windowSize/stepSize);
            if (startIndex < 0) startIndex = 0; 
            int endIndex = peakIndex + (int) (windowSize/stepSize);
            if (endIndex >= currentMSC.mz_values.length) endIndex = currentMSC.mz_values.length - 1;
            data[0] = Arrays.copyOfRange(currentMSC.mz_values, startIndex, endIndex);
            data[1] = Arrays.copyOfRange(currentMS.yvals, startIndex, endIndex);
        } else {
            int peakIndex = Utils.binarySearch(currentMS.msValues[0], peakNo);
            stepSize = currentMS.msValues[0][50] - currentMS.msValues[0][49];
            int windowSize = getIntProperty("windowSize");
            int startIndex = peakIndex - (int) (windowSize/stepSize);
            if (startIndex < 0) startIndex = 0; 
            int endIndex = peakIndex + (int) (windowSize/stepSize);
            if (endIndex >= currentMS.msValues[0].length) endIndex = currentMS.msValues[0].length - 1; 
            data[0] = Arrays.copyOfRange(currentMS.msValues[0], startIndex, endIndex);
            data[1] = Arrays.copyOfRange(currentMS.msValues[1], startIndex, endIndex);
        }

        
        XYSeries series = FormatChange.ArrayToXYSeries(data);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        String zoomTitle = "peak " + peakNo;
        DecimalFormat dataformat = FormatChange.getFormat(stepSize);
        FormatChange.FormatArray(data[0], dataformat);
        Peak_Zoom pz = new Peak_Zoom(this, data, zoomTitle);
        pz.setPeak(peakNo);
        pz.setVisible(true);  
    }//GEN-LAST:event_zoomToPeakActionPerformed

    private void resetPanel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPanel1ActionPerformed
        refreshChromatogram();
    }//GEN-LAST:event_resetPanel1ActionPerformed

    private void getSpecAtTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getSpecAtTimeActionPerformed
        if (currentMS == null) {
            Utils.showErrorMessage("Load a spectrum first!!");
            return;
        }
        if (!MSMath.isDouble(spectrumattime.getText())) {
            Utils.showErrorMessage("Not a valid input. Try again...");
            return;
        }
        if (!jTextField1.getText().equals("")) {
            double time1 = Double.parseDouble(spectrumattime.getText());
            double time2 = Double.parseDouble(jTextField1.getText());
            spectrumattime.setText("");
            jTextField1.setText("");
            int index1 = Utils.binarySearch(currentMSC.TIC[0], time1);
            int index2 = Utils.binarySearch(currentMSC.TIC[0], time2);
            if (index1 >= currentMSC.TIC[0].length) index1 = currentMSC.TIC[0].length - 1;
            if (index2 >= currentMSC.TIC[0].length) index2 = currentMSC.TIC[0].length - 1;
            try {
                currentMS = currentMSC.combineSpectra(index1, index2);
            } catch (DifferentResolutionsException e) {
                Utils.showErrorMessage("Spectra cannot be combined because the dimensions are not uniform");
                return;
            }
        } else {
            double time = Double.parseDouble(spectrumattime.getText());
            spectrumattime.setText("");
            
            int tIndex = Utils.binarySearch (currentMSC.TIC[0], time);
            if (tIndex >= currentMSC.spectra.length) tIndex = currentMSC.spectra.length - 1;
            currentMS = currentMSC.spectra[tIndex];
        }
        XYSeries spec;
        if (currentMS.msValues == null) {
            spec = FormatChange.ArrayToXYSeries(currentMSC.mz_values, currentMS.yvals);
        } else {
            spec = FormatChange.ArrayToXYSeries(currentMS.msValues);
        }
        
        XYSeriesCollection specData = new XYSeriesCollection();
        specData.addSeries(spec);
        JFreeChart chart2 = Utils.drawChart( specData, currentMS.msTitle, "m/z", "intensity" );
        ChartPanel chartPanel2 = new ChartPanel(chart2);
        chartPanel2.setPreferredSize(new java.awt.Dimension(785, 440));
        jPanel2.removeAll();
        jPanel2.add(chartPanel2, BorderLayout.CENTER);
        jPanel2.setVisible(true);
        jPanel2.revalidate();
        smoothed = false;
    }//GEN-LAST:event_getSpecAtTimeActionPerformed


    private void viewHDExchangeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewHDExchangeMenuActionPerformed
        if ( hdExchangeInstance == null ) {
            Utils.showErrorMessage("No HDX initialized");
            return;
        } else if ( hdExchangeInstance.numberOfScans() < 2) {
            Utils.showErrorMessage("Need to have multiple spectra loaded before viewing HDX");
            return;
        }
        ChoosePeakNo cpn = new ChoosePeakNo (this, true);
        cpn.setLocationRelativeTo(this);
        cpn.setVisible(true);
        Peptide current = cpn.getPeptide();
        hdExchangeInstance.setPeptide( current );
        hdExchangeInstance.analyze();
    }//GEN-LAST:event_viewHDExchangeMenuActionPerformed

        
    private void chromOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chromOptionsActionPerformed
        Chromatogram_Options cho = new Chromatogram_Options(this, true);
        cho.setLocationRelativeTo(this);
        cho.setVisible(true);
        refreshChromatogram();
    }//GEN-LAST:event_chromOptionsActionPerformed

    private void peptideListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_peptideListActionPerformed
        if ( peptides_ == null ) {
            peptides_ = new PeptideList();
        }
        ViewPeptides pl = new ViewPeptides( this, true );
        pl.setLocationRelativeTo( this );
        pl.setVisible( true );
    }//GEN-LAST:event_peptideListActionPerformed

    private void openHDXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openHDXActionPerformed
        fileChooser.setCurrentDirectory (msreaderFiles);
        fileChooser.setFileFilter(ExtensionFilter.hdxfilter);
        fileChooser.setSelectedFile(new File(""));
        fileChooser.setVisible(true);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File[] files = fileChooser.getSelectedFiles();
        HDRun[] hdr = new HDRun[files.length];
        Object temp;
        for (int i = 0; i < files.length; i++) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(files[i]));
                temp = ois.readObject();
                if (!(temp instanceof HDRun)) {
                    Utils.showErrorMessage("Error: one or more files are invalid");
                    return;
                }
                hdr[i] = (HDRun)temp;
                ois.close();
            } catch (IOException ex) {
                Utils.showErrorMessage("Error: one or more files are invalid");
            } catch (ClassNotFoundException e) {
                Utils.showErrorMessage("Error: one or more files are invalid");
            }
        }
        if (hdr.length > 15) {
            Utils.showErrorMessage("Cannot overlay more than 15 HDX files at once");
            return;
        }
        HDX_Form HDX = new HDX_Form(this);
        HDX.altOverlay(hdr);
        HDX.setVisible(true);
    }//GEN-LAST:event_openHDXActionPerformed
       
    private void exportSingleHDX (File f, File filepath) {
        final LoadingDialog ld = new LoadingDialog(this, false);
        ld.setLocationRelativeTo(this);
        ld.setText("Exporting....");
        ld.setVisible(true);
        final File FILE = f;
        final File path = filepath;
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws IOException, FileNotFoundException, ClassNotFoundException{
                ObjectInputStream ois;
                try {
                    ois = new ObjectInputStream(new FileInputStream(FILE)); 
                    HDRun hdr = (HDRun)ois.readObject();
                    ois.close();
                    if (path.exists()) {
                        int opt = JOptionPane.showOptionDialog(null, (Object)path.toString() +" already exists. Overwrite?",
                            "Overwrite file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                            null, new Object[] {"Yes", "No"}, (Object)"Yes");
                        if (opt == 1) { return null; }
                        path.delete();
                    }
                    try { 
                        path.createNewFile(); 
                    }
                    catch (FileNotFoundException e) { 
                       Utils.showMessage("Error: MSReader cannot access the file because it is being used by another process");
                        return null;
                    }
                    FileOutputStream fos = new FileOutputStream(path);
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet worksheet = workbook.createSheet(".HDX conversion");
                    int rowno = 0;
                    XSSFRow rowA = worksheet.createRow(rowno);
                    rowA.createCell(0).setCellValue(hdr.title);
                    rowA.createCell(1).setCellValue("A: ");
                    rowA.createCell(2).setCellValue(hdr.A);
                    rowA.createCell(3).setCellValue("K: ");
                    rowA.createCell(4).setCellValue(hdr.K);
                    rowno++;
                    XSSFRow rowB = worksheet.createRow(rowno);
                    rowno++;
                    XSSFRow rowC = worksheet.createRow(rowno);
                    rowB.createCell(0).setCellValue("Time");
                    rowC.createCell(0).setCellValue("D/res");
                    for (int j = 1; j < hdr.exchangeValues.length+1; j++) {
                        rowB.createCell(j).setCellValue((Double)hdr.exchangeValues[j-1][0]);
                        rowC.createCell(j).setCellValue((Double)hdr.exchangeValues[j-1][1]);
                    }
                    ois.close();
                    workbook.write(fos);
                    fos.flush();
                    fos.close();
                    return null;
            } catch (IOException e) {
                throw new IOException();
            } catch (ClassNotFoundException c) {
                throw new ClassNotFoundException();
            }
        }  
           
        @Override
        protected void done () {
            try { get();
            } catch (InterruptedException ex) {Utils.showErrorMessage("Could not convert file");}
            catch (ExecutionException e) { Utils.showErrorMessage("Could not convert file"); }
            ld.dispose();
        }}; 
        
        worker.execute(); 
    }
    
    public void exportMultipleHDX(File[] f, File filepath) {
        final File[] files = f;
        final File path = filepath;
        final LoadingDialog ld = new LoadingDialog(this, false);
        ld.setLocationRelativeTo(this);
        ld.setText("Exporting....");
        ld.setVisible(true);
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground()  throws IOException, FileNotFoundException, ClassNotFoundException {
            HDRun[] hdr = new HDRun[files.length];
            ObjectInputStream ois = null;
            for (int i = 0; i < files.length; i++) {
                try {
                    FileInputStream fis = new FileInputStream(files[i]);
                    ois = new ObjectInputStream(fis);
                    hdr[i] = (HDRun)ois.readObject();
                }
                catch (IOException ex) {Utils.showErrorMessage("Error: one or more files are invalid");
                    return null;} catch (ClassNotFoundException e) {
                    Utils.showErrorMessage("Error: one or more files are invalid");
                    return null;
                }
            }
            if (ois!=null) ois.close(); 
            if (path.exists()) {
                int opt = JOptionPane.showOptionDialog(null, (Object)path.toString() +" already exists. Overwrite?",
                    "Overwrite file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                    null, new Object[] {"Yes", "No"}, (Object)"Yes");
                if (opt == 1) { return null; }
                path.delete();
            }
            FileOutputStream fos;
            try {
                path.createNewFile();
                fos = new FileOutputStream(path);
            }
            catch (FileNotFoundException e) { 
               Utils.showMessage("Error: MSReader cannot access the file because it is being used by another process");
                return null;
            }
            XSSFWorkbook workbook = new XSSFWorkbook(); 
            XSSFSheet worksheet = workbook.createSheet(".HDX conversion");
            int rowno = 0;
            for (int i = 0; i < files.length; i++) {
                XSSFRow rowA = worksheet.createRow(rowno);
                rowA.createCell(0).setCellValue(hdr[i].title);
                rowA.createCell(1).setCellValue("A: ");
                rowA.createCell(2).setCellValue(hdr[i].A);
                rowA.createCell(3).setCellValue("K: ");
                rowA.createCell(4).setCellValue(hdr[i].K);
                rowno++;
                XSSFRow rowB = worksheet.createRow(rowno);
                rowno++;
                XSSFRow rowC = worksheet.createRow(rowno);
                rowB.createCell(0).setCellValue("Time");
                rowC.createCell(0).setCellValue("D/res");
                for (int j = 1; j < hdr[i].exchangeValues.length+1; j++) {
                    rowB.createCell(j).setCellValue((Double)hdr[i].exchangeValues[j-1][0]);
                    rowC.createCell(j).setCellValue((Double)hdr[i].exchangeValues[j-1][1]);
                }
                rowno+=2;
            }
            workbook.write(fos);
            fos.flush();
            fos.close();
            return null;
        }
        @Override
        protected void done() {
            try { 
                get(); 
            } catch (InterruptedException ex) {Utils.showErrorMessage("Process was cancelled unexpectedly");} catch(ExecutionException e) { 
                Utils.showErrorMessage("Process was cancelled unexpectedly");
            }
            ld.dispose();
        } };
        worker.execute();  
    }
                    
    private void exportHDXToExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportHDXToExcelActionPerformed
        fileChooser.setCurrentDirectory(msreaderFiles);
        fileChooser.setFileFilter(ExtensionFilter.hdxfilter);
        fileChooser.setSelectedFile(new File(""));
        fileChooser.setVisible(true);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File[] files = fileChooser.getSelectedFiles();
            for (File f: files) {
                if (!f.toString().toLowerCase().contains(".hdx")) {
                    Utils.showMessage("Error - one or more files are invalid");
                    return;
                }
            }
            fileChooser.setVisible(true);
            fileChooser.setDialogTitle("Choose file destination");
            fileChooser.setSelectedFile(new File(""));
            fileChooser.setCurrentDirectory(msreaderFiles);
            fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
            int val = fileChooser.showSaveDialog(this);
            File savepath;
            if (val == JFileChooser.APPROVE_OPTION) {
                savepath = fileChooser.getSelectedFile();
            }
            else return; 
            final File path = new File(savepath.toString() + ".xlsx");
            if (files.length > 1) exportMultipleHDX(files, path);
            else exportSingleHDX(files[0], path);
        }
    }//GEN-LAST:event_exportHDXToExcelActionPerformed

    private void viewHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewHelpActionPerformed
        Help_Popup hp = new Help_Popup();
        hp.setLocationRelativeTo(this);
        hp.setVisible(true);
    }//GEN-LAST:event_viewHelpActionPerformed

    private void exportToCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportToCSVActionPerformed
        if (currentMS == null) {
            Utils.showErrorMessage("No spectrum loaded");
            return;
        } 
        
        String saver = currentMS.runTitle + " " + currentMS.msTitle;
        saver = saver.replace(".", "_");
        saver = saver.replace(":", " ");
        fileChooser.setSelectedFile(new File(saver+ ".csv"));
        fileChooser.setCurrentDirectory(msreaderFiles);
        fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.setVisible(true);
        int val = fileChooser.showSaveDialog(this);
        File savepath;
        if (val == JFileChooser.APPROVE_OPTION) {
            savepath = fileChooser.getSelectedFile();
        }
        else return; 
        final File f = new File(savepath.toString() + ".csv");
        if (f.exists()) f.delete(); 
        try { 
            f.createNewFile();
        } catch (IOException io) { 
            Utils.showErrorMessage("Could not create file"); 
            return; 
        }
        final LoadingDialog ld = new LoadingDialog(this, false);
        ld.setLocationRelativeTo(this);
        ld.setText("Converting...");
        ld.setVisible(true);
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                PrintWriter out;
                try {
                    out = new PrintWriter (new FileOutputStream(f));
                    if (currentMS.msValues == null) {
                        for (int i = 0; i < currentMSC.mz_values.length; i++) {
                            out.println(currentMSC.mz_values[i] + "," + currentMS.yvals[i]);
                        }
                    } else {
                        for (int i = 0; i < currentMS.msValues[0].length; i++) {
                            out.println(currentMS.msValues[0][i] + ","+currentMS.msValues[1][i]);
                        }
                    }
                    out.close();
                    return null;
                } catch (IOException e) {
                    Utils.showErrorMessage("Error converting file");
                    return null;
                }
            }
            @Override
            protected void done() {
                try {
                    ld.dispose();
                    get();
                } catch (InterruptedException ex) {Utils.showErrorMessage("Process was cancelled unexpectedly");} catch(ExecutionException e) { 
                Utils.showErrorMessage("Process was cancelled unexpectedly"); }
            }
        };
        worker.execute();
    }//GEN-LAST:event_exportToCSVActionPerformed

    private void clearAllWindowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllWindowsActionPerformed
        Frame[] f = Frame.getFrames();
        for (Frame fr: f) {
            if (fr != this) fr.dispose(); 
        }
    }//GEN-LAST:event_clearAllWindowsActionPerformed

    private void abortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_abortActionPerformed
        
        if (worker!=null) worker.cancel(true);
    }//GEN-LAST:event_abortActionPerformed

    private void autoHDXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoHDXActionPerformed
        performHDX( true );
    }//GEN-LAST:event_autoHDXActionPerformed

    private void manualHDXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualHDXActionPerformed
        performHDX( false );
    }//GEN-LAST:event_manualHDXActionPerformed

    /* 
    Performs semi-automatic HD exchange. User selects a folder full of 
    data files for each time point, and each one is opened, extracted
    for the peptide of interest, trimmed (if the boolean variable auto is
    true), and combined to show a full HD exchange plot.  
    */
    private void performHDX ( final boolean auto ) {
        AutoHDXSetup a = new AutoHDXSetup( this, true );
        a.setTitle ( (auto) ? 
                "Choose files for Auto HDX" : "Choose files for Manual HDX" );
        a.setLocationRelativeTo(this);
        a.setVisible(true);
        final File[] files = a.getFiles();
        for ( File f : files ) {
            if ( Utils.getDeutTimePoint(f.toString()) == -1 ) {
                Utils.showErrorMessage( "Error: file "+f.toString()+" is named "
                    + "improperly. Could not extract time point." );
                return;
            }
        }
        if (files.length == 0) return;
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare (File o1, File o2) {
                if ( Utils.getDeutTimePoint(o1.toString()) > Utils.getDeutTimePoint(o2.toString())) return 1;
                else if ( Utils.getDeutTimePoint(o1.toString()) < Utils.getDeutTimePoint(o2.toString())) return -1;
                else return 0;
            }
        });
        
        ChoosePeakNo cpn = new ChoosePeakNo(this, true);
        cpn.setLocationRelativeTo(this);
        cpn.setVisible(true);
        final Peptide pept = cpn.getPeptide();
        if (pept == null) return;
        final LoadingDialog ld = new LoadingDialog(null, false);
        
        hdExchangeInstance = new HDExchange();
        hdExchangeInstance.setPeptide( pept );
        
        // Saves current MSC to place back on the screen after running auto hdx
        // May be unnecessary and take up a lot of memory
//        if (currentMSC != null) tempMSC = currentMSC.copy();
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws MzMLUnmarshallerException {
                int elutionIndex;
                double [][] eic; 
                String extension = "";
                for ( File f : files ) {
                    System.out.println("Processing "+f.toString());
                    if ( f.toString().toLowerCase().endsWith(".cdf") ) extension = "CDF";
                    else if ( f.toString().toLowerCase().endsWith(".mzml" ) ) extension = "MZML";
                    try {
                        currentMSC = new MSChrom( f, extension );
                    } catch ( MzMLUnmarshallerException exc ) {
                        Utils.showErrorMessage( "Error reading file" );
                        return null;
                    }
                    
                    //If zero time point change XIC parameters
                    if ( Utils.getDeutTimePoint(f.toString()) == 0) eic = currentMSC.getEIC(pept.mz - 2, pept.mz + 2);
                    else eic = (pept.mz > 1000) ? currentMSC.getEIC(pept.mz - 1, pept.mz + 5): 
                            currentMSC.getEIC(pept.mz - 2, pept.mz + 2);
                    elutionIndex = currentMSC.getElutionIndexFromEIC(eic, pept.elutiontime);

                    currentMS = currentMSC.spectra[ elutionIndex ];
                    
                    if (autoSmooth) {
                        switch (getIntProperty("smoothType")) {
                            case 0:
                                currentMS.smoothMovingAverage(getIntProperty("filter"));
                                break;
                            case 1:
                                currentMS.smoothSavitzkyGolay(getIntProperty("SGfilter"), getIntProperty("SGdegree"));
                                break;
                        }
                    }
                    
                    hdExchangeInstance.addSpectrum( currentMS );
                    
                    // Erase current chromatogram to save space
                    currentMSC = null;
                }
              return null;  
            }
            @Override
            protected void done() {
                try { 
                    get(); 
                } catch (InterruptedException ex) {
                    Utils.showErrorMessage("Process was cancelled");
                    ld.dispose();
                    Utils.logException(bin, ex, "Cancellation error during auto hdx");
                    return;
                } catch(ExecutionException e) {
                    Utils.showErrorMessage("Process was cancelled");
                    ld.dispose();
                    Utils.logException(bin, e, "Cancellation error during auto hdx");
                    return;
                }

                hdExchangeInstance.analyze();
                
                getHDExchangeInstance().updateSummary();
                
                // Cycle through all time points and trim if AutoHDX
                if ( auto ) hdExchangeInstance.trimAllSpectra();
                
                // Restore the chromatogram from the start
//                if ( tempMSC!= null ) currentMSC = tempMSC.copy();
                refreshChromatogram();
                ld.dispose();
                resize();
        }};
        ld.setLocationRelativeTo(this);
        ld.setVisible(true);
        worker.execute();
    }
     
    private void checkPeptideAccuracyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkPeptideAccuracyActionPerformed
        if (currentMSC == null) {
            Utils.showErrorMessage("Please load file first");
            return;
        }
        ChoosePeakNo cpn = new ChoosePeakNo(this, true);
        cpn.setLocationRelativeTo(this);
        cpn.setVisible(true);
        final Peptide pept = cpn.getPeptide();
        if ( pept == null ) return;
        double[][] eic = currentMSC.getEIC(pept.mz - 2, pept.mz + 2);
        int elutionindex = currentMSC.getElutionIndexFromEIC(eic, pept.elutiontime);
        currentMS = currentMSC.spectra[elutionindex];

        currentMS.convertToNonUniform( currentMSC );
        int windowSize = MSReader.getInstance().getIntProperty("windowSize");
        
        double[][] dataRange = currentMS.getWindow( pept.mz, windowSize );
            
        double[][] isotope = pept.getThreadedDistribution((int)Math.pow(10, 6));
        
        // Normalize the isotopic distribution percentages
        double max = MSMath.getMax ( dataRange[ 1 ] ); 
        max /= MSMath.getMax( isotope[1] );
        for (int i = 0; i < isotope[1].length; i++) isotope[1][i] *= max;
        
        XYSeries dataSeries = FormatChange.ArrayToXYSeries( dataRange ); 
        
        XYSeries isotopeSeries = FormatChange.ArrayToXYSeries(isotope, "tope");
        
        // Hack to force lines to go back to zero on either side of isotopic peak
        for (int i = 0; i < isotope[0].length; i++) {
            isotopeSeries.add( isotope[0][i], 0.0 );
            isotopeSeries.add( isotope[0][i], Double.NaN );
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( dataSeries );
        dataset.addSeries( isotopeSeries );
        JFreeChart chart = Utils.drawChart( dataset, pept.displaySequence, 
                "m/z","intensity" );
        
        Peak_Zoom pz = new Peak_Zoom();
        pz.setChart(chart);
        pz.expandGraph();
        pz.setTitle( MSMath.getScore( dataRange, isotope ) + "" );
        pz.setVisible(true);
    }//GEN-LAST:event_checkPeptideAccuracyActionPerformed

    private void sequenceEngineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sequenceEngineActionPerformed
        SequenceEngine eng = new SequenceEngine(this);
        eng.setLocationRelativeTo(this);
        eng.setVisible(true);
    }//GEN-LAST:event_sequenceEngineActionPerformed
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        ProtectionMapOptions opt = new ProtectionMapOptions(this, true);
        opt.setLocationRelativeTo(this);
        opt.setVisible(true);
        final boolean protection = opt.getExchangeBool();
        final File pdb = opt.getPDB();
        boolean HDXinput = opt.getHDXBool();
        final String seq = opt.getSequence();
        final File outputpath = opt.getOutputPath();
        if (pdb == null || seq.equals("")) return;
        if (!outputpath.getParentFile().exists()) {
            Utils.showErrorMessage ("Invalid script output path");
            return;
        }
        final HeatMapGradient gradient = getGradient();
        if (properties.getProperty("pymolpath").equals("")) {
            final File[] x = new File[1];
            final LoadingDialog ld = new LoadingDialog (null, false);
            ld.setLocationRelativeTo(this);
            ld.setText("Retrieving PyMOL path...");
            ld.setVisible(true);
            worker = new SwingWorker <Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    x[0] = ProtectionMap.getPymolLocation();
                    return null;
                }

                @Override
                public void done() {
                    ld.dispose();
                    if (x[0] == null) {
                        Utils.showMessage("Could not locate Pymol executable on your system. Please select its path");
                        fileChooser = new JFileChooser();
                        fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
                        fileChooser.setCurrentDirectory(documents);
                        fileChooser.setVisible(true);
                        int returnVal = fileChooser.showOpenDialog(null);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            x[0] = fileChooser.getSelectedFile();
                        } 
                        else return;
                    }
            
            
            properties.setProperty("pymolpath", x[0].toString());
            saveProperties();
                }
            };
            worker.execute();
        }
        
        final File pymol = new File (properties.getProperty("pymolpath"));
        if (HDXinput) {
            final HDRun[][] sender;
            
            if (protection) {
                fileChooser.setFileFilter(ExtensionFilter.hdxfilter);
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setDialogTitle("Choose HDX files for free state");
                fileChooser.setCurrentDirectory(msreaderFiles);
                fileChooser.setSelectedFile(new File (""));
                fileChooser.setVisible(true);
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                File[] filegroup = fileChooser.getSelectedFiles();

                HDRun[] hdfree = new HDRun[filegroup.length];
                for (int i = 0; i < filegroup.length; i++) {
                    ObjectInputStream in;
                    try {
                        in = new ObjectInputStream(new FileInputStream(filegroup[i]));
                        HDRun hdx = (HDRun)in.readObject();
                        hdfree[i] = hdx;
                    } catch (IOException ex) {Utils.showErrorMessage ("Unable to load HDX files");
                        Utils.logException (bin, ex, "Couldn't load HDX");} catch(ClassNotFoundException e) {
                        Utils.showErrorMessage ("Unable to load HDX files");
                        Utils.logException (bin, e, "Couldn't load HDX");
                    }
                }
                
                fileChooser.setDialogTitle("Choose HDX files for bound state");
                fileChooser.setCurrentDirectory(msreaderFiles);
                fileChooser.setSelectedFile(new File (""));
                fileChooser.setVisible(true);
                returnVal = fileChooser.showOpenDialog(this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                filegroup = fileChooser.getSelectedFiles();
                
                HDRun[] hdbound = new HDRun[filegroup.length];
                for (int i = 0; i < filegroup.length; i++) {
                    ObjectInputStream in;
                    try {
                        in = new ObjectInputStream(new FileInputStream(filegroup[i]));
                        HDRun hdx = (HDRun)in.readObject();
                        hdbound[i] = hdx;
                    } catch (IOException ex) {Utils.showErrorMessage ("Unable to load HDX files");
                        Utils.logException (bin, ex, "Couldn't load HDX");} catch (ClassNotFoundException e) {
                        Utils.showErrorMessage ("Unable to load HDX files");
                        Utils.logException (bin, e, "Couldn't load HDX");
                    }
                }
                
                sender = new HDRun[2][];
                sender[0] = hdfree;
                sender[1] = hdbound;
                
            } else {
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setFileFilter(ExtensionFilter.hdxfilter);
                fileChooser.setDialogTitle("Choose HDX files for heat map generation");
                fileChooser.setCurrentDirectory(msreaderFiles);
                fileChooser.setSelectedFile(new File (""));
                fileChooser.setVisible(true);
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                File[] filegroup = fileChooser.getSelectedFiles();

                HDRun[] hdfree = new HDRun[filegroup.length];
                for (int i = 0; i < filegroup.length; i++) {
                    ObjectInputStream in;
                    try {
                        in = new ObjectInputStream(new FileInputStream(filegroup[i]));
                        HDRun hdx = (HDRun)in.readObject();
                        hdfree[i] = hdx;
                    } catch (IOException ex) {Utils.showErrorMessage ("Unable to load HDX files");
                        Utils.logException (bin, ex, "Couldn't load HDX");} catch(ClassNotFoundException e) {
                        Utils.showErrorMessage ("Unable to load HDX files");
                        Utils.logException (bin, e, "Couldn't load HDX");
                    }
                }
                
                sender = new HDRun[1][];
                sender[0] = hdfree;
            }
            
            
            final LoadingDialog ld = new LoadingDialog(this, false);
            ld.setText("Generating heat map...");
            ld.setLocationRelativeTo(this);
            ld.setVisible(true);
            worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground () {
                    ProtectionMap.createProtectionMap(sender, pdb, pymol, outputpath, gradient, seq);
                    return null;
                }
                
                @Override
                public void done() {
                    try {
                        ld.dispose();
                        get();
                    } catch (InterruptedException ex) { Utils.logException(bin, ex, "swing worker interrupted");} catch(ExecutionException i) {
                        Utils.logException(bin, i, "swing worker interrupted");
                    }
                }
            };
            worker.execute();
        } else {
            final File[] excelpaths;
            if (protection) {
                Utils.showMessage("Select excel file for free state");
                fileChooser.setCurrentDirectory(documents);
                fileChooser.setSelectedFile(new File(""));
                fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
                fileChooser.setVisible(true);
                fileChooser.setDialogTitle("Select excel file for free state");
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                excelpaths = new File[2];
                excelpaths[0] = fileChooser.getSelectedFile();
                Utils.showMessage ("Select excel file for bound state");
                fileChooser.setCurrentDirectory(documents);
                fileChooser.setSelectedFile(new File(""));
                fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
                fileChooser.setVisible(true);
                fileChooser.setDialogTitle("Select excel file for bound state");
                returnVal = fileChooser.showOpenDialog(this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                excelpaths[1] = fileChooser.getSelectedFile();
            } else {
                Utils.showMessage("Select excel file with heat map data");
                fileChooser.setCurrentDirectory(documents);
                fileChooser.setSelectedFile(new File(""));
                fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
                fileChooser.setVisible(true);
                fileChooser.setDialogTitle("Select excel file to generate heat map");
                int returnVal = fileChooser.showOpenDialog(this);
                if (returnVal != JFileChooser.APPROVE_OPTION) return;
                excelpaths = new File[1];
                excelpaths[0] = fileChooser.getSelectedFile();
            }
            final LoadingDialog ld = new LoadingDialog(this, false);
            ld.setText("Generating heat map....");
            ld.setLocationRelativeTo(this);
            ld.setVisible(true);
            worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    ProtectionMap.createProtectionMap(excelpaths, pdb, outputpath, pymol, gradient, seq);
                   return null;
                }

                @Override
                protected void done() {
                    ld.dispose();
                    try {
                        get();
                    } catch (InterruptedException e){Logger.getLogger(MSReader.class.getName()).log(Level.SEVERE, null, e);} catch(ExecutionException ex) {
                        Logger.getLogger(MSReader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            worker.execute();
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    public HeatMapGradient getGradient () {
        if (properties.getProperty("gradientpath").equals("")) initGradient();
        if (!new File (properties.getProperty("gradientpath")).exists()) initGradient();
        try {
            ObjectInputStream ois = new ObjectInputStream (new FileInputStream (new File (properties.getProperty("gradientpath"))));
            return (HeatMapGradient)ois.readObject();
        } catch ( IOException e ) { 
            Utils.logException(bin, e);
            return null;
        } catch (ClassNotFoundException e) {
            Utils.logException(bin, e);
            return null;
        }
    }
    
    public void initGradient() {
        HeatMapGradient grade = new HeatMapGradient();
        grade.addRedLevel (.1425, new int[] {150, 0, 0});
        grade.addRedLevel (.135, new int[] {165, 0, 0});
        grade.addRedLevel (.1275, new int[] {175, 0, 0});
        grade.addRedLevel (.12, new int[] {185, 0, 0});
        grade.addRedLevel (.1125, new int[] {255, 0, 0});
        grade.addRedLevel (.105, new int[] {255, 40, 0});
        grade.addRedLevel (.09, new int[] {255, 80, 0});
        grade.addRedLevel (.075, new int[] {255, 100, 0});
        grade.addRedLevel (.06, new int[] {255, 140, 0});
        grade.addRedLevel (.045, new int[] {255, 160, 0});
        grade.addRedLevel (.03, new int[] {255, 200, 0});
        grade.addRedLevel (.015, new int[] {255, 225, 0});
        grade.addBlueLevel (.379917, new int[] {0, 0, 150});
        grade.addBlueLevel (.368167, new int[] {0, 0, 165});
        grade.addBlueLevel (.3525, new int[] {0, 0, 175});
        grade.addBlueLevel (.336833, new int[] {0, 0, 185});
        grade.addBlueLevel (.325083, new int[] {0, 0, 195});
        grade.addBlueLevel (.313333, new int[] {0, 0, 200});
        grade.addBlueLevel (.29375, new int[] {0, 0, 255});
        grade.addBlueLevel (.274167, new int[] {0, 40, 255});
        grade.addBlueLevel (.254583, new int[] {0, 80, 255});
        grade.addBlueLevel (.235, new int[] {0, 160, 255});
        grade.addBlueLevel (.156667, new int[] {0, 200, 255});
        grade.addBlueLevel (.1175, new int[] {0, 225, 255});
        File gradpath = Utils.osjoin(bin, "gradient.gr");
        grade.setPath (gradpath);
        grade.save();
        properties.setProperty("gradientpath", gradpath.toString());
        saveProperties();
    }
    

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MSReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MSReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MSReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MSReader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                /*try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    
                }*/
                new MSReader().setVisible(true);
            }
        });
    }


    

    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem abort;
    private javax.swing.JMenuItem addSpectrum;
    private javax.swing.JMenuItem autoHDX;
    private javax.swing.JMenuItem checkPeptideAccuracy;
    private javax.swing.JMenuItem chromOptions;
    private javax.swing.JMenuItem clearAllWindows;
    private javax.swing.JMenuItem closeFrame;
    private javax.swing.JMenu editMenuBar;
    private javax.swing.JMenu experimentalMenu;
    private javax.swing.JMenuItem exportHDXToExcel;
    private javax.swing.JMenuItem exportToCSV;
    private javax.swing.JMenu file;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenuBar fileMenuBar;
    private javax.swing.JButton getSpecAtTime;
    private javax.swing.JMenu hdExchangeMenu;
    private javax.swing.JMenu help;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JMenuItem manualHDX;
    private javax.swing.JMenu menu;
    private javax.swing.JMenuItem openFile;
    private javax.swing.JMenuItem openHDX;
    private javax.swing.JTextField peak;
    private javax.swing.JMenuItem peptideList;
    private javax.swing.JButton resetPanel1;
    private javax.swing.JButton resetPanel2;
    private javax.swing.JMenuItem sequenceEngine;
    private javax.swing.JMenuItem smoothSpectrum;
    private javax.swing.JMenuItem smoothingOptions;
    private javax.swing.JTextField spectrumattime;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JMenuItem viewHDExchangeMenu;
    private javax.swing.JMenuItem viewHelp;
    private javax.swing.JButton zoomToPeak;
    // End of variables declaration//GEN-END:variables

}

