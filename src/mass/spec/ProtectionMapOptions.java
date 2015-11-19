package mass.spec;

import java.io.File;
import java.util.Collections;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Structure;
import org.biojava3.structure.StructureIO;

public class ProtectionMapOptions extends javax.swing.JDialog {

    private File output;
    
    public ProtectionMapOptions(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        outputpath.setText(Utils.osjoin(MSReader.getInstance().bin, "heatmap.py").toString());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        ok = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        exchange = new javax.swing.JRadioButton();
        protection = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        hdx = new javax.swing.JRadioButton();
        excel = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sequenceInput = new javax.swing.JTextArea();
        pdbFileBrowser = new javax.swing.JButton();
        pdbInput = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        outputpath = new javax.swing.JTextField();
        setOutputPath = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Heat Map Options");
        setResizable(false);

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

        jLabel2.setText("Are you measuring total exchange or protection?");

        buttonGroup1.add(exchange);
        exchange.setSelected(true);
        exchange.setText("Exchange");
        exchange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exchangeActionPerformed(evt);
            }
        });

        buttonGroup1.add(protection);
        protection.setText("Protection");

        jLabel1.setText("Are exchange values stored in .hdx files, or in an Excel document?");

        buttonGroup2.add(hdx);
        hdx.setSelected(true);
        hdx.setText("HDX files");

        buttonGroup2.add(excel);
        excel.setText("Excel");
        excel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 286, Short.MAX_VALUE)
                        .addGap(216, 216, 216))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(exchange)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(excel)
                            .addComponent(protection, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(hdx))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exchange)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(protection)
                .addGap(57, 57, 57)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hdx)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(excel)
                .addContainerGap(120, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Exchange options", jPanel2);

        jLabel4.setText("Input protein sequence:");

        sequenceInput.setColumns(20);
        sequenceInput.setLineWrap(true);
        sequenceInput.setRows(5);
        jScrollPane1.setViewportView(sequenceInput);

        pdbFileBrowser.setText("Open file browser");
        pdbFileBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdbFileBrowserActionPerformed(evt);
            }
        });

        jLabel3.setText("Choose PDB File");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pdbFileBrowser)
                    .addComponent(jLabel4)
                    .addComponent(pdbInput, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pdbInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pdbFileBrowser)
                .addGap(38, 38, 38)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Sequence", jPanel1);

        jLabel5.setText("Path to output color script:");

        setOutputPath.setText("Open file browser");
        setOutputPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setOutputPathActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(setOutputPath)
                    .addComponent(outputpath, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(93, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(setOutputPath)
                .addContainerGap(204, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Advanced", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Color gradient", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ok, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancel)))
                .addGap(0, 11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ok)
                    .addComponent(cancel))
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("PDB");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        if ( pdbInput.getText().equals("") && sequenceInput.getText().equals("") ) {
            Utils.showErrorMessage( "Error: please input sequence either from a text file or PDB");
        }
        dispose();
    }//GEN-LAST:event_okActionPerformed
    
    public boolean getExchangeBool () {
        return protection.isSelected();
    }
    
    public boolean getHDXBool () {
        return hdx.isSelected();
    }
    
    public String getSequence() {
        String str;
        if ( sequenceInput.getText().equals("") ) { // Default to string entry - if none pull from PDB
            str = getSequenceFromPDB( pdbInput.getText() );
        } else {
            str = sequenceInput.getText();
        }
  
        return str.replaceAll("\\s+", "");
    }
    
    public File getOutputPath() {
        String ex = outputpath.getText();
        if (ex.toLowerCase().contains(".py")) {
            return new File (ex);
        } else {
            return new File (ex + ".py");
        }
    }
    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_cancelActionPerformed

    private void setOutputPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setOutputPathActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory( MSReader.getInstance().documents );
        fileChooser.setVisible(true);
        fileChooser.setSelectedFile(new File(""));
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.CANCEL_OPTION) return;
        File choice = fileChooser.getSelectedFile();
        outputpath.setText(choice.toString());
    }//GEN-LAST:event_setOutputPathActionPerformed

    private void pdbFileBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdbFileBrowserActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(ExtensionFilter.pdbfilter);
        fileChooser.setCurrentDirectory( MSReader.getInstance().documents );
        fileChooser.setVisible(true);
        fileChooser.setSelectedFile(new File(""));
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        pdbInput.setText( fileChooser.getSelectedFile().toString() );
    }//GEN-LAST:event_pdbFileBrowserActionPerformed

    private void excelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_excelActionPerformed

    private void exchangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exchangeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_exchangeActionPerformed

    private String getSequenceFromPDB( String pdbfile ) {
        try {
            Structure struct = StructureIO.getStructure(pdbfile);
            String seq = "";
            for ( Chain c : struct.getChains()) {
                seq += c.getSeqResSequence();
            }
            return seq;
        } catch ( Exception exc ) {
            Utils.showErrorMessage(exc.toString());
            return null;
        }
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProtectionMapOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProtectionMapOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProtectionMapOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProtectionMapOptions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProtectionMapOptions dialog = new ProtectionMapOptions(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancel;
    private javax.swing.JRadioButton excel;
    private javax.swing.JRadioButton exchange;
    private javax.swing.JRadioButton hdx;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton ok;
    private javax.swing.JTextField outputpath;
    private javax.swing.JButton pdbFileBrowser;
    private javax.swing.JTextField pdbInput;
    private javax.swing.JRadioButton protection;
    private javax.swing.JTextArea sequenceInput;
    private javax.swing.JButton setOutputPath;
    // End of variables declaration//GEN-END:variables
}
