package mass.spec;

import java.io.*;
import java.util.*;
import javax.swing.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

public class SequenceEngine extends javax.swing.JFrame {
    
    MSChrom c;
    File outputpath;
    int missed;
    String proteinsequence;
    double threshold;
    int digesttype;
    final int NO_DIGEST = 0;
    final int TRYPSIN_DIGEST = 1;
    final int PEPSIN_DIGEST = 2;
    final LoadingDialog ld = new LoadingDialog(this, false);
    ArrayList<Peptide> high_score_matches = new ArrayList();
    String[] fragments;
    static int OPTIMUM_THREAD_NO = 40;
    int file_counter = 0;
    MSReader msr;
    int timeIndex1, timeIndex2;
    
    public SequenceEngine(MSReader parent) {
        initComponents();
        setTitle("Sequence Engine");
        msr = parent;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        sequence = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        datafile = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        outputfile = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        go = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        missedcleavages = new javax.swing.JTextField();
        ionthreshold = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        trypsin = new javax.swing.JRadioButton();
        pepsin = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        nodigest = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        timeRange1 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        timeRange2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Enter protein sequence:");

        jLabel2.setText("Choose data file:");

        jButton1.setText("Open file browser");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Choose output file name:");

        go.setText("Go!");
        go.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goActionPerformed(evt);
            }
        });

        jLabel4.setText("Missed cleavage sites");

        missedcleavages.setText("0");
        missedcleavages.setEnabled(false);

        ionthreshold.setText("5000");

        jLabel5.setText("Lowest ion count for peak detection");

        buttonGroup1.add(trypsin);
        trypsin.setText("Trypsin");
        trypsin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trypsinActionPerformed(evt);
            }
        });

        buttonGroup1.add(pepsin);
        pepsin.setText("Pepsin");
        pepsin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pepsinActionPerformed(evt);
            }
        });

        jLabel6.setText("Digest type:");

        buttonGroup1.add(nodigest);
        nodigest.setSelected(true);
        nodigest.setText("None");
        nodigest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodigestActionPerformed(evt);
            }
        });

        jLabel7.setText("Time range:");

        jLabel8.setText("min to  ");

        jLabel9.setText("min");

        jLabel10.setText("(leave blank to sequence entire run)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(287, 287, 287)
                .addComponent(go)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(datafile, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(45, 45, 45)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(outputfile, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10))
                            .addComponent(nodigest)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sequence, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(missedcleavages, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(158, 158, 158)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(ionthreshold, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(trypsin)
                            .addComponent(pepsin)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(timeRange1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timeRange2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nodigest)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trypsin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pepsin)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(datafile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeRange1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(timeRange2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(missedcleavages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ionthreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(go)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        try {
            final File datapath = fileChooser.getSelectedFile();
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws MzMLUnmarshallerException {
                    c = new MSChrom(datapath, "MZML");
                    datafile.setText(datapath.toString());
                    return null;
                }
                @Override
                protected void done() {
                    ld.dispose();
                }
            };
            ld.setText("Opening...");
            ld.setLocationRelativeTo(this);
            ld.setVisible(true);
            worker.execute();
            
        } catch (Exception e) {
            Utils.showErrorMessage("Error reading file");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void goActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goActionPerformed
        if (sequence.getText().equals("")) {
            Utils.showErrorMessage("Please input protein sequence");
            return;
        } else {
            proteinsequence = sequence.getText();
            proteinsequence = proteinsequence.replace("\n", "").replace("\r", "").trim();
        }
        
        if (c == null) {
            Utils.showErrorMessage("Please input data file");
            return;
        } 
        
        if (missedcleavages.getText().equals("")) {
            missed = 0;
        } else if (!MSMath.isInt(missedcleavages.getText())) {
            Utils.showErrorMessage("Invalid input for missed cleavage sites");
            return;
        } else {
            missed = Integer.parseInt(missedcleavages.getText());
        }
        
        
        if (outputfile.getText().equals("")) {
            Utils.showErrorMessage("Please input destination file name");
            return;
        } else {
            if (outputfile.getText().toLowerCase().contains(".txt")) {
                outputpath = Utils.osjoin(msr.documents, outputfile.getText());
            } else {
                outputpath = Utils.osjoin(msr.documents, outputfile.getText()+".txt");
            }
        }
        
        
        if (ionthreshold.getText().equals("") || !MSMath.isDouble(ionthreshold.getText())) {
            Utils.showErrorMessage("Invalid input for ion count threshold");
            return;
        } else {
            threshold = Double.parseDouble(ionthreshold.getText());
        }

        
        if ((timeRange1.getText().equals("") && !timeRange2.getText().equals("")) 
                || (timeRange2.getText().equals("") && !timeRange1.getText().equals(""))) {
            Utils.showErrorMessage ("Invalid time range");
            return;
        }
        
        if (timeRange1.getText().equals("") && timeRange2.getText().equals("")) {
            timeIndex1 = 0;
            timeIndex2 = c.spectra.length;
        } else {
            if (!MSMath.isDouble(timeRange1.getText()) || !MSMath.isDouble(timeRange2.getText())) {
                Utils.showErrorMessage ("Invalid time range");
                return;
            }
            float time1 = Float.parseFloat(timeRange1.getText());
            timeIndex1 = Utils.binarySearch(c.TIC[0], time1);
            float time2 = Float.parseFloat(timeRange2.getText());
            timeIndex2 = Utils.binarySearch(c.TIC[0], time2);
        }
        
        if (trypsin.isSelected()) digesttype = TRYPSIN_DIGEST;
        else if (pepsin.isSelected()) digesttype = PEPSIN_DIGEST;
        else digesttype = NO_DIGEST;
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground() {
                threadedSequenceEngine();
                return null;
            }
            @Override
            protected void done() {
                outputSequenceResults();
                ld.dispose();
                Utils.showMessage ("Sequencing complete!");
                dispose();
            }
        };
        ld.setText("Sequencing...");
        ld.setLocationRelativeTo(this);
        ld.setVisible(true);
        worker.execute();
    }//GEN-LAST:event_goActionPerformed

    private void nodigestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodigestActionPerformed
        missedcleavages.setEnabled(false);
        
    }//GEN-LAST:event_nodigestActionPerformed

    private void trypsinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trypsinActionPerformed
        missedcleavages.setEnabled(true);
    }//GEN-LAST:event_trypsinActionPerformed

    private void pepsinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pepsinActionPerformed
        missedcleavages.setEnabled(true);
    }//GEN-LAST:event_pepsinActionPerformed
    
    private void threadedSequenceEngine() {
        if (digesttype == NO_DIGEST) {
            fragments = new String[1];
            fragments[0] = proteinsequence;
        } else if (digesttype == TRYPSIN_DIGEST) {
            fragments = Utils.trypsindigest(proteinsequence, missed);
        } else {
            fragments = Utils.pepsincleavage(proteinsequence, missed);
        }
        try {
            int[][] indices = Utils.getSplitArrayIndices(c.spectra, OPTIMUM_THREAD_NO, timeIndex1, timeIndex2);
            Thread[] runners = new Thread[OPTIMUM_THREAD_NO];
            for (int i = 0; i < OPTIMUM_THREAD_NO; i++) {
                runners[i] = new Thread(new SequenceSpectrum(this, indices[i][0], indices[i][1]));
                runners[i].start();
            }
             for (int i = 0; i < runners.length; i++) {
                runners[i].join();
            }
        } catch (Exception e) {
            Utils.showErrorMessage ("Unable to complete sequence comparison");
            Utils.logException (msr.bin, e);
        }
    }
    
    private void outputSequenceResults () {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outputpath));
            out.println("Sequence: "+proteinsequence);
            String dig = "";
            if (digesttype == TRYPSIN_DIGEST) {
                dig = "trypsin";
            } else if (digesttype == PEPSIN_DIGEST) {
                dig = "pepsin";
            } else if (digesttype == NO_DIGEST) {
                dig = "none";
            }
            out.println("Type of digest: "+dig);
            out.println("Missed cleavages: "+missed);
            out.println("Lower threshold: "+threshold);
            out.println("====================");
            out.println("Sequence\tM/z\tCharge\tElution time");
            for (Peptide pept: high_score_matches) {
                out.println(pept.sequence+"\t"+pept.mz+"\t"+pept.charge+"\t"+pept.elutiontime);
            }
            out.close();
        } catch (IOException e) {
            Utils.logException(msr.bin, e);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField datafile;
    private javax.swing.JButton go;
    private javax.swing.JTextField ionthreshold;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField missedcleavages;
    private javax.swing.JRadioButton nodigest;
    private javax.swing.JTextField outputfile;
    private javax.swing.JRadioButton pepsin;
    private javax.swing.JTextField sequence;
    private javax.swing.JTextField timeRange1;
    private javax.swing.JTextField timeRange2;
    private javax.swing.JRadioButton trypsin;
    // End of variables declaration//GEN-END:variables
}

class SequenceSpectrum implements Runnable {
    SequenceEngine engine;
    int start;
    int end;
    
    public SequenceSpectrum (SequenceEngine e, int s, int en) {
        engine = e;
        start = s;
        end = en;
    }
    
    public SequenceSpectrum() {}
    
    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            sequence_spectrum (i);
            engine.ld.setText("Processing "+(++engine.file_counter)+ " of " + (engine.timeIndex2 - engine.timeIndex1));
        }
    }
    
    private void sequence_spectrum (int index) {
        ArrayList<Float> peaks = new ArrayList(); 
        float time = engine.c.TIC[0][index];
        MassSpectrum ms = engine.c.spectra[index];
        peaks.clear();
        if (engine.c.SPECTRA_UNIFORM) {
            for (int k = 0; k < engine.c.mz_values.length; k++) {
                if (ms.yvals[k] > engine.threshold) peaks.add(engine.c.mz_values[k]);
            }
        } else {
            for (int k = 0; k < ms.msValues[0].length; k++) {
                if (ms.msValues[1][k] > engine.threshold) peaks.add(ms.msValues[0][k]);
            }
        }
        for (Float peak: peaks) {
            for (String frag: engine.fragments) {
                for (int k = 1; k < 50; k++) {
                    double mwapprox = (MSMath.mwEstimate(frag)+k)/k;
                    if (Math.abs(mwapprox - peak) < 1) {
                        Peptide temp = new Peptide(frag, k, time);
                        engine.high_score_matches.add(temp);
                        int peakIndex, startIndex, endIndex;
                        double stepSize;
//                        MassSpectrum ms = 
                        if (engine.c.SPECTRA_UNIFORM) {
                            peakIndex = Utils.binarySearch(engine.c.mz_values, peak);
                            stepSize = engine.c.mz_values[50] - engine.c.mz_values[49];
                            startIndex = peakIndex - (int) (10/stepSize);
                            if (startIndex < 0) startIndex = 0; 
                            endIndex = peakIndex + (int) (10/stepSize);
                            if (endIndex >= engine.c.mz_values.length) endIndex = engine.c.mz_values.length - 1; 
                        } else {
                            peakIndex = Utils.binarySearch(ms.msValues[0], peak);
                            stepSize = ms.msValues[0][50] - ms.msValues[0][49];
                            startIndex = peakIndex - (int) (10/stepSize);
                            if (startIndex < 0) startIndex = 0; 
                            endIndex = peakIndex + (int) (10/stepSize);
                            if (endIndex >= ms.msValues[0].length) endIndex = ms.msValues[0].length - 1; 
                        }
                        double[][] isotope = temp.getDistribution((int)Math.pow(10, 5), true, true);
                        double max = ms.getYMax (startIndex, endIndex);
                        max /= MSMath.getMax(isotope[1]);
                        for (int f = 0; f < isotope[1].length; f++) isotope[1][f] = isotope[1][f] * max;
                        float[][] data = new float [2][];
                        data[0] =  (engine.c.SPECTRA_UNIFORM) ? 
                                Arrays.copyOfRange(engine.c.mz_values, startIndex, endIndex) 
                                : Arrays.copyOfRange(ms.msValues[0], startIndex, endIndex);
                        data[1] =  (engine.c.SPECTRA_UNIFORM) ? 
                                Arrays.copyOfRange(ms.yvals, startIndex, endIndex) 
                                : Arrays.copyOfRange(ms.msValues[1], startIndex, endIndex);
                        double score = MSMath.getScore(data, isotope);
                        if (score > .75) {
                            engine.high_score_matches.add(temp);
                        }
                    } else {
                        continue;
                    }
                }
            }
        } 
    }
}
