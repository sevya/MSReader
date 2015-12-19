package mass.spec;

import java.awt.*;
import java.text.DecimalFormat;
import org.jfree.chart.*;
import org.jfree.data.xy.*;

public class Exchange_Popup extends javax.swing.JFrame {
    final HDExchangeTimePoint parent;
    
    public Exchange_Popup ( HDExchangeTimePoint par ) {
        super ("HD Exchange");
        initComponents();
        parent = par;
        centroid.setText( parent.getCentroidString() );
        updateAll();
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

    private void updateAll () {
        updateTable();
        updateData();
    }
    private void updateData () {
        XYSeries ser = parent.getDataAsXYSeries();
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ser);
        String chartTitle = parent.getPeptide().displaySequence+" "+
                Double.toString( parent.getTimePoint() )+"min"+
                " ("+String.format("%.2f",parent.getRetentionTime())+" RT)";
        JFreeChart chart = Utils.drawChart( dataset, chartTitle, "m/z", "intensity" );
        
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(785, 300));
        jPanel1.removeAll();
        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.revalidate();
        
    }
    
    private void updateTable () {
        jTable1.setModel( parent.getDataAsTable() );
        jScrollPane1.setViewportView(jTable1);
        jScrollPane1.revalidate();    
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
        jButton1 = new javax.swing.JButton();
        removeTimePoint = new javax.swing.JButton();

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

        trim.setText("Auto-trim");
        trim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trimActionPerformed(evt);
            }
        });

        jButton1.setText("Undo delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        removeTimePoint.setText("Remove time point");
        removeTimePoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTimePointActionPerformed(evt);
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
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(centroid, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 112, Short.MAX_VALUE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(trim)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(removeTimePoint)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(centroid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(delete)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(trim))
                .addGap(5, 5, 5)
                .addComponent(removeTimePoint))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        int start = jTable1.getSelectedRow();
        int len = jTable1.getSelectedRowCount();
        
        parent.deleteRows( start, len );
        
        centroid.setText( parent.getCentroidString() );
        updateAll();
    }//GEN-LAST:event_deleteActionPerformed

    
    private void trimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trimActionPerformed
        parent.trim();
        centroid.setText( parent.getCentroidString() );
        updateAll();
    }//GEN-LAST:event_trimActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        parent.undoDelete();
        centroid.setText( parent.getCentroidString() );
        updateAll();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void removeTimePointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTimePointActionPerformed
        // TODO write code to remove this time point
        parent.removeTimePoint();
        this.dispose();
    }//GEN-LAST:event_removeTimePointActionPerformed
    
    public void setError(String str) {
        jLabel2.setText(str);
    }
    
   
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField centroid;
    private javax.swing.JButton delete;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton removeTimePoint;
    private javax.swing.JButton trim;
    // End of variables declaration//GEN-END:variables
}
