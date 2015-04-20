package mass.spec;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;


public class PDBQuery extends javax.swing.JDialog {
    
    DefaultListModel<String> model = new DefaultListModel();
    int searchType;
    final int SEARCH_TYPE_ALL = 0;
    final int SEARCH_TYPE_AUTHOR = 1;
    final int SEARCH_TYPE_PDBID = 2;
    final int SEARCH_TYPE_PMID = 3;
    List<String> pdbIds;
    File pdb;
    File savepath;
    
    public PDBQuery(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jList1.setModel(model);
    }

    public File getPath () {
        return savepath;
    }
    
    public void setSavePath (File f) {
        savepath = f;
    }
    
    public void query (String term) {
        try {
            String xml = null;
            switch (searchType) {
                case SEARCH_TYPE_ALL:
                    xml = searchByKeyword(term);
                    break;
                case SEARCH_TYPE_AUTHOR:
                    xml = searchByAuthor(term);
                    break;
                case SEARCH_TYPE_PDBID:
                    break;
                case SEARCH_TYPE_PMID:
                    xml = searchByPubmedID(term);
                    break;    
            }
            if (searchType == SEARCH_TYPE_PDBID) {
                pdbIds = new ArrayList();
                pdbIds.add(term);
                if (!validConnection()) {
                    Utils.showErrorMessage("Error: server not available");
                    return;
                }
            } else {
                try {
                    pdbIds = postQuery(xml);
                } catch (UnknownHostException exc) {
                    Utils.showErrorMessage("Error: server not available");
                    return;
                }
            }
            if (pdbIds.isEmpty()) {
                Utils.showMessage ("No results found");
                return;
            }
            List<String> pdbTitles ;
            pdbTitles = getProteinTitles(pdbIds);
            if (pdbTitles.isEmpty()) {
                Utils.showMessage ("No results found");
                return;
            }
            for (String str: pdbTitles) {
                model.addElement(str);
            }
            jList1.setModel(model);
        } catch ( HeadlessException e ) {
            e.printStackTrace(); 
        } catch ( IOException e ) { 
            e.printStackTrace();
        }
    }
    
    private String searchByKeyword (String term) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<orgPdbQuery>" +
            "<version>head</version>" +
            "<queryType>org.pdb.query.simple.AdvancedKeywordQuery</queryType>" +        
            "<description>Text search for: "+term+"</description>" +
            "<keywords>"+term+"</keywords>" +
            "</orgPdbQuery>";
    }
    
    private String searchByAuthor (String name) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<orgPdbQuery>" +
            "<version>head</version>" +
            "<queryType>org.pdb.query.simple.AdvancedAuthorQuery</queryType>" +        
            "<description>Author Name: Search type is All Authors and Author is "+name+" and Exact match is false</description>" +
            "<searchType>All Authors</searchType>" +
            "<audit_author.name>"+name+"</audit_author.name>" +
            "<exactMatch>false</exactMatch>" +
            "</orgPdbQuery>";
    }
    
    private String searchByPubmedID (String id) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<orgPdbQuery>" +
            "<version>head</version>" +
            "<queryType>org.pdb.query.simple.PubmedIdQuery</queryType>" +        
            "<description>Simple query for a list of PubMed IDs:"+id+"</description>" +
            "<pubMedIdList>"+id+"</pubMedIdList>"+
            "</orgPdbQuery>";
    }
    
    private static List<String> postQuery(String xml) throws IOException {
        URL u = new URL("http://www.rcsb.org/pdb/rest/search");
        String encodedXML = URLEncoder.encode(xml,"UTF-8");
        InputStream in =  doPOST(u,encodedXML);
        List<String> pdbIds = new ArrayList();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = rd.readLine()) != null) {
                pdbIds.add(line);
            }
            rd.close();
        } catch ( Exception exc ) {
            exc.printStackTrace();
        }
        return pdbIds;
    }
    
    private static InputStream doPOST(URL url, String data) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        return conn.getInputStream();
    }

    private static List<String> getProteinTitles (List<String> ids) {
        try {
            String site = "http://www.rcsb.org/pdb/rest/describePDB?structureId="+ids.get(0);
            if (ids.size() < 50) {
                for (int i = 1; i < ids.size(); i++) site += (","+ids.get(i));
            } else {
                for (int i = 1; i < 50; i++) site += (","+ids.get(i));
            }
            //for (int i = 1; i < ids.size(); i++) site += (","+ids.get(i));
            URL url = new URL (site);
            ArrayList<String> titles;
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                String input;
                titles = new ArrayList();
                while((input = in.readLine()) != null) {
                    if (input.contains("title=\"")) {
                        int ind = input.indexOf("title=\"")+"title=\"".length();
                        titles.add(input.substring(ind, input.indexOf("\"", ind)));
                    }
                }
            } finally {
                if ( in != null ) in.close();
            }
            return titles;
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean validConnection () {
        try {
            String site = "http://www.rcsb.org/";
            URL url = new URL (site);
            url.openStream();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void download (String id) {
        String site = "http://www.rcsb.org/pdb/files/"+id+".pdb";
        try {
            URL url = new URL (site);
            PrintWriter out;
            BufferedReader in = null;
            try {
                in = new BufferedReader (new InputStreamReader(url.openStream()));
                savepath = Utils.osjoin(savepath, id+".pdb");
                out = new PrintWriter (new FileOutputStream (savepath));
                String current;
                while ((current=in.readLine()) != null) {
                    out.println(current);
                }
            } finally {
                if ( in != null ) in.close();
            }
            out.close();
        } catch (Exception e) {
            Utils.showErrorMessage ("Unable to download file");
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        term = new javax.swing.JTextField();
        searchtype = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        accept = new javax.swing.JButton();
        cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RCSB Database Search");

        jLabel1.setText("Search term");

        jLabel2.setText("Search type");

        searchtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Author", "PDB ID", "Pubmed ID" }));

        jButton1.setText("Search!");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        accept.setText("OK");
        accept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(searchtype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(term, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(307, 307, 307)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(accept, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancel)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(term, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(searchtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accept)
                    .addComponent(cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        searchType = searchtype.getSelectedIndex();
        model.clear();
        query (term.getText());
        jList1.setSelectedIndex(0);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void acceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptActionPerformed
        final String id = pdbIds.get(jList1.getSelectedIndex());
        download(id);
        dispose();
    }//GEN-LAST:event_acceptActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_cancelActionPerformed

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
            java.util.logging.Logger.getLogger(PDBQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PDBQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PDBQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PDBQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PDBQuery dialog = new PDBQuery(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton accept;
    private javax.swing.JButton cancel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox searchtype;
    private javax.swing.JTextField term;
    // End of variables declaration//GEN-END:variables

}

