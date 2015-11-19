package mass.spec;

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ViewPeptides extends javax.swing.JDialog {

    boolean saved;
//    int sortKey;
    private final JFileChooser fc;
    private final MSReader msr;
    
    public ViewPeptides(java.awt.Frame parent, boolean modal) {
        super(parent, "Peptides", modal);
        initComponents();
        msr = MSReader.getInstance();
        fc = new JFileChooser();
        saved = true;
//        sortKey = 1;
        DefaultTableModel table = FormatChange.PeptidesToDTM( msr.getPeptides() );
        jTable1.setModel( table );
        jTable1.revalidate();
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        jTable1.getTableHeader().addMouseListener( new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int sortKey = jTable1.getTableHeader().columnAtPoint( e.getPoint() );
                msr.getPeptides().setSortKey( sortKey );
                refresh();
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
        
        });
        
        addWindowListener( new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                if ( saved ) dispose();
                else {
                     int opt = JOptionPane.showOptionDialog(null, "Do you want to save your peptides before exiting?",
                        "Save", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                        null, new Object[] {"Yes", "No"}, (Object)"Yes");
                     if (opt == 0) {
                         save();
                     }
                     dispose();
                }
            }

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        add = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        edit = new javax.swing.JButton();
        clear = new javax.swing.JButton();
        extract = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newPeptideList = new javax.swing.JMenuItem();
        openList = new javax.swing.JMenuItem();
        addFromTxt = new javax.swing.JMenuItem();
        save = new javax.swing.JMenuItem();
        saveAS = new javax.swing.JMenuItem();
        setDefault = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        add.setText("Add peptide");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        delete.setText("Delete peptide");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        edit.setText("Edit peptide");
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });

        clear.setText("Clear all peptides");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });

        extract.setText("Extract peptide");
        extract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractActionPerformed(evt);
            }
        });

        jButton1.setText("Check accuracy");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");

        newPeptideList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newPeptideList.setText("New list");
        newPeptideList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPeptideListActionPerformed(evt);
            }
        });
        jMenu1.add(newPeptideList);

        openList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openList.setText("Open .pep file");
        openList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openListActionPerformed(evt);
            }
        });
        jMenu1.add(openList);

        addFromTxt.setText("Add from .txt file");
        addFromTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFromTxtActionPerformed(evt);
            }
        });
        jMenu1.add(addFromTxt);

        save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        save.setText("Save");
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        jMenu1.add(save);

        saveAS.setText("Save as");
        saveAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveASActionPerformed(evt);
            }
        });
        jMenu1.add(saveAS);

        setDefault.setText("Set as default peptide list");
        setDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDefaultActionPerformed(evt);
            }
        });
        jMenu1.add(setDefault);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 602, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(extract, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edit, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(clear)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(extract)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(add)
                    .addComponent(delete)
                    .addComponent(edit)
                    .addComponent(clear))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        try {
            EditPeptide ep = new EditPeptide( msr, true );
            ep.setLocationRelativeTo(this);
            ep.setVisible(true);
            msr.addPeptidetoList( ep.getPeptide() );
            saved = false;
            refresh();
        } catch (Exception e) { 
            Utils.showErrorMessage("Invalid input"); 
        }
    }//GEN-LAST:event_addActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        if (jTable1.getSelectedRow() == -1) {
            Utils.showErrorMessage("No peptide selected");
            return;
        }
        int[] selectedIndices = jTable1.getSelectedRows();
        String prompt = "Are you sure you want to delete " + 
                ((selectedIndices.length > 1) ? 
                "this peptide?" : "these peptides?");

        int opt = JOptionPane.showOptionDialog(null, prompt, "Peptide delete", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                        null, new Object[] {"Yes", "No"}, (Object)"Yes");
        if (opt == 1) return;
        for ( int index : selectedIndices ) {
            msr.removePeptideFromList( index );
        }
        saved = false;
        refresh(); 
    }//GEN-LAST:event_deleteActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        if (jTable1.getSelectedRow() == -1) {
            Utils.showErrorMessage("No peptide selected");
            return;
        }
        int index = jTable1.getSelectedRow();
        EditPeptide ep = new EditPeptide( msr, true );
        ep.setLocationRelativeTo(this);
        ep.setPeptide( msr.getPeptides().elementAt(index) );
        ep.setVisible( true );
        msr.removePeptideFromList( index );
        msr.addPeptidetoList( ep.getPeptide() );
        saved = false;
        refresh();
    }//GEN-LAST:event_editActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        int opt = JOptionPane.showOptionDialog(null, "Are you sure you want to delete all peptides?",
                        "Peptide delete", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, 
                        null, new Object[] {"Yes", "No"}, (Object)"Yes");
        if (opt != 1) {
            msr.setPeptides( new PeptideList() );
            saved = false;
            refresh();
        }
    }//GEN-LAST:event_clearActionPerformed

    private void save () {
        if (msr.getPeptides().getPath() == null) {
            fc.setCurrentDirectory( msr.msreaderFiles );
            fc.setFileFilter( fc.getAcceptAllFileFilter() );
            fc.setSelectedFile( new File("") );
            int returnVal = fc.showSaveDialog(this);
            if (returnVal != JFileChooser.APPROVE_OPTION) return;
            File savepath;
            if (fc.getSelectedFile().toString().toLowerCase().contains(".pep")) {
                savepath = fc.getSelectedFile();
            }
            else {
                savepath = new File(fc.getSelectedFile().toString() + ".pep");
            }
            msr.getPeptides().setPath(savepath);
            refresh();
            msr.getPeptides().save();
        }
        else msr.getPeptides().save();
        saved = true;
        Utils.showMessage("Peptide list saved!");
        if (!new File (msr.getProperty("peptidepath")).exists()) {
            msr.setProperty("peptidepath", msr.getPeptides().getPath().toString());
            msr.saveProperties();
        }
    }
    
    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        save();
    }//GEN-LAST:event_saveActionPerformed

    private void newPeptideListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPeptideListActionPerformed
        msr.setPeptides( new PeptideList() );
        refresh();
        saved = false;
    }//GEN-LAST:event_newPeptideListActionPerformed

    private void openListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openListActionPerformed
        fc.setCurrentDirectory( msr.msreaderFiles );
        fc.setFileFilter(ExtensionFilter.pepfilter);
        fc.setSelectedFile(new File(""));
        int returnVal = fc.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        
        File openpath = fc.getSelectedFile();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(openpath));
            try {
                msr.setPeptides( (PeptideList)ois.readObject() );
            } finally {
                ois.close();
            }
        } catch ( IOException e ) {
            Utils.showErrorMessage("Error loading list");
        } catch ( ClassNotFoundException e ) {
            Utils.showErrorMessage("Error loading list");
        }
        setTitle( msr.getPeptides().getPath().getName() );
        refresh();
        
        saved = true;
    }//GEN-LAST:event_openListActionPerformed

    private void saveASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveASActionPerformed
        fc.setCurrentDirectory( msr.msreaderFiles );
        fc.setFileFilter(fc.getAcceptAllFileFilter());
        fc.setSelectedFile(new File(""));
        int returnVal = fc.showSaveDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        
        File savepath;
        if (fc.getSelectedFile().toString().toLowerCase().contains(".pep")) {
            savepath = fc.getSelectedFile();
        }
        else {
            savepath = new File(fc.getSelectedFile().toString() + ".pep");
        }
        msr.getPeptides().setPath( savepath );
        refresh();
        msr.getPeptides().save();
        Utils.showMessage("Peptide list saved!");
        saved = true;
        
    }//GEN-LAST:event_saveASActionPerformed

    private void setDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDefaultActionPerformed
        msr.setProperty( "peptidepath", msr.getPeptides().path.toString() );
        msr.saveProperties();
        Utils.showMessage("Peptide list set as default");
        saved = true;
    }//GEN-LAST:event_setDefaultActionPerformed

    private void extractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractActionPerformed
        if (msr.currentMS == null) {
             Utils.showErrorMessage("Error: no spectrum loaded");
             return;      
        }
        if (jTable1.getSelectedRow() == -1) {
            Utils.showErrorMessage("No peptide selected");
            return;
        }
        int index = jTable1.getSelectedRow();
        msr.chromatogramType = MSReader.CHROM_TYPE_EIC;
        Peptide temp = msr.getPeptides().elementAt(index);
        msr.XICrange = new double [] {temp.mz - 2, temp.mz + 2};
        msr.refreshChromatogram();
        dispose();
    }//GEN-LAST:event_extractActionPerformed
    
    private void addFromTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFromTxtActionPerformed
        fc.setCurrentDirectory( msr.msreaderFiles);
        fc.setFileFilter(ExtensionFilter.txtfilter);
        fc.setSelectedFile(new File(""));
        int returnVal = fc.showOpenDialog(this);
        if ( returnVal != JFileChooser.APPROVE_OPTION ) return;
        File f = fc.getSelectedFile();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            try {
                String str;
                while ( (str=br.readLine()) != null ) {
                    String[] tiago = str.split("\t|,");
                    for (String s : tiago) {
                        System.out.println(s);
                    }
                    if (tiago.length == 4) {
                        try {
                            msr.getPeptides().addPeptide (new Peptide (tiago[0],
                                    Integer.parseInt(tiago[2]), 
                                    Double.parseDouble(tiago[3])));
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            throw new NumberFormatException();
                        }
                    } else {
                        System.out.println("token count: "+tiago.length);
                        throw new NumberFormatException();
                    }
                } 
            } finally {
                br.close(); 
            }
            
        } catch (NumberFormatException io) {
            io.printStackTrace();
            Utils.showErrorMessage("Error: text file is not in correct format");
            Utils.logException( io );
        } catch (IOException n) {
            Utils.showErrorMessage("Error: could not read file");
            Utils.logException( n );
        }
        saved = false;
        refresh();
    }//GEN-LAST:event_addFromTxtActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (msr.currentMS == null) {
             Utils.showErrorMessage("Error: no spectrum loaded");
             return;      
        }
        if (jTable1.getSelectedRow() == -1) {
            Utils.showErrorMessage("No peptide selected");
            return;
        }
        int index = jTable1.getSelectedRow();
        msr.chromatogramType = MSReader.CHROM_TYPE_EIC;
        Peptide pept = msr.getPeptides().elementAt(index);
        MSChrom currentMSC = MSReader.getInstance().currentMSC;
        double[][] eic = currentMSC.getEIC(pept.mz - 2, pept.mz + 2);
        int elutionindex = currentMSC.getElutionIndexFromEIC(eic, pept.elutiontime);
        MassSpectrum currentMS = currentMSC.spectra[elutionindex];

        currentMS.convertToNonUniform( currentMSC );
        int windowSize = MSReader.getInstance().getIntProperty("windowSize");
        
        double[][] dataRange = currentMS.getWindow( pept.mz, windowSize );
            
        double[][] isotope = pept.getThreadedDistribution((int)Math.pow(10, 6));
        
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
        JFreeChart chart = Utils.drawChart( dataset, pept.displaySequence, 
                "m/z","intensity" );
        
        Peak_Zoom pz = new Peak_Zoom();
        pz.setChart(chart);
        pz.expandGraph();
        pz.setTitle( MSMath.getScore( dataRange, isotope ) + "" );
        pz.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private ArrayList<Peptide> importPeptides ( String filename ) {
        ArrayList<Peptide> peptides = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            try {
                String str;
                while ( (str=br.readLine()) != null ) {
                    String[] tiago = str.split("\t|,");
                    for (String s : tiago) {
                        System.out.println(s);
                    }
                    if (tiago.length == 4) {
                        try {
                            peptides.add( new Peptide (tiago[0],
                                    Integer.parseInt(tiago[2]), 
                                    Double.parseDouble(tiago[3])) );
                        } catch ( Exception e ) {
                            e.printStackTrace();
                            throw new NumberFormatException();
                        }
                    } else {
                        System.out.println("token count: "+tiago.length);
                        throw new NumberFormatException();
                    }
                } 
            } finally {
                br.close(); 
            }

        } catch (NumberFormatException io) {
            io.printStackTrace();
            Utils.showErrorMessage("Error: text file is not in correct format");
            Utils.logException( io );
        } catch (IOException n) {
            Utils.showErrorMessage("Error: could not read file");
            Utils.logException( n );
        }
        return peptides;
    }
    
    private void refresh() {
        DefaultTableModel d = FormatChange.PeptidesToDTM( msr.getPeptides() );
        jTable1.setModel(d);
        jTable1.revalidate();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JMenuItem addFromTxt;
    private javax.swing.JButton clear;
    private javax.swing.JButton delete;
    private javax.swing.JButton edit;
    private javax.swing.JButton extract;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuItem newPeptideList;
    private javax.swing.JMenuItem openList;
    private javax.swing.JMenuItem save;
    private javax.swing.JMenuItem saveAS;
    private javax.swing.JMenuItem setDefault;
    // End of variables declaration//GEN-END:variables


}
