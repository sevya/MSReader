package mass.spec;

import javax.swing.JOptionPane;


public class Chromatogram_Options extends javax.swing.JDialog {
    MSReader msr;
    
    public Chromatogram_Options(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        msr = (MSReader)parent;
        initComponents();
        switch (msr.chromatogramType) {
            case 0:
                TIC.setSelected(true);
                break;
            case 1: 
                BPC.setSelected(true);
                break;
            case 2:
                XIC.setSelected(true);
                jTextField1.setEditable(true);
                jTextField2.setEditable(true);
                break;
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        TIC = new javax.swing.JRadioButton();
        BPC = new javax.swing.JRadioButton();
        XIC = new javax.swing.JRadioButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ok = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Chromatogram Options");
        setResizable(false);

        buttonGroup1.add(TIC);
        TIC.setText("Total Ion Chromatogram");
        TIC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TICActionPerformed(evt);
            }
        });

        buttonGroup1.add(BPC);
        BPC.setText("Base Peak Chromatogram");
        BPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BPCActionPerformed(evt);
            }
        });

        buttonGroup1.add(XIC);
        XIC.setText("Extracted Ion Chromatogram");
        XIC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XICActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);

        jTextField2.setEditable(false);

        jLabel1.setText(" :");

        jLabel2.setText("Ion Range");

        jLabel3.setText("Select Type of Chromatogram:");

        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        jLabel4.setText("(input one value to view single ion EIC)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BPC)
                    .addComponent(TIC)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(XIC)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(ok, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(cancel)))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(102, 102, 102))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(TIC)
                .addGap(38, 38, 38)
                .addComponent(BPC)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(XIC))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ok)
                    .addComponent(cancel))
                .addGap(40, 40, 40))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        if (TIC.isSelected()) {
            msr.chromatogramType = MSReader.CHROM_TYPE_TIC;
        }
        else if (BPC.isSelected()) {
            msr.chromatogramType = MSReader.CHROM_TYPE_BPC;
        }
        else if (XIC.isSelected()) {
            msr.chromatogramType = MSReader.CHROM_TYPE_EIC;
            double [] x = getXICRange();
            if (x == null || x[1] < x[0]) {
                Utils.showErrorMessage("Not a valid input");
                return;
            }
            msr.XICrange = getXICRange();
        }
        this.dispose();
    }//GEN-LAST:event_okActionPerformed

    public double[] getXICRange () {
        double[] values = new double[2];
        try {
            values[0] = Double.parseDouble(jTextField1.getText());
        } catch (Exception e) {
            return null;
        }
        try {
            values[1] = Double.parseDouble(jTextField2.getText());
        } catch (Exception e) {
            values[0] -= 2;
            values[1] = values[0] + 4;
            return values;
        }
        return values;
    }
    
    
    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_cancelActionPerformed

    private void XICActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XICActionPerformed
        jTextField1.setEditable(true);
        jTextField2.setEditable(true);
    }//GEN-LAST:event_XICActionPerformed

    private void BPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BPCActionPerformed
        jTextField1.setEditable(false);
        jTextField2.setEditable(false);
    }//GEN-LAST:event_BPCActionPerformed

    private void TICActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TICActionPerformed
        jTextField1.setEditable(false);
        jTextField2.setEditable(false);
    }//GEN-LAST:event_TICActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton BPC;
    private javax.swing.JRadioButton TIC;
    private javax.swing.JRadioButton XIC;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton ok;
    // End of variables declaration//GEN-END:variables
}
