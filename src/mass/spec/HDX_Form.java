package mass.spec;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

public class HDX_Form extends javax.swing.JFrame {

   String title;
   Peptide peptide;
   double a = 0, k = 0;
   MSReader parent;
   DecimalFormat akformat = new DecimalFormat("###.###");
     
   public HDX_Form() {}
   
   public HDX_Form (MSReader msr) {
        super("HDX Form");
        parent = msr;
        initComponents();
        zstate.setText("0");
        residues.setText("0");
   }
   
   public HDX_Form (MSReader msr, Peptide pept) {
        super("HDX Form");
        parent = msr;
        initComponents();
        zstate.setText("0");
        residues.setText("0");
        String[] colNames = {"time(min)", "centroid"};
        DefaultTableModel d = new DefaultTableModel (parent.exchange.getTable(), colNames);
        Utils.dtmsort(d, 0);
        peptide = pept;
        zstate.setText(""+peptide.charge);
        residues.setText(""+peptide.sequence.length());
        Exchange_Popup temp;
        for (Frame f : getFrames()) {
            if (f instanceof Exchange_Popup) {
                temp = (Exchange_Popup)f;
                if (temp.timePoint == 0) jTextField1.setText(new DecimalFormat("##.######").format(temp.score*100) + "%");
            }
        }
        refresh();
    }
    
    public void setPeptide(Peptide p) {
        peptide = p;
        
    }

    public void altOverlay (HDRun[] hdr) {
        expandGraph();
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series;
        for (int i = 0; i < hdr.length; i++) {
            series = new XYSeries(gettitle(hdr[i].title, dataset));
            for (int j = 0; j < hdr[i].exchangeValues.length; j++) {
                series.add((Double)hdr[i].exchangeValues[j][0], (Double)hdr[i].exchangeValues[j][1]);
            } dataset.addSeries(series);
            series = new XYSeries(gettitle("A: "+akformat.format(hdr[i].A)+ " K: "+akformat.format(hdr[i].K), dataset));
            double x = (Double)hdr[i].exchangeValues[hdr[i].exchangeValues.length - 1][0];
            int y = (int)x;
            for (int j = 0; j < y; j++) {
                series.add(j, pointAt(hdr[i].A, hdr[i].K, j));
            } 
            dataset.addSeries(series);
        }
        String charttitle = (hdr.length == 1) ? hdr[0].title : "";
        JFreeChart chart = ChartFactory.createXYLineChart(
                charttitle,
                "time (min)",
                "D/res",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        int counter = 0;
        for (int i = 0; i < dataset.getSeriesCount(); i+=2) {
            renderer.setSeriesLinesVisible(i, false);
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesPaint(i, getColor(counter));
            counter++;
        } 
        counter = 0;
        for (int i = 1; i < dataset.getSeriesCount(); i+=2) {
            renderer.setSeriesLinesVisible(i, true);
            renderer.setSeriesShapesVisible(i, false);
            renderer.setSeriesPaint(i, getColor(counter));
            counter++;
        }
        plot.setRenderer(renderer);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 250));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
    }
    
    private void expandGraph () {
        setLayout(new BorderLayout());
        add(jPanel1, BorderLayout.CENTER);
        setPreferredSize(this.getSize());
        setResizable(true);
        pack();
        zstate.setVisible(false);
        residues.setVisible(false);
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        jTextField1.setVisible(false);
        jScrollPane1.setVisible(false);
        addRegression.setVisible(false);
    }

    private static String gettitle (String title, XYSeriesCollection coll) {
        int index = coll.getSeriesIndex(title);
        while (index >= 0) {
            title += " ";
            index = coll.getSeriesIndex(title);
        } return title;
    }
    
    private Double pointAt (double A, double K, double x) {
        return A*(1-Math.exp(-K*x));
    }
    
    private Color getColor (int index) {
        switch (index) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.RED;
            case 2:
                return Color.black;
            case 3:
                return Color.cyan;
            case 4:
                return Color.LIGHT_GRAY;
            case 5:
                return Color.green;
            case 6:
                return Color.ORANGE;
            case 7:
                return Color.darkGray;
            case 8:
                return Color.MAGENTA;
            case 9:
                return Color.yellow;
            case 10:
                return Color.PINK;
            default:
                return getColor(index-11);
        }
    }
    
    public void setParameters (HDRun hdr) {
        DefaultTableModel d = new DefaultTableModel(hdr.exchangeValues, new String[] {"time(min)", "D/res"});
        jTable1.setModel(d);
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.revalidate();        
        jTable1.setCellSelectionEnabled(true);
        addRegression.setVisible(false);
        XYSeries series;
        XYSeriesCollection dataset = new XYSeriesCollection();
        peptide = hdr.peptide;
        a = hdr.A;
        k = hdr.K;
        zstate.setText(""+peptide.charge);
        residues.setText(""+peptide.sequence.length());
        series = new XYSeries(hdr.title);
        for (int i = 0; i < hdr.exchangeValues.length; i++) {
            series.add((Double)hdr.exchangeValues[i][0], (Double)hdr.exchangeValues[i][1]);
        } dataset.addSeries(series);
        series = new XYSeries ("A: "+akformat.format(hdr.A)+ " K: "+akformat.format(hdr.K));
        for (int i = 0; i < MSMath.getTableXMax(d); i++) {
            series.add(i, pointAt(a, k, i));
        } dataset.addSeries(series);        
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                hdr.peptide.displaySequence, 
                "time(min)", 
                "D/residue", 
                dataset, 
                PlotOrientation.VERTICAL, 
                false, 
                true, 
                false);
        XYPlot plot = (XYPlot)chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesPaint(0, getColor(0));
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesPaint(1, getColor(0));
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRenderer(renderer);
        equation.setText("Equation fitted to data: y = "+akformat.format(a)+"(1-e^(-"+akformat.format(k)+"t))");
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();  
    }
    
    @Override
    public void setTitle(String str) {
        title = str;
    }
    
    public void refresh() {
        DefaultTableModel dtm = new DefaultTableModel (parent.exchange.getTable(), 
                new String[] {"time(min)", "D/r"});
        jTable1.setModel(dtm);
        jTable1.revalidate();
        XYSeries ser = parent.exchange.getXYSeries();
        XYSeriesCollection coll = new XYSeriesCollection();
        coll.addSeries(ser);
        
        final NumberAxis xAxis = new NumberAxis("time(min)");
        final ValueAxis yAxis = new NumberAxis("D/residue");
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        XYPlot plot = new XYPlot (coll, xAxis, yAxis, (XYItemRenderer)renderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        JFreeChart chart = new JFreeChart(peptide.displaySequence, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.setVisible(true);
        jPanel1.revalidate();
    }
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        zstate = new javax.swing.JTextField();
        residues = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        equation = new javax.swing.JLabel();
        addRegression = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        file = new javax.swing.JMenu();
        Save = new javax.swing.JMenuItem();
        update = new javax.swing.JMenuItem();
        remove = new javax.swing.JMenuItem();
        help = new javax.swing.JMenu();
        Help = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Time", "Centroid"
            }
        ));
        jTable1.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(jTable1);

        zstate.setEditable(false);

        residues.setEditable(false);

        jLabel1.setText("Charge state");

        jLabel2.setText("Peptide length");

        addRegression.setText("Add regression line");
        addRegression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRegressionActionPerformed(evt);
            }
        });

        jLabel3.setText("Peptide accuracy score:");

        jTextField1.setEditable(false);

        file.setText("File");

        Save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        Save.setText("Save");
        Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        file.add(Save);

        update.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        update.setText("Update");
        update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });
        file.add(update);

        remove.setText("Remove time point");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        file.add(remove);

        jMenuBar1.add(file);

        help.setText("Help");

        Help.setText("Help menu");
        Help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HelpActionPerformed(evt);
            }
        });
        help.add(Help);

        jMenuBar1.add(help);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(zstate, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(residues, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(equation, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addRegression, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(residues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(equation))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addRegression)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(zstate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void HelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HelpActionPerformed
        Help_Popup hp = new Help_Popup();
        hp.setPreferredSize(new Dimension(700, 500));
        hp.setLocationRelativeTo(this);
        hp.setVisible(true);
    }//GEN-LAST:event_HelpActionPerformed

    private void SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveActionPerformed
        JFileChooser fr = new JFileChooser();
        /*File f = MSReader.root;
        if (!f.exists()) f.mkdir();*/
        fr.setVisible(true);
        fr.setCurrentDirectory(parent.msreaderFiles);
        if (peptide != null) {
        fr.setSelectedFile(new File(peptide.displaySequence)); }
        File savepath;
        String path;
        int returnval = fr.showSaveDialog(this);
        if (returnval == JFileChooser.APPROVE_OPTION) {
            savepath = fr.getSelectedFile();
            path = savepath.toString();
        }
        else return; 
        title = savepath.getName().toString();
        HDRun h = new HDRun(this);
        int opt = -1;
        
        if (path.toUpperCase().contains(".HDX")) {
            path = path.substring(0, path.toLowerCase().indexOf(".hdx"));
        }
        savepath = new File(path + ".hdx");
        if (savepath.exists()) {
            opt = JOptionPane.showOptionDialog(null, path + ".hdx already exists. Overwrite?",
                    "Save HDX", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                    null, new Object[] {"Yes", "No"}, (Object)"Yes");
        }
        if (opt==1) return;
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(savepath));
            oos.writeObject(h);
            oos.close();
        } catch (Exception e) {
            Utils.showErrorMessage("Error: could not write file");
        }
    }//GEN-LAST:event_SaveActionPerformed

    private void updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateActionPerformed
        refresh();
    }//GEN-LAST:event_updateActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        RemoveTimePoint tp = new RemoveTimePoint(this, true);
        tp.setLocationRelativeTo(this);
        tp.setVisible(true);
        refresh();
    }//GEN-LAST:event_removeActionPerformed

    private void addRegressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRegressionActionPerformed
        if (!parent.exchange.hasZeroPt()) Utils.showErrorMessage("Error: can't add regression"
                + " line without a non deuterated time point");
        double[][] data = parent.exchange.getArray();
        double[] values = new Optimizer(data[0], data[1]) {
            @Override
            double function(double A, double K, double xval) {
                return pointAt(A, K, xval);
            }
        }.optimize(.25, .25);
        a = values[0];
        k = values[1];
        XYSeries dataSeries = FormatChange.ArrayToXYSeries(data);
        double[][] fitData = new double[2][(int)MSMath.getMax(data[0])];
        for (int i = 0; i < fitData[0].length; i++) {
            fitData[0][i] = i;
            fitData[1][i] = pointAt(a, k, i);        
        }        
        XYSeries fitSeries = FormatChange.ArrayToXYSeries(fitData);
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(dataSeries);
        final NumberAxis xAxis = new NumberAxis("time(min)");
        final ValueAxis yAxis = new NumberAxis("D/residue");
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        XYPlot plot = new XYPlot (collection, xAxis, yAxis, (XYItemRenderer)renderer);
        XYSeriesCollection lineSeriesData = new XYSeriesCollection();
        final XYLineAndShapeRenderer xyLineRenderer = new XYLineAndShapeRenderer();
        xyLineRenderer.setSeriesShapesVisible(0, false);
        lineSeriesData.addSeries(fitSeries);
        plot.setDataset(1, lineSeriesData);
        plot.setRenderer(1, xyLineRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        JFreeChart chart = new JFreeChart(peptide.displaySequence, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        equation.setText("Equation fitted to data: y = "+akformat.format(a)+"(1-e^(-"+akformat.format(k)+"t))");
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel1.removeAll();
        jPanel1.add(chartPanel);
        jPanel1.revalidate();
    }//GEN-LAST:event_addRegressionActionPerformed
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Help;
    private javax.swing.JMenuItem Save;
    private javax.swing.JButton addRegression;
    private javax.swing.JLabel equation;
    private javax.swing.JMenu file;
    private javax.swing.JMenu help;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JMenuItem remove;
    private javax.swing.JTextField residues;
    private javax.swing.JMenuItem update;
    private javax.swing.JTextField zstate;
    // End of variables declaration//GEN-END:variables

    class RemoveTimePoint extends javax.swing.JDialog {

        DefaultListModel listModel;
        HDX_Form hdxf;

        public RemoveTimePoint(java.awt.Frame parent, boolean modal) {
            super(parent, modal);
            initComponents();
            hdxf = (HDX_Form)parent;
            listModel = new DefaultListModel();
            for (int i = 0; i < hdxf.parent.exchange.key.size(); i++) {
                listModel.addElement(hdxf.parent.exchange.key.get(i));          
            }
            jList1.setModel (listModel);
        }


        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jScrollPane1 = new javax.swing.JScrollPane();
            jList1 = new javax.swing.JList();
            jButton1 = new javax.swing.JButton();
            jButton2 = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            jList1.setModel(new javax.swing.AbstractListModel() {
                String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
                public int getSize() { return strings.length; }
                public Object getElementAt(int i) { return strings[i]; }
            });
            jScrollPane1.setViewportView(jList1);

            jButton1.setText("Remove");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });

            jButton2.setText("Cancel");
            jButton2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(31, 31, 31)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(44, 44, 44)
                            .addComponent(jButton1)
                            .addGap(18, 18, 18)
                            .addComponent(jButton2)))
                    .addContainerGap(29, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton2))
                    .addContainerGap(29, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>

        private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
            dispose();
        }

        private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
            int index = jList1.getSelectedIndex();
            if (index < 0) return;
            String str = (String)jList1.getSelectedValue();
            hdxf.parent.exchange.removeTimePoint(str);
            dispose();
        }

        private javax.swing.JButton jButton1;
        private javax.swing.JButton jButton2;
        private javax.swing.JList jList1;
        private javax.swing.JScrollPane jScrollPane1;
    }
    
    class ColorChooser extends JDialog {
        
	public ColorChooser (Frame parent, boolean modal, HDRun[] hdr, final XYLineAndShapeRenderer renderer, final XYLineAndShapeRenderer rend) {
            super (parent, "Edit Color", modal);
            setPreferredSize(new Dimension(500, 500));
            GridLayout layout = new GridLayout (0, 2, 0, 30);
            setLayout(layout);
            JLabel[] labels = new JLabel [hdr.length];
            final JComboBox[] boxes = new JComboBox [hdr.length];
            final Paint[] colors = {Color.RED, Color.BLUE, Color.black, Color.cyan, Color.LIGHT_GRAY,
                Color.green, Color.ORANGE, Color.darkGray, Color.MAGENTA, Color.yellow}; 
            Object[] COLORS = {"Red", "Blue", "Black", "Cyan", "Light gray",
                "Green", "Orange", "Dark gray", "Magenta", "Yellow"}; 
            int x = 10;
            int y = 10;
            for (int i = 0; i < hdr.length; i++) {
                labels[i] = new JLabel(hdr[i].title);
                labels[i].setPreferredSize(new Dimension (300, 100));
                labels[i].setLocation(new Point(x, y));
                getContentPane().add(labels[i]);
                boxes[i] = new JComboBox (COLORS);
                boxes[i].setSelectedIndex(getIndex (colors, renderer.getSeriesPaint(i)));
                getContentPane().add(boxes[i], BorderLayout.CENTER);
                boxes[i].setSize(new Dimension (10, 10));
                boxes[i].setLocation (new Point(x + 20, y));
                y += 50;
            }
            JButton accept = new JButton ();
            accept.setText("Apply");
            accept.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                        for (int i = 0; i < boxes.length; i++) {
                            int index = boxes[i].getSelectedIndex();
                            renderer.setSeriesPaint(i, colors[index]);
                            rend.setSeriesPaint(i, colors[index]);
                            //dispose();
                        }
                    }
                });
            getContentPane().add(accept);
            y += 50;
            accept.setLocation(x, y);
            JButton cancel = new JButton ();
            cancel.setText("Done");
            cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                        dispose();
                    }
                });
            getContentPane().add(cancel);
            cancel.setLocation(x + 50, y);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
            pack();
            
	}
        
        private int getIndex (Object[] list, Paint color) {
            for (int i = 0; i < list.length; i++) {
                if (color == list[i]) return i;
                else continue;
            }
            return -1;
        }
    }
}
