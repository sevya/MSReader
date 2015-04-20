package mass.spec;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;

public class Exchange_Popup extends javax.swing.JFrame {
    String title;
    Peptide peptide;
    double[][] data;
    double timePoint;
    double cent;
    DecimalFormat myformat = new DecimalFormat ("####.###");
    double score;
    
    public Exchange_Popup ( double[][] dat, Peptide p, String tit ) {
        super ("HD Exchange");
        initComponents();
        data = dat;
        peptide = p;
        jTable1.setModel(new DefaultTableModel (FormatChange.ArrayToTable(data), new String[] {"m/z", "intensity"}));
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.revalidate();    
        title = tit;
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(FormatChange.ArrayToXYSeries(data));
        JFreeChart chart = ChartFactory.createXYLineChart (
                title,
                "m/z",
                "intensity",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
                );
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(785, 300));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();
        timePoint = Utils.getDeutTimePoint(title);
        if (timePoint == -1) Utils.showErrorMessage("Error in reading time points. "
                    + "Make sure that the files are properly named");
            
        cent = MSMath.calcCentroid(data);
        centroid.setText(myformat.format(cent));
    }

    public void expandGraph() {
        setLayout(new BorderLayout());
        add(jPanel1, BorderLayout.CENTER);
        jScrollPane1.setVisible(false);
        delete.setVisible(false);
        trim.setVisible(false);
        jLabel1.setVisible(false);
        centroid.setVisible(false);
        setPreferredSize(this.getSize());
        setResizable(true);
        pack();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        delete = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        centroid = new javax.swing.JTextField();
        trim = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

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
                "M/z", "intensity"
            }
        ));
        jTable1.setCellSelectionEnabled(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        delete.setText("Delete");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        jLabel1.setText("Centroid:");

        centroid.setEditable(false);

        trim.setText("Trim");
        trim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trimActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(delete)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(centroid, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 18, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(trim)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(centroid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delete)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(trim))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        int start = jTable1.getSelectedRow();
        int len = jTable1.getSelectedRowCount();
        ArrayList<Double> tempx = new ArrayList();
        ArrayList<Double> tempy = new ArrayList();
        for (int i = 0; i < data[0].length; i++) {
            if (i >= start && i < start+len) continue;
            else {
                tempx.add(data[0][i]);
                tempy.add(data[1][i]);
            }
        }
        data[0] = FormatChange.ArraylistToArray(tempx);
        data[1] = FormatChange.ArraylistToArray(tempy);
        jTable1.setModel(new DefaultTableModel (FormatChange.ArrayToTable(data), new String[] {"m/z", "intensity"}));
        cent = MSMath.calcCentroid(data);
        centroid.setText(myformat.format(cent));
        MSReader.getInstance().setExchangePt(title, cent);
        XYSeries ser = FormatChange.ArrayToXYSeries(data);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ser);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "m/z",
                "intensity",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
                );
        
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(785, 300));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();
        updateHDX();
    }//GEN-LAST:event_deleteActionPerformed

    private void updateHDX() {
        HDX_Form hdx = null;
        Frame[] frames = Frame.getFrames();
        for (Frame f: frames) {
            if (f instanceof HDX_Form) {
                hdx = (HDX_Form)f;
                break;
            }
        }
        hdx.refresh();
    }
    
    private void trimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trimActionPerformed
        cent = trim(); 
        MSReader.getInstance().setExchangePt(title, cent);
        updateHDX();
    }//GEN-LAST:event_trimActionPerformed

    
    public void setError(String str) {
        jLabel2.setText(str);
    }
    
    public double trim () {
        try {
            int[] indices = peakDetector();
            data[0] = Arrays.copyOfRange(data[0], indices[0], indices[1]);
            data[1] = Arrays.copyOfRange(data[1], indices[0], indices[1]);
        } catch (NoPeakDetectedException e) {
            setError(e.getMessage());
        }
        jTable1.setModel(new DefaultTableModel (FormatChange.ArrayToTable(data), new String[] {"m/z", "intensity"}));
        cent = MSMath.calcCentroid(data);
        centroid.setText(myformat.format(cent));
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(FormatChange.ArrayToXYSeries(data));
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "m/z",
                "intensity",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
                );
        
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(785, 300));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();
        return cent;
    }
    
     private int[] peakDetector () throws NoPeakDetectedException {
        double[][] tope = peptide.getThreadedDistribution((int)Math.pow(10, 5));
        Utils.sort2DArray (tope, 0);
        int endindex = tope[0].length-1;
        for (int i = 0; i < tope[0].length; i++) {
            if (tope[1][i] > .005) endindex = i;
        }
        double max = MSMath.getMax(data[1]);
        max /= MSMath.getMax(tope[1]);
        for (int i = 0; i < tope[1].length; i++) tope[1][i] = tope[1][i] * max;
        
        if (timePoint != 0) {
            double shift = getShift(data, new double[][] {tope[0].clone(), tope[1].clone()});
            for (int i = 0; i < tope[0].length; i++) tope[0][i] += shift;
        }
        score = MSMath.getScore(data, tope);
        setTitle(score+"");
        
        if (score < .5) throw new NoPeakDetectedException("No peak detected - does not match input peptide");
        int start = Utils.binarySearch (data[0], tope[0][0]);
        
        int beforeindex = 0;
        max = MSMath.getMax(data[1]);
        double lowerthresh = (max > 10000) ? 500 : max*.05;
        for (int i = start; i > 0; i--) {
            if (data[1][i] < lowerthresh) {
                beforeindex = i-1;
                break;
            } 
        }
        
        int after = Utils.binarySearch (data[0], tope[0][endindex]);
        int afterindex = data[0].length;
        for (int i = after; i < data[0].length; i++) {
            if (data[1][i] < lowerthresh) {
                afterindex = i+1;
                break;
            }
        }
        if (MSMath.getMax(data[1], beforeindex, afterindex) < 5000) throw new NoPeakDetectedException("No peak detected - signal too weak");
        
        return new int[] {beforeindex, afterindex};
    } 
     
     private double getShift (double[][] data, double[][] isotope) {
        int counter = 1;
        double score = MSMath.getScore(data, isotope);
        double maxscore = score;
        double maxshift = 0;
        double shift = 1/(double)peptide.charge;
        while (isotope[0][isotope[0].length-1] < data[0][data[0].length-1]) {
            for (int i = 0; i < isotope[0].length; i++) isotope[0][i] += shift;
            score = MSMath.getScore(data, isotope);
            if (score > maxscore) {
                maxscore = score;
                maxshift = (double)counter*shift;
            }
            counter++;
        }
        return maxshift;
     }
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField centroid;
    private javax.swing.JButton delete;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton trim;
    // End of variables declaration//GEN-END:variables
}
