package mass.spec;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

public class Peak_Zoom extends javax.swing.JFrame {

    private float[][] data;
    private String title;
    
    public Peak_Zoom() {
        initComponents();
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public Peak_Zoom (MSReader msr, float[][] array, String name) {
        super ("Zoom");
        title = name;
        data = array;
        initComponents();
        updateTable();
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.revalidate();        
        jTable1.setCellSelectionEnabled(true);

        XYSeries ser = FormatChange.ArrayToXYSeries(data);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ser);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "m/z",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
                );
        
        ChartPanel chartPanel = new ChartPanel (chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();
        double cent = MSMath.calcCentroid(data);
        centroid.setText(String.format("%.3f", cent));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void setName(String str) {
        title = str;
    }
    
    public void setChart (JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel (chart);
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();
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
    
      private int[] peakDetector () throws NoPeakDetectedException {
        ChoosePeakNo cpn = new ChoosePeakNo(MSReader.getInstance(), true);
        cpn.setLocationRelativeTo(this);
        cpn.setVisible(true);
        final Peptide peptide = cpn.getPeptides()[0];
        double[][] tope = peptide.getDistribution((int)Math.pow(10, 5), true, true);
        int endindex = tope[0].length-1;
        for (int i = 0; i < tope[0].length; i++) {
            if (tope[1][i] > .005) endindex = i;
        }
        double max = MSMath.getMax(data[1]);
        max /= MSMath.getMax(tope[1]);
        for (int i = 0; i < tope[1].length; i++) tope[1][i] = tope[1][i] * max;
        
        double shift = getShift(data, new double[][] {tope[0].clone(), tope[1].clone()}, peptide);
        for (int i = 0; i < tope[0].length; i++) tope[0][i] += shift;
        double score = MSMath.getScore(data, tope);
        setTitle(score+"");
        
        if (score < .5) throw new NoPeakDetectedException("No peak detected - does not match input peptide");
        
        int start = Utils.binarySearch (FormatChange.Float2Double(data[0]), tope[0][0]);
        int beforeindex = 0;
        max = MSMath.getMax(data[1]);
        double lowerthresh = (max > 10000) ? 500 : max*.05;
        if (start < 0) start = -(start+1);
        for (int i = start; i > 0; i--) {
            if (data[1][i] < lowerthresh) {
                beforeindex = i-1;
                break;
            } 
        }
        
        int after = Utils.binarySearch (FormatChange.Float2Double(data[0]), tope[0][endindex]);
        int afterindex = data[0].length;
        for (int i = after; i < data[0].length; i++) {
            if (data[1][i] < lowerthresh) {
                afterindex = i+1;
                break;
            }
        }
        
        return new int[] {beforeindex, afterindex};
    } 
     
     private double getShift (float[][] data, double[][] isotope, Peptide peptide) {
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
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

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
                "M/z", "Intensity"
            }
        ));
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

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(trim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(13, 13, 13)
                        .addComponent(centroid, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(182, 535, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trim)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(centroid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(31, 31, 31))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        int start = jTable1.getSelectedRow();
        int len = jTable1.getSelectedRowCount();
        ArrayList<Float> tempx = new ArrayList();
        ArrayList<Float> tempy = new ArrayList();
        for (int i = 0; i < data[0].length; i++) {
            if (i >= start && i < start+len) continue;
            else {
                tempx.add(data[0][i]);
                tempy.add(data[1][i]);
            }
        }
        
        updateTable();

        double cent = MSMath.calcCentroid(data);
        centroid.setText(String.format("%.3f", cent));
        XYSeries ser = FormatChange.ArrayToXYSeries(data);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ser);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "m/z",
                "",
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
    }//GEN-LAST:event_deleteActionPerformed

    private void updateTable() {
        DefaultTableModel table = new DefaultTableModel( FormatChange.ArrayToTable(
            data, new DecimalFormat[] { new DecimalFormat("###.##"), new DecimalFormat("0.0E0")} ),
            new String[] {"m/z", "intensity"});
        jTable1.setModel( table );
    }
    private void trimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trimActionPerformed
        try {
            int[] indices = peakDetector();  
            data[0] = Arrays.copyOfRange(data[0], indices[0], indices[1]);
            data[1] = Arrays.copyOfRange(data[1], indices[0], indices[1]);
        } catch (NoPeakDetectedException e) {
            Utils.showMessage(e.getMessage());
        }
        updateTable();
        double cent = MSMath.calcCentroid( data );
        centroid.setText(String.format("%.3f", cent));

        XYSeries ser = FormatChange.ArrayToXYSeries(data);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ser);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "m/z",
                "",
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
    }//GEN-LAST:event_trimActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField centroid;
    private javax.swing.JButton delete;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton trim;
    // End of variables declaration//GEN-END:variables
}
