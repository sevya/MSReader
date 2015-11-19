package mass.spec;

import java.awt.event.*;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

public class OptionsMenu extends javax.swing.JDialog {
    int smoothType;
    MSReader msr;
    
    public OptionsMenu(java.awt.Frame parent, boolean modal) {
        super(parent, "Processing Options", modal);
        initComponents();
        msr = MSReader.getInstance();
        smoothType = msr.getIntProperty("smoothType");
        refresh();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        closeDialog = new javax.swing.JButton();
        acceptChanges = new javax.swing.JButton();
        dfault = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        autosmooth = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        filterInput = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        filterSG = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        degreeSG = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        windowsize = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Processing Options");
        setResizable(false);

        closeDialog.setText("Cancel");
        closeDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeDialogActionPerformed(evt);
            }
        });

        acceptChanges.setText("OK");
        acceptChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptChangesActionPerformed(evt);
            }
        });

        dfault.setText("Restore to defaults");
        dfault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dfaultActionPerformed(evt);
            }
        });

        jLabel3.setText("Smoothing Options");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Moving Average Smoothing");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Savitzky-Golay Smoothing");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Gaussian Smoothing");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        autosmooth.setText("Smooth spectrum in auto HDX");
        autosmooth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autosmoothActionPerformed(evt);
            }
        });

        jLabel2.setText("Filter Size");

        jLabel4.setText("Filter Size");

        jLabel5.setText("Polynomial degree");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(autosmooth)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButton2)
                                    .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(55, 55, 55)
                                .addComponent(jLabel4)
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel5))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(filterInput, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                            .addComponent(filterSG)
                            .addComponent(degreeSG))))
                .addContainerGap(266, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(64, 64, 64)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(filterInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton2)
                    .addComponent(jLabel4)
                    .addComponent(filterSG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(degreeSG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton3)
                .addGap(56, 56, 56)
                .addComponent(autosmooth)
                .addGap(76, 76, 76))
        );

        jTabbedPane1.addTab("Smoothing", jPanel1);

        jLabel1.setText("Background Subtraction");

        jCheckBox1.setText("Subtract background automatically after loading");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton1.setText("Choose control scan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton1))
                .addContainerGap(328, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(61, 61, 61)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(223, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Background Subtraction", jPanel2);

        jLabel6.setText("Exchange Options");

        jLabel7.setText("Window size");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(windowsize, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(402, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(76, 76, 76)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(windowsize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(241, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Exchange", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 344, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Advanced", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(acceptChanges)
                .addGap(31, 31, 31)
                .addComponent(closeDialog)
                .addGap(41, 41, 41)
                .addComponent(dfault)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeDialog)
                    .addComponent(acceptChanges)
                    .addComponent(dfault))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refresh() {
        switch (smoothType) {
            case 0:
                jRadioButton1.setSelected(true);
                filterInput.setEditable(true);
                filterSG.setEditable(false);
                degreeSG.setEditable(false);
                break;
            case 1:
                jRadioButton2.setSelected(true);
                filterSG.setEditable(true);
                degreeSG.setEditable(true);
                filterInput.setEditable(false);
                break;
            case 2:
                jRadioButton2.setSelected(true);
                filterInput.setEditable(false);
                filterSG.setEditable(false);
                degreeSG.setEditable(false);
                break;
        }
        jButton1.setEnabled(jCheckBox1.isSelected());
        autosmooth.setSelected(msr.autoSmooth);
        jCheckBox1.setSelected(msr.autoBSB);
        windowsize.setText(msr.getProperty("windowSize"));
        filterInput.setText(msr.getProperty("filter"));
        filterSG.setText(msr.getProperty("SGfilter"));
        degreeSG.setText(msr.getProperty("SGdegree"));
    }
    private void closeDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeDialogActionPerformed
        dispose();
    }//GEN-LAST:event_closeDialogActionPerformed

    private void acceptChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptChangesActionPerformed
        if (jRadioButton1.isSelected()) {
            String input = filterInput.getText();
            if (!MSMath.isInt(input)) {
                JOptionPane.showMessageDialog(null, "Not a valid filter size", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            msr.setProperty("filter", input);
            msr.setProperty("smoothType", ""+MSReader.SMOOTH_MOVING_AVG);
        } else if (jRadioButton2.isSelected()) {
            String input1 = filterSG.getText();
            String input2 = degreeSG.getText();
            if (!MSMath.isInt(input1)) {
                Utils.showErrorMessage("Not a valid filter size");
                return;
            }
            if (!MSMath.isInt(input2)) {
                Utils.showErrorMessage("Not a valid polynomial degree");
                return;
            }
            msr.setProperty("SGfilter", input1);
            msr.setProperty("SGdegree", input2);
            msr.setProperty("smoothType", ""+MSReader.SMOOTH_SAV_GOL);
        }
        
        if (jCheckBox1.isSelected()) {
            msr.autoBSB = true;
            if (msr.controlScan==null) {
                Utils.showMessage("To do background subtraction you need to select a control scan");
                return;
            }
        } else msr.autoBSB = false; 
        msr.autoSmooth = autosmooth.isSelected();
        
        if (MSMath.isInt(windowsize.getText())) {
            msr.setProperty("windowSize", windowsize.getText());
        } else {
            Utils.showErrorMessage("Invalid input for window size");
            return;
        }
        
        dispose();
    }//GEN-LAST:event_acceptChangesActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        smoothType = 1;
        refresh();
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        Utils.showMessage("This feature is not ready yet...");
        if (smoothType == 1) jRadioButton2.setSelected(true);
        else jRadioButton1.setSelected(true);
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        BackgroundSubDialog s  = new BackgroundSubDialog(msr, true);
        s.setLocationRelativeTo((java.awt.Frame)this.getParent());
        s.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        msr.autoBSB = jCheckBox1.isSelected();
        refresh();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        smoothType = 0;
        refresh();
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void autosmoothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autosmoothActionPerformed
        msr.autoSmooth = autosmooth.isSelected();
    }//GEN-LAST:event_autosmoothActionPerformed

    private void dfaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dfaultActionPerformed
        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to restore all values to default? "
                + "You will not be able to recover your settings", 
                "Restore defaults", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) msr.initConfig(); 
        refresh();
    }//GEN-LAST:event_dfaultActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptChanges;
    private javax.swing.JCheckBox autosmooth;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton closeDialog;
    private javax.swing.JTextField degreeSG;
    private javax.swing.JButton dfault;
    private javax.swing.JTextField filterInput;
    private javax.swing.JTextField filterSG;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField windowsize;
    // End of variables declaration//GEN-END:variables

 class BackgroundSubDialog extends javax.swing.JDialog implements MouseListener {
    
    File file = null;
    DefaultListModel mylist;
    MSReader msr;

    
    public BackgroundSubDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        msr = (MSReader)parent;
        initComponents();
        mylist = new DefaultListModel();
        updateList();
    }

    private void updateList() {
        mylist.clear();
        if (msr.controlScan!=null) {
            mylist.addElement(msr.controlScan.path);
            file = new File(msr.controlScan.path);
        }
        jList1.setModel(mylist);
        if (file!=null) {
            jList1.setSelectedIndex(0); 
        }
        jButton2.setEnabled(file != null);
        jList1.revalidate();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Please select control scan to use for background subtraction:");

        jButton1.setText("Open file chooser");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("OK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(81, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        JFileChooser fc = new JFileChooser();
        fc.setVisible(true);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            final LoadingDialog ld = new LoadingDialog(null, false);
            SwingWorker worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    msr.controlScan = new MSChrom(file);
                    return null;
                }
                @Override
                protected void done() {
                    updateList();
                    ld.dispose();
                }
            };
            ld.setLocationRelativeTo(this);
            ld.setVisible(true);
            worker.execute();
        }
    }                                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (jList1.getSelectedIndex() == -1) Utils.showMessage("Select a control scan to continue");
        else dispose(); 
    }                                        

    public boolean fileLoaded() {
        return file != null;
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (jList1.getSelectedIndex() >-1) {
            jButton2.setEnabled(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
}
