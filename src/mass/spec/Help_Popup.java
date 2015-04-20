package mass.spec;

import java.io.IOException;
import java.net.*;
import javax.swing.tree.TreeSelectionModel;

public class Help_Popup extends javax.swing.JFrame {

    public Help_Popup() {
        super("Help Menu");
        initComponents();
        jSplitPane1.setDividerLocation(.35);
        jEditorPane1.setEditable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        try {
            URL helpURL = getClass().getResource("/resources/startup.html");
            jEditorPane1.setPage(helpURL);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jScrollPane3 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Help");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Getting Started");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Load a file");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Edit spectra");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Check peptide accuracy");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("View hydrogen exchange");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("HDX form");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Advanced");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Auto HDX");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Manual HDX");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Generate heat map");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Sequence engine");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("FAQ");
        treeNode1.add(treeNode2);
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        tree.setPreferredSize(new java.awt.Dimension(700, 500));
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane3.setViewportView(jEditorPane1);

        jSplitPane1.setRightComponent(jScrollPane3);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMouseClicked
        String node = tree.getClosestPathForLocation(evt.getX(), evt.getY()).getLastPathComponent().toString();
        try {
            if (node.equals("Help")) {
                jEditorPane1.setPage(getClass().getResource("/resources/startup.html"));
            } else if (node.equals("Getting Started")) {
                jEditorPane1.setPage(getClass().getResource("/resources/gettingstarted.html"));
            } else if (node.equals("Load a file")) {
                jEditorPane1.setPage(getClass().getResource("/resources/loadfile.html"));
            } else if (node.equals("Edit spectra")) {
                jEditorPane1.setPage(getClass().getResource("/resources/editspectra.html"));
            } else if (node.equals("Check peptide accuracy")) {
                jEditorPane1.setPage(getClass().getResource("/resources/checkaccuracy.html"));
            } else if (node.equals("View hydrogen exchange")) {
                jEditorPane1.setPage(getClass().getResource("/resources/HDExchange.html"));
            } else if (node.equals("HDX form")) {
                jEditorPane1.setPage(getClass().getResource("/resources/TimeExchange.html"));
            } else if (node.equals("Advanced")) {
                jEditorPane1.setPage(getClass().getResource("/resources/advanced.html"));
            } else if (node.equals("Auto HDX")) {
                jEditorPane1.setPage(getClass().getResource("/resources/autohdx.html"));
            } else if (node.equals("Manual HDX")) {
                jEditorPane1.setPage(getClass().getResource("/resources/manualhdx.html"));
            } else if (node.equals("Generate heat map")) {
                jEditorPane1.setPage(getClass().getResource("/resources/generateheatmap.html"));
            } else if (node.equals("Sequence engine")) {
                jEditorPane1.setPage(getClass().getResource("/resources/sequenceengine.html"));
            } else if (node.equals("FAQ")) {
                jEditorPane1.setPage(getClass().getResource("/resources/faq.html"));
            } else {
                //do nothing
            }
        } catch (IOException e) {
            //do nothing
        }
    }//GEN-LAST:event_treeMouseClicked
   
     
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Help_Popup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Help_Popup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Help_Popup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Help_Popup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Help_Popup().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables

}
