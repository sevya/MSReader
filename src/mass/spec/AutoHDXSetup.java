package mass.spec;

import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class AutoHDXSetup extends javax.swing.JDialog {
    DefaultListModel model;
    JFileChooser fileChooser;
    File currentFile;
    
    
    public AutoHDXSetup(java.awt.Frame parent, boolean modal) {
        super(parent, "Choose files for Auto HDX", modal);
        initComponents();
        model = new DefaultListModel();
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory( MSReader.getInstance().msreaderFiles );
        jList1.setModel(model);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        remove = new javax.swing.JButton();
        clear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        add = new javax.swing.JButton();
        done = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        remove.setText("Remove");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        clear.setText("Clear All");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        add.setText("Add");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        done.setText("Done");
        done.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clear)
                            .addComponent(remove)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(done, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(remove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clear))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(add)
                    .addComponent(done))
                .addContainerGap(90, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void changeTitle (String str) {
        setTitle(str);
    }
    
     private void walk( String path, ArrayList cdfs ) {
        File root = new File(path);
        File[] list = root.listFiles();
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath(), cdfs );
            }
            else {
                if ( f.toString().toLowerCase().contains(".cdf") || 
                        f.toString().toLowerCase().contains(".mzml") ) {
                    cdfs.add(f.toString());
                }
            }
        }
    }
    
    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        fileChooser.setFileFilter(ExtensionFilter.msreadablefilter);
        fileChooser.setVisible(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled( true );
        int returnVal = fileChooser.showOpenDialog(this);
        ArrayList<String> cdffiles = new ArrayList(0);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] chosen = fileChooser.getSelectedFiles();
            for ( File f : chosen ){
                if ( f.isFile() ) {
                    model.add( model.size(), f );
                } else {
                    walk( f.toString(), cdffiles );
                    for ( String cdfFile : cdffiles ) {
                        model.add( model.size(), new File( cdfFile ) );
                    }
                }
            }
            jList1.setModel(model);
        }
            
        
    }//GEN-LAST:event_addActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        if (jList1.getSelectedIndex() >= 0) {
            model.remove(jList1.getSelectedIndex());
        }
        else {
            Utils.showErrorMessage ("No item selected");
        }
        jList1.setModel(model);
    }//GEN-LAST:event_removeActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        model.clear();
        jList1.setModel(model);
    }//GEN-LAST:event_clearActionPerformed

    private void doneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneActionPerformed
        dispose();
    }//GEN-LAST:event_doneActionPerformed

    public File[] getFiles() {
        File[] files = new File[model.size()];
        model.copyInto(files);
        return files;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton clear;
    private javax.swing.JButton done;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton remove;
    // End of variables declaration//GEN-END:variables
}
