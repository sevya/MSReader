package mass.spec;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ChoosePeakNo extends javax.swing.JDialog {

    private final MSReader msr;
//    private int sortKey;
    private Peptide peptide;
    
    public ChoosePeakNo(java.awt.Frame parent, boolean modal) {
        super(parent, "Choose desired peptide", modal);
        initComponents();
//        sortKey = 1;
        msr = MSReader.getInstance();
        peakList.setModel( FormatChange.PeptidesToDTM( msr.getPeptides() ) );
        peakList.getTableHeader().addMouseListener( new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int sortKey = peakList.getTableHeader().columnAtPoint(e.getPoint());
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
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        peakList = new javax.swing.JTable();
        getPeakFromList = new javax.swing.JButton();
        newpeptide = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel2.setText("Choose peptide from the list:");

        peakList.setModel(new javax.swing.table.DefaultTableModel(
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
        peakList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(peakList);

        getPeakFromList.setText("Go!");
        getPeakFromList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getPeakFromListActionPerformed(evt);
            }
        });

        newpeptide.setText("New peptide");
        newpeptide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newpeptideActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(getPeakFromList, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(newpeptide)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getPeakFromList)
                    .addComponent(newpeptide))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refresh() {
        peakList.setModel( FormatChange.PeptidesToDTM( msr.getPeptides() ) );
        peakList.revalidate();
    }
    
    private void getPeakFromListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getPeakFromListActionPerformed
        int selected = peakList.getSelectedRow();
        if (selected == -1) {
            Utils.showMessage("No peptide selected");
            return;
        }
        peptide = msr.getPeptides().elementAt( selected );
        dispose();
    }//GEN-LAST:event_getPeakFromListActionPerformed

    private void newpeptideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newpeptideActionPerformed
        EditPeptide ep = new EditPeptide(null, true);
        ep.setLocationRelativeTo(this);
        ep.setVisible(true);
        try {
            peptide = ep.getPeptide();
            dispose();
        } catch (NullPointerException npe) {
            //edit peptide menu was cancelled - do nothing
        }
    }//GEN-LAST:event_newpeptideActionPerformed

    public Peptide getPeptide() {
        return peptide;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton getPeakFromList;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newpeptide;
    private javax.swing.JTable peakList;
    // End of variables declaration//GEN-END:variables


}
