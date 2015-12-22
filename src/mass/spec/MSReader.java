package mass.spec;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.entity.*;
import org.jfree.data.xy.*;
import org.uncommons.maths.random.MersenneTwisterRNG;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
        

public class MSReader extends javax.swing.JFrame {

    public enum chromType { CHROM_TYPE_TIC, CHROM_TYPE_BPC, CHROM_TYPE_EIC }
    public enum smooth { NONE, SMOOTH_MOVING_AVG, SMOOTH_SAV_GOL }
    
    public MassSpectrum currentMS;
    private Properties properties;
    public File documents=null, msreaderFiles=null, bin=null;
    public ArrayList<MassSpectrum> loadedMS = new ArrayList();
    public MSChrom currentMSC = null;
    public boolean smoothed = false;

    public chromType chromatogramType = chromType.CHROM_TYPE_TIC;
    public smooth smoothType = smooth.NONE;
    public double[] XICrange;
    public MSChrom controlScan;
    public boolean autoBSB = false;
    public boolean autoSmooth = false;
    
    // TODO refactor smoothing type as enum instead of integer
    public static final int SMOOTH_MOVING_AVG = 0;
    public static final int SMOOTH_SAV_GOL = 1;
    
    private SwingWorker worker;
    private PeptideList peptides_;
    
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
        // Cause frame to automatically expand
//        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
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
        if ( hdExchangeInstance == null ) {
            hdExchangeInstance = new HDExchange();
        }
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
    
    ///@brief Sets up paths for MSReader files folder and subfolders
    private void checkOS() {
        try {
            documents = new File (Utils.osjoin(System.getProperty("user.home"), "Documents"));
            msreaderFiles = Utils.osjoin(documents, "MSReader files");
            bin = Utils.osjoin(msreaderFiles, "bin");
            if (!msreaderFiles.exists()) msreaderFiles.mkdir();
            if (!bin.exists()) bin.mkdir();

        } catch ( Exception exc ) {
            Utils.showErrorMessage( "Unable to configure settings");
        }
    }
    
    ///@brief Reads configuration file to get user-specified properties
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
    
    ///@brief Gets peptide list at start up if there is a default list specified,
    ///@brief otherwise creates an empty list
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
    
    ///@brief Creates a new default configuration file
    public void initConfig() {
        properties = new Properties();
        //Set appropriate properties
        properties.setProperty("filter", "5");
        properties.setProperty("SGfilter", "5");
        properties.setProperty("SGdegree", "4");
        properties.setProperty("smoothType", ""+SMOOTH_SAV_GOL);
        properties.setProperty("windowSize", "10");
        properties.setProperty("peptidepath", "");
        properties.setProperty("pymolpath", "");
        properties.setProperty("gradientpath", "");
    
        saveProperties();
    }
    
    ///@brief Save properties to config file in the MSReader files/bin directory
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
    
    ///@brief Returns the value of an integer property
    public int getIntProperty ( String key ) {
        return Integer.parseInt(properties.getProperty(key));
    }
    
    ///@brief Resize window to the right proportions when something is moved
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
        generateHeatMap = new javax.swing.JMenuItem();
        structuralHeatMap = new javax.swing.JMenuItem();
        calcSignificance = new javax.swing.JMenuItem();
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

        viewHDExchangeMenu.setText("Launch HD Exchange");
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

        generateHeatMap.setText("Generate heat map");
        generateHeatMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateHeatMapActionPerformed(evt);
            }
        });
        experimentalMenu.add(generateHeatMap);

        structuralHeatMap.setText("Make structural heat map");
        structuralHeatMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                structuralHeatMapActionPerformed(evt);
            }
        });
        experimentalMenu.add(structuralHeatMap);

        calcSignificance.setText("Calculate significance ");
        calcSignificance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcSignificanceActionPerformed(evt);
            }
        });
        experimentalMenu.add(calcSignificance);

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
     
    ///@brief Create generic charts for start-up
    private void startUpCharts() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = Utils.drawChart( dataset, "Load a CDF or MZML file to begin...","time","" );
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

    ///@brief Repopulate chromatogram based on currently loaded MSChrom object,
    ///@brief add handler to pull up spectrum in the right window
    public final void refreshChromatogram() {
        if (currentMSC == null) {
            startUpCharts();
            return;
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = null;
        
        switch ( chromatogramType ) {
            case CHROM_TYPE_TIC:
                dataset.addSeries(FormatChange.ArrayToXYSeries(currentMSC.TIC));
                chart = Utils.drawChart( dataset, currentMSC.title + " Total Ion Chromatogram",
                        "time(min)","" );
                break;
            case CHROM_TYPE_BPC:
                float[][] bpc = currentMSC.getBPC();
                dataset.addSeries( FormatChange.ArrayToXYSeries(bpc) );
                chart = Utils.drawChart( dataset, currentMSC.title + " Base Peak Chromatogram",
                        "time(min)","" );
                break;
            case CHROM_TYPE_EIC:
                float[][] eic = currentMSC.getEIC(XICrange[0], XICrange[1]);
                dataset.addSeries(FormatChange.ArrayToXYSeries(eic));
                chart = Utils.drawChart( dataset, currentMSC.title + 
                        " EIC Range " + String.format("%.2f",XICrange[0]) + "-" + 
                        String.format("%.2f",XICrange[1]),
                        "time(min)","" );
                break;
        }
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
    
    ///@brief Open chromatogram for viewing
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
        final LoadingDialog ld = new LoadingDialog(this, true);
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
                    spec = FormatChange.ArrayToXYSeries(currentMSC.mz_values, currentMS.yvals);
                } else {
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

    ///@brief Smooth current spectrum based on the user-specified smoothing type
    private void smoothSpectrumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smoothSpectrumActionPerformed
        if (currentMS == null) {
            Utils.showErrorMessage("No MS file loaded");
            return;
        }
        if (smoothed) {
           int choice = JOptionPane.showConfirmDialog(this, "Spectrum has already been smoothed. Do you wish to smooth again?");
           if (choice > 0) return; 
        }
        final LoadingDialog ld = new LoadingDialog(this, true);
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
                    if (currentMSC.SPECTRA_UNIFORM) {
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
    
    ///@brief Open options menu to set user options
    private void smoothingOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_smoothingOptionsActionPerformed
        OptionsMenu so = new OptionsMenu(this, true);
        so.setLocationRelativeTo(this);
        so.setVisible(true);
    }//GEN-LAST:event_smoothingOptionsActionPerformed

    ///@brief Reset the chromatogram to default view
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
        float peakNo = Float.parseFloat(peak.getText());
        float[][] data = new float [2][];
        if ( currentMSC.SPECTRA_UNIFORM ) {
            int windowSize = getIntProperty("windowSize");
            int startIndex = Utils.binarySearch(currentMSC.mz_values, peakNo - windowSize);
            int endIndex = Utils.binarySearch(currentMSC.mz_values, peakNo + windowSize);
            if (startIndex < 0) startIndex = 0; 
            if (endIndex >= currentMSC.mz_values.length) endIndex = currentMSC.mz_values.length - 1;
            data[0] = Arrays.copyOfRange(currentMSC.mz_values, startIndex, endIndex);
            data[1] = Arrays.copyOfRange(currentMS.yvals, startIndex, endIndex);
        } else {
            int windowSize = getIntProperty("windowSize");
            int startIndex = Utils.binarySearch(currentMS.msValues[0], peakNo - windowSize);
            int endIndex = Utils.binarySearch(currentMS.msValues[0], peakNo + windowSize);
            if (startIndex < 0) startIndex = 0; 
            if (endIndex >= currentMS.msValues[0].length) endIndex = currentMS.msValues[0].length - 1; 
            data[0] = Arrays.copyOfRange(currentMS.msValues[0], startIndex, endIndex);
            data[1] = Arrays.copyOfRange(currentMS.msValues[1], startIndex, endIndex);
        }
        
        XYSeries series = FormatChange.ArrayToXYSeries(data);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        String zoomTitle = String.format("peak %.3f", peakNo);

        Peak_Zoom pz = new Peak_Zoom(this, data, zoomTitle);
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
            float time1 = Float.parseFloat(spectrumattime.getText());
            float time2 = Float.parseFloat(jTextField1.getText());
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
            float time = Float.parseFloat(spectrumattime.getText());
            spectrumattime.setText("");
            
            int tIndex = Utils.binarySearch (currentMSC.TIC[0], time);
            if (tIndex >= currentMSC.spectra.length) tIndex = currentMSC.spectra.length - 1;
            currentMS = currentMSC.spectra[tIndex];
        }
        XYSeries spec;
        if (currentMSC.SPECTRA_UNIFORM) {
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
        Peptide current = cpn.getPeptides()[0];
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
        for (int i = 0; i < files.length; i++) {
            hdr[ i ] = Utils.readHDRun( files[ i ] );
            if ( hdr[i] == null ) return;
        }
        if (hdr.length > 15) {
            Utils.showErrorMessage("Cannot overlay more than 15 HDX files at once");
            return;
        }
        HDX_Form HDX = new HDX_Form( hdr );
        HDX.setVisible(true);
    }//GEN-LAST:event_openHDXActionPerformed
    
    private void exportHDX (File[] files, File out) {
        String outstr = "";
        for ( File f: files ) {
            try{
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f)); 
                HDRun hdr = (HDRun)ois.readObject();
                ois.close();
                outstr += hdr.getSummaryString();
            } catch ( Exception exc ) {
                exc.printStackTrace();
            }
        }
        try {
            if (out.exists()) {
                    int opt = JOptionPane.showOptionDialog(null, (Object)out.toString() +" already exists. Overwrite?",
                        "Overwrite file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                        null, new Object[] {"Yes", "No"}, (Object)"Yes");
                    if (opt == 1) { return; }
                    out.delete();
                }
            out.createNewFile(); 
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(outstr.getBytes());
            fos.close();
        } catch ( Exception exc ) { 
            exc.printStackTrace();
        }
        
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
            final File path = new File(savepath.toString() + ".tsv");

            exportHDX( files, path );

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
        final LoadingDialog ld = new LoadingDialog(this, true);
        ld.setLocationRelativeTo(this);
        ld.setText("Converting...");
        ld.setVisible(true);
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                PrintWriter out;
                try {
                    out = new PrintWriter (new FileOutputStream(f));
                    if (currentMSC.SPECTRA_UNIFORM) {
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

    
    ///@brief Performs semi-automatic HD exchange. User selects a folder full of 
    ///@brief data files for each time point, and each one is opened, extracted
    ///@brief for the peptide of interest, trimmed (if the boolean variable auto is
    ///@brief true), and combined to show a full HD exchange plot.  
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
        final Peptide[] pept = cpn.getPeptides();
        if (pept == null) return;
        final LoadingDialog ld = new LoadingDialog(null, true);
        
        // edited to allow for multiple HDExchangeInstances with multiple peptides
        final HDExchange[] hdExchangeInstances = new HDExchange[pept.length];
        for ( int i = 0; i < pept.length; ++i ) {
            hdExchangeInstances[ i ] = new HDExchange();
            hdExchangeInstances[ i ].setPeptide( pept[ i ] );
        }
//        hdExchangeInstance = new HDExchange();
//        hdExchangeInstance.setPeptide( pept );
        
        // Saves current MSC to place back on the screen after running auto hdx
        // May be unnecessary and take up a lot of memory
//        if (currentMSC != null) tempMSC = currentMSC.copy();
        worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws MzMLUnmarshallerException {
                int elutionIndex;
                float [][] eic; 
                String extension = "";
                for ( File f : files ) {
                    System.out.println("Processing "+f.toString());
                    publish( "Processing "+f.getName() );
                    if ( f.toString().toLowerCase().endsWith(".cdf") ) extension = "CDF";
                    else if ( f.toString().toLowerCase().endsWith(".mzml" ) ) extension = "MZML";
                    try {
                        currentMSC = new MSChrom( f, extension );
                    } catch ( MzMLUnmarshallerException exc ) {
                        Utils.showErrorMessage( "Error reading file" );
                        return null;
                    }
                    
                    // Iterate through peptides, extract each one from the currently loaded MSChrom
                    for ( int ii = 0; ii < pept.length; ++ii ) {
                        //If zero time point change XIC parameters
                        if ( Utils.getDeutTimePoint(f.toString()) == 0) {
                            eic = currentMSC.getEIC(pept[ii].mz - 1, pept[ii].mz + 1);
                        }
                        // Set XIC parameters as a function of peptide size and total possible deuterons
                        else {
                            eic = currentMSC.getEIC( pept[ ii ].mz - 1, 
                                    pept[ ii ].mz + pept[ ii ].maxDeuteration()*2/3 );
                        }

                        // TODO - peptide RT should be a float but this messes up my serialized objects
                        elutionIndex = currentMSC.getElutionIndexFromEIC(eic, (float)pept[ii].elutiontime);
                        
                        currentMS = currentMSC.spectra[ elutionIndex ];
                        
                        hdExchangeInstances[ii].addSpectrum( currentMS );
                    }
                    
                    // Deallocate current chromatogram to save space
                    currentMSC = null;
                }
              return null;  
            }
            
            @Override
            protected void process( List<String> message ) {
                ld.setText( message.get( 0 ) );
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

                for ( HDExchange exchange : hdExchangeInstances ) {
                    exchange.analyze();
                    exchange.updateSummary();
                }
//                hdExchangeInstance.analyze();
//                
//                getHDExchangeInstance().updateSummary();
                
                // Cycle through all time points and trim if AutoHDX
                if ( auto ) hdExchangeInstance.trimAllSpectra();
                
                // Restore the chromatogram from the start
//                if ( tempMSC!= null ) currentMSC = tempMSC.copy();
                refreshChromatogram();
                ld.dispose();
                resize();
            }
        };
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
        final Peptide pept = cpn.getPeptides()[0];
        if ( pept == null ) return;
        checkPeptideAccuracy( pept );
    }//GEN-LAST:event_checkPeptideAccuracyActionPerformed

    public void checkPeptideAccuracy ( Peptide pept ) {
        float[][] eic = currentMSC.getEIC(pept.mz - 1, pept.mz + 1);
        // TODO - peptide RT should be in float but this messes up my serialized objects
        int elutionindex = currentMSC.getElutionIndexFromEIC(eic, (float)pept.elutiontime);
        currentMS = currentMSC.spectra[elutionindex];
        currentMS.convertToNonUniform( currentMSC );
        int windowSize = MSReader.getInstance().getIntProperty("windowSize");
        
        float[][] dataRange = currentMS.getWindow( pept.mz, windowSize );
            
        double[][] isotope = pept.getDistribution((int)Math.pow(10, 5), true, true);
        
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
        JFreeChart chart = Utils.drawChart( dataset, pept.displaySequence +
                " ("+String.format("%.2f",currentMS.getRetentionTime())+" RT)",
                "m/z","intensity" );
        
        Peak_Zoom pz = new Peak_Zoom();
        pz.setChart(chart);
        pz.expandGraph();
        pz.setTitle( MSMath.getScore( dataRange, isotope ) + "" );
        pz.setVisible(true);
    }
    
    private void sequenceEngineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sequenceEngineActionPerformed
        SequenceEngine eng = new SequenceEngine(this);
        eng.setLocationRelativeTo(this);
        eng.setVisible(true);
    }//GEN-LAST:event_sequenceEngineActionPerformed
    
    private void generateHeatMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateHeatMapActionPerformed
        ProtectionMapOptions opt = new ProtectionMapOptions(this, true);
        opt.setLocationRelativeTo(this);
        opt.setVisible(true);
        final boolean protection = opt.getExchangeBool();
        boolean HDXinput = opt.getHDXBool();
        if ( !HDXinput ) {
            Utils.showErrorMessage( "You can't run this from an excel file fuckface" );
            return;
        }
        final String seq = opt.getSequence();
        final File outputpath = opt.getOutputPath();
        if ( seq.equals("") ) return;
        if (!outputpath.getParentFile().exists()) {
            Utils.showErrorMessage ("Invalid script output path");
            return;
        }
        
        if (HDXinput) {            
            // If this is a protection map, need to load two sets of HDX files
            if ( protection ) {
                HDRun[] free = loadHDXFiles( "Choose HDX files for free state" );
                if ( free.length == 0 ) return;
                HDRun[] bound = loadHDXFiles( "Choose HDX files for bound state" );
                if ( bound.length == 0 ) return;

                ProtectionMap.createExchangeLines(free, bound, outputpath, seq);
                
            } else {
                HDRun[] hdrs = loadHDXFiles( "Choose HDX files for heat map" );
                if ( hdrs.length == 0 ) return;
                ProtectionMap.createExchangeLines(hdrs, outputpath, seq);
            }
        } 
    }//GEN-LAST:event_generateHeatMapActionPerformed

    private void calcSignificanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcSignificanceActionPerformed
        HDRun[] free = loadHDXFiles( "Choose HDX files for free state" );
        if ( free.length == 0 ) return;
        HDRun[] bound = loadHDXFiles( "Choose HDX files for bound state" );
        if ( bound.length == 0 ) return;

        ProtectionMap.calculateSignificance( free, bound );
    }//GEN-LAST:event_calcSignificanceActionPerformed

    private void structuralHeatMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_structuralHeatMapActionPerformed
        ProtectionMapOptions opt = new ProtectionMapOptions(this, true);
        opt.setLocationRelativeTo(this);
        opt.setVisible(true);
        final boolean protection = opt.getExchangeBool();
        boolean HDXinput = opt.getHDXBool();
        if ( !HDXinput ) {
            Utils.showErrorMessage( "You can't run this from an excel file fuckface" );
            return;
        }
        final String seq = opt.getSequence();
        final File outputpath = opt.getOutputPath();
        if ( seq.equals("") ) return;
        if (!outputpath.getParentFile().exists()) {
            Utils.showErrorMessage ("Invalid script output path");
            return;
        }
        
        if (HDXinput) {            
            // If this is a protection map, need to load two sets of HDX files
            if ( protection ) {
                HDRun[] free = loadHDXFiles( "Choose HDX files for free state" );
                if ( free.length == 0 ) return;
                HDRun[] bound = loadHDXFiles( "Choose HDX files for bound state" );
                if ( bound.length == 0 ) return;

                ProtectionMap.createProtectionMap(free, bound, outputpath, seq);
                
            } else {
                HDRun[] hdrs = loadHDXFiles( "Choose HDX files for heat map" );
                if ( hdrs.length == 0 ) return;
                ProtectionMap.createProtectionMap(hdrs, outputpath, seq);
            }
        } 
    }//GEN-LAST:event_structuralHeatMapActionPerformed

    private HDRun[] loadHDXFiles ( String dialogTitle ) {
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(ExtensionFilter.hdxfilter);
        fileChooser.setDialogTitle( dialogTitle );
        fileChooser.setCurrentDirectory(msreaderFiles);
        fileChooser.setSelectedFile(new File (""));
        fileChooser.setVisible(true);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return new HDRun[] {};
        File[] filegroup = fileChooser.getSelectedFiles();

        HDRun[] hdruns = new HDRun[filegroup.length];
        for (int i = 0; i < filegroup.length; i++) {
            hdruns[ i ] = Utils.readHDRun( filegroup[i] );
        }
        return hdruns;
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
    private javax.swing.JMenuItem calcSignificance;
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
    private javax.swing.JMenuItem generateHeatMap;
    private javax.swing.JButton getSpecAtTime;
    private javax.swing.JMenu hdExchangeMenu;
    private javax.swing.JMenu help;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JMenuItem structuralHeatMap;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JMenuItem viewHDExchangeMenu;
    private javax.swing.JMenuItem viewHelp;
    private javax.swing.JButton zoomToPeak;
    // End of variables declaration//GEN-END:variables

}

