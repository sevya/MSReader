package mass.spec;

import javax.swing.*;

public class HDExchangeSetup extends javax.swing.JDialog {
    DefaultListModel listModel = new DefaultListModel();
    boolean addSpectrum = true;
    
    public HDExchangeSetup( boolean modal) {
        super ( MSReader.getInstance(), "HD Exchange Setup", modal);
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        refreshList();
        if ( addSpectrum ) {
            if (MSReader.getInstance().currentMS == null) return;
            jTextField1.setText( MSReader.getInstance().currentMS.getFullTitle() );
        }
    }
    
    private void refreshList() {
        listModel.clear();
        try {
            for ( String title : MSReader.getHDExchangeInstance().getExchangeSpectraTitles() ) {
                listModel.addElement( title );
            }
            spectraList.setModel( listModel );
        } catch (Exception exc) {
            Utils.logException( exc, "Error populating list of loaded spectra");
        }        
    }
    
    public void addOptionsState(boolean b) {
        addSpectrum = b;
        if (!b) {
            listModel.clear();
            jTextField1.setText("");
        }
        refreshList();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        addFile = new javax.swing.JButton();
        done = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        spectraList = new javax.swing.JList();
        removeFile = new javax.swing.JButton();
        clearFiles = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        instanceList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel2.setText("Files:");

        addFile.setText("Add file");
        addFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileActionPerformed(evt);
            }
        });

        done.setText("Done");
        done.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(spectraList);

        removeFile.setText("Remove file");
        removeFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFileActionPerformed(evt);
            }
        });

        clearFiles.setText("Clear all files");
        clearFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFilesActionPerformed(evt);
            }
        });

        instanceList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(instanceList);

        jLabel3.setText("Instances:");

        jButton1.setText("New instance");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(clearFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeFile, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(done))
                            .addComponent(addFile, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(removeFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearFiles)))
                        .addGap(30, 30, 30)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(done)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileActionPerformed
        if ( jTextField1.getText().equals("") ) { return; }
        
        MSReader.getInstance().currentMS.convertToNonUniform( MSReader.getInstance().currentMSC );
        
        MSReader.getHDExchangeInstance().addSpectrum( MSReader.getInstance().currentMS );
        
        listModel.addElement(jTextField1.getText());
        spectraList.setModel(listModel);
        jTextField1.setText("");
        
    }//GEN-LAST:event_addFileActionPerformed

    private void doneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneActionPerformed
        this.dispose();
    }//GEN-LAST:event_doneActionPerformed

    private void removeFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFileActionPerformed
        String title = ( String )spectraList.getSelectedValue();
        System.out.println( title );
        MSReader.getHDExchangeInstance().removeSpectrum( title );
        refreshList();
    }//GEN-LAST:event_removeFileActionPerformed

    private void clearFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFilesActionPerformed
        MSReader.getHDExchangeInstance().removeAllSpectra();
        refreshList();
    }//GEN-LAST:event_clearFilesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFile;
    private javax.swing.JButton clearFiles;
    private javax.swing.JButton done;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JList instanceList;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton removeFile;
    private javax.swing.JList spectraList;
    // End of variables declaration//GEN-END:variables
}
