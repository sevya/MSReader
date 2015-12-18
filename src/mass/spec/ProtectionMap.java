package mass.spec;

import java.awt.Color;
import java.io.*;
import java.util.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.math3.stat.inference.TTest;

public class ProtectionMap {
    
    // Doesn't require pdb or pymol path
    public static void createProtectionMap (HDRun[][] forms, File outPath, String sequence) {
        double[] deuteration;        
        // If you're analyzing exchange
        if (forms.length == 1) {
            List<String> peptidelist = new ArrayList();
            List<Double> deuterium = new ArrayList();
            for (int i = 0; i < forms[0].length; i++) {
                peptidelist.add( forms[0][i].getPeptide().sequenceNoModification() );
                
                // TODO pulls value from last time point - find a way to make this more flexible
                Object[][] pctValues = forms[0][i].getPercentValues();
                Double latestTimePoint = MSMath.getMax( pctValues[0] );
                Double avgValue = 0.0;
                int count = 0;
                for ( int ii = 0; ii < pctValues[0].length; ++ii ) {
                    if ( ((Double)pctValues[0][ii]) - latestTimePoint < Math.pow(10, -4)) {
                        avgValue += (Double)pctValues[1][ii];
                        count++;
                    }
                }
                avgValue /= count;
                deuterium.add(avgValue);
            }

            deuteration = calculateResidueDeuteration (peptidelist, deuterium, sequence );
        // If you're analyzing protection
        } else { 
            Utils.showWarningMessage("Protection maps are under construction");
            return;
        }
        writeHeatMap( deuteration, outPath );
    }
    
    public static List<Color> calculateColors( List<Double> deuteration ) {
        return calculateColors( deuteration, false );
    }
    
    public static List<Color> calculateColors( List<Double> deuteration, boolean negative ) {
        Double maxDeut = Collections.max(deuteration);
        // if negative values are allowed (ie in protection maps)
        if ( negative ) {
            Double minDeut = Collections.min(deuteration);
            maxDeut = 50.0;
            minDeut = -50.0;
            List<Color> colors = new ArrayList();
            System.out.println("Min: "+minDeut+"\tMax: "+maxDeut);
            for ( Double deut : deuteration ) {
                if ( deut > 0 ) {
                    int drop = (int)((deut/maxDeut) * 255);
                    colors.add( new Color( 255-drop, 255-drop, 255 ) );
                } else if ( deut < 0 ) {
                    int drop = (int)((deut/minDeut) * 255);
                    colors.add( new Color( 255, 255-drop, 255-drop ) );
                } else {
                    colors.add( new Color( 255, 255, 255 ) );
                }
            }
            return colors;
        } else {
            // TODO: fix hard coded 5 here or add logic
            maxDeut = (maxDeut > 50) ? maxDeut : 50.0;
            Double minDeut = Collections.min(deuteration) < 0 ? 0 : Collections.min(deuteration) - 5;
//            minDeut = 0.0;
            List<Color> colors = new ArrayList();
            System.out.println("Min: "+minDeut+"\tMax: "+maxDeut);
            for ( Double deut : deuteration ) {
                int drop = (deut > 0) ? (int)((deut/maxDeut) * 255) : 0;
                colors.add(new Color( 255-drop, 255-drop, 255 ) );
            }
            return colors;
        }

    }
    
    public static void createExchangeLines (HDRun[][] forms, File outPath, String sequence) {
        /// Get time point for protection map
        /// Use first instance of HDRun - TODO fix this to be better
        Double[] timePoints = new Double[forms[0][0].getTimePoints().size()];
        timePoints = forms[0][0].getTimePoints().toArray( timePoints );
        Double timePoint = (Double)JOptionPane.showInputDialog(
                            new JFrame(),
                            "Choose your time point to measure exchange",
                            "Exchange Map",
                            JOptionPane.PLAIN_MESSAGE,
                            new ImageIcon(),
                            timePoints,
                            timePoints[0]);
        
        double[] deuteration;        
        // If you're analyzing exchange
        if (forms.length == 1) {
            List<String> peptidelist = new ArrayList();
            List<Double> deuterium = new ArrayList();
            for (int i = 0; i < forms[0].length; i++) {
                peptidelist.add( forms[0][i].getPeptide().sequenceNoModification() );                
                deuterium.add( forms[0][i].getAvgPercentAtTime( timePoint ) );
                System.out.println(peptidelist.get(peptidelist.size()-1)+"\t"+
                        deuterium.get(deuterium.size()-1));
            }

            deuteration = calculateResidueDeuteration (peptidelist, deuterium, sequence );   
            List<Color> colorlist = calculateColors( deuterium );
            Drawer.drawExchangeMap( sequence, peptidelist, colorlist );
        // If you're analyzing protection
        } else {             
            List<String> peptidelist = new ArrayList();
            List<Double> deuterium = new ArrayList();
            for (int i = 0; i < forms[0].length; i++) {
                // find index of peptide in the protected set
                int protectedIndex = -1;
                String peptideUnprotected = forms[0][i].getPeptide().sequenceNoModification();
                for ( int j = 0; j < forms[1].length; ++j ) {
                    if ( peptideUnprotected.equals( forms[1][j].getPeptide().sequenceNoModification() )) {
                        protectedIndex = j;
                        break;
                    }
                }
                if ( protectedIndex == -1 ) {
                    Utils.showErrorMessage("Peptide "+peptideUnprotected+" not found in bound state");
                    continue;
                }
                peptidelist.add( peptideUnprotected );                
                deuterium.add( forms[0][i].getAvgPercentAtTime( timePoint ) -
                        forms[1][protectedIndex].getAvgPercentAtTime( timePoint ) );
                System.out.println(peptidelist.get(peptidelist.size()-1)+"\t"+
                        deuterium.get(deuterium.size()-1));
            }

            deuteration = calculateResidueDeuteration (peptidelist, deuterium, sequence );   
            List<Color> colorlist = calculateColors( deuterium, true );
            Drawer.drawExchangeMap( sequence, peptidelist, colorlist );
        }
        writeHeatMap( deuteration, outPath );
    }
    
    ///@brief Draws exchange pattern for one state
    public static void createExchangeLines (HDRun[] form, File outPath, String sequence) {
        /// Get time point for protection map
        /// Use first instance of HDRun - TODO fix this to be better
        Double[] timePoints = new Double[form[0].getTimePoints().size()];
        timePoints = form[0].getTimePoints().toArray( timePoints );
        Double timePoint = (Double)JOptionPane.showInputDialog(
                            new JFrame(),
                            "Choose your time point to measure exchange",
                            "Exchange Map",
                            JOptionPane.PLAIN_MESSAGE,
                            new ImageIcon(),
                            timePoints,
                            timePoints[0]);
        
        List<String> peptidelist = new ArrayList();
        List<Double> deuterium = new ArrayList();
        for (int i = 0; i < form.length; i++) {
            peptidelist.add( form[i].getPeptide().sequenceNoModification() );                
            deuterium.add( form[i].getAvgPercentAtTime( timePoint ) );
//            System.out.println(peptidelist.get(peptidelist.size()-1)+"\t"+
//                    deuterium.get(deuterium.size()-1));
        }

        List<Color> colorlist = calculateColors( deuterium );
        Drawer.drawExchangeMap( sequence, peptidelist, colorlist );
    }
    
    ///@brief Draws protection lines for a free and bound state
    public static void createExchangeLines (HDRun[] free, HDRun[] bound, 
            File outPath, String sequence) {
        /// Get time point for protection map
        /// Use first instance of HDRun - TODO fix this to be better
        Double[] timePoints = new Double[free[0].getTimePoints().size()];
        timePoints = free[0].getTimePoints().toArray( timePoints );
        Double timePoint = (Double)JOptionPane.showInputDialog(
                            new JFrame(),
                            "Choose your time point to measure exchange",
                            "Exchange Map",
                            JOptionPane.PLAIN_MESSAGE,
                            new ImageIcon(),
                            timePoints,
                            timePoints[0]);
        

        List<String> peptidelist = new ArrayList();
        List<Double> deuterium = new ArrayList();
        for (int freeIndex = 0; freeIndex < free.length; freeIndex++) {
            // find index of peptide in the protected set
            int boundIndex = -1;
            String peptideUnprotected = free[freeIndex].getPeptide().sequenceNoModification();
            for ( int j = 0; j < bound.length; ++j ) {
                if ( peptideUnprotected.equals( bound[j].getPeptide().sequenceNoModification() )) {
                    boundIndex = j;
                    break;
                }
            }
            if ( boundIndex == -1 ) {
                Utils.showErrorMessage("Peptide "+peptideUnprotected+" not found in bound state");
                continue;
            }
            peptidelist.add( peptideUnprotected );                
            deuterium.add( free[freeIndex].getAvgPercentAtTime( timePoint ) -
                    bound[boundIndex].getAvgPercentAtTime( timePoint ) );
        }

        List<Color> colorlist = calculateColors( deuterium, true );
        Drawer.drawExchangeMap( sequence, peptidelist, colorlist );
    }
    
    ///@brief Calculate significance of peptide exchange between bound and unbound states
    public static void calculateSignificance (HDRun[] free, HDRun[] bound) {
        /// Get time point for protection map
        /// Use first instance of HDRun - TODO fix this to be better
        Double[] timePoints = new Double[free[0].getTimePoints().size()];
        timePoints = free[0].getTimePoints().toArray( timePoints );
        Double timePoint = (Double)JOptionPane.showInputDialog(
                            new JFrame(),
                            "Choose your time point to measure exchange",
                            "Exchange Map",
                            JOptionPane.PLAIN_MESSAGE,
                            new ImageIcon(),
                            timePoints,
                            timePoints[0]);
        
        double[] deuteration;                
        // If you're analyzing protection
        List<String> peptidelist = new ArrayList();
        List<Double> deuterium = new ArrayList();
        String printStr = "";
        for (int freeIndex = 0; freeIndex < free.length; freeIndex++) {
            // find index of peptide in the protected set
            int boundIndex = -1;
            String peptideUnprotected = free[freeIndex].getPeptide().sequenceNoModification();
            for ( int j = 0; j < bound.length; ++j ) {
                if ( peptideUnprotected.equals( bound[j].getPeptide().sequenceNoModification() )) {
                    boundIndex = j;
                    break;
                }
            }
            if ( boundIndex == -1 ) {
                Utils.showErrorMessage("Peptide "+peptideUnprotected+" not found in bound state");
                continue;
            }
            double[] unprotectedDeut = free[freeIndex].getPercentAtTime( timePoint );
            double[] protectedDeut = bound[boundIndex].getPercentAtTime( timePoint );
            double pValue = new TTest().homoscedasticTTest( unprotectedDeut, protectedDeut );
            printStr += peptideUnprotected+" "+String.format("%.2e", pValue) + "\n";
        }
        Utils.showMessage(printStr);
    }
    
    private static void printExchangeMap (HDRun[] forms, Double timePoint) {
        double[] deuteration;        
        
    // If you're analyzing exchange
        List<String> peptidelist = new ArrayList();
        List<Double> deuterium = new ArrayList();
        for (int i = 0; i < forms.length; i++) {
            peptidelist.add(Utils.trimPeptide(forms[i].getPeptide().sequence));
            Double avgValue = forms[i].getAvgPercentAtTime( timePoint );
            deuterium.add(avgValue);
            System.out.println(forms[i].getPeptide().displaySequence + "\t" + avgValue);
        }

    }
    
    
    private static void writeHeatMap( double[] deuteration, File outPath ) {  
        try {
            PrintWriter out = new PrintWriter( new FileWriter( outPath ) );
            for ( int i = 0; i < deuteration.length; ++i ) {
                String outstr = ((((Double)deuteration[i]).isNaN()) ?
                        "cmd.alter(\"resi "+Integer.toString(i+1)+"\", \"b=0\")"
                        : "cmd.alter(\"resi "+Integer.toString(i+1)+"\", \"b="+Double.toString(deuteration[i])+"\")" 
                        );
                out.println(outstr);
            }
        } catch (IOException e) {
            Utils.showErrorMessage ("Unable to create heat map");
        }        
    }
        
    private static double[] calculateResidueDeuteration (List<String> peptidelist, List<Double> deuterium, String sequence) {
        double[] sums = new double[sequence.length()];
        Arrays.fill(sums, 0.0);
        double[] scaleFactors = new double[sequence.length()];
        Arrays.fill(scaleFactors, 0.0);
        for (int j = 0; j < peptidelist.size(); j++) {
            String ex = peptidelist.get(j);
            int index = sequence.toUpperCase().indexOf(ex.toUpperCase());
            double scale = 1/(double)ex.length();
            if (index < 0) {
                Utils.showMessage("Residue not found: "+ex);
                continue;
            }
            for (int i = index; i < index + ex.length(); i++) {
                scaleFactors[i] += scale;
                sums[i] += (scale*deuterium.get(j));
            }
        }
        
        for (int i = 0; i < sums.length; i++) {
            sums[i] /= scaleFactors[i];
        }
        return sums;
    }
    
    public static void main ( String[] args ) {
        HDRun apo = Utils.readHDRun(new File("/Users/alexsevy/Documents/MSReader files/14N4 apo/hdx/L.GVRATTGDLDY.W.hdx"));
        HDRun bound = Utils.readHDRun(new File("/Users/alexsevy/Documents/MSReader files/14N4 scaffold bound/hdx/L.GVRATTGDLDY.W.hdx"));
        HDRun[] apoArr = new HDRun[] {apo};
        HDRun[] boundArr = new HDRun[] {bound};

        ProtectionMap.calculateSignificance (apoArr, boundArr);
//        HDRun[][] forms = new HDRun[1][];
//        forms[0] = new HDRun[] {run};
//        ProtectionMap.createExchangeLines (forms, new File("/Users/alexsevy/test"), 
//                "EVQLVESGGGLIQPGGSLRLSCAVSGFTVSSKYMTWVRQAPGKGLEWVSVIYGGGS"
//                +"TYYADSVVGRFTISRDNSKNTLYLQMNSLRAEDTAVYYCASRLGVRATTGDLDYWGQ"
//                +"GTLVTVSSASTKG");

        

        
//        String[] names = {"/Users/alexsevy/Documents/MSReader files/14N4 apo/A.VSGFTVSSK.Y.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/D.IKRTVAAPSV.F.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/D.IKRTVAAPSVF.I.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/F.TISRDNSKNTL.Y.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/F.TLTISSLQPDDF.A.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/L.ESGVPSRFSGSGSGTEF.T.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/L.VESGGGLIQ(+.98)PGGSLRL.S.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/L.VESGGGLIQ(+.98)PGGSLRLSC.A.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/M.NSLRAEDTAV.Y.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/M.NSLRAEDTAVY.Y.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/R.FTISRDNSKN(+.98)TLYLQM.N.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/T.WVRQ(+.98)APGKGLEW.V.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/W.LAWYQQ(+.98)KPGKAPKL.L.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/W.LAWYQQKPGKAPKL.L.hdx",
//            "/Users/alexsevy/Documents/MSReader files/14N4 apo/W.WTFGQGTKVD.I.hdx"};
//        HDRun[] hdrs = new HDRun[names.length];
//        for ( int i = 0; i < names.length; ++i ) {
//            hdrs[i] = Utils.readHDRun ( new File(names[i]) );
//        }
//        System.out.println("30");
//        ProtectionMap.printExchangeMap( hdrs, 30.0 );
//        System.out.println("60");
//        ProtectionMap.printExchangeMap( hdrs, 60.0 );
//        System.out.println("90");
//        ProtectionMap.printExchangeMap( hdrs, 90.0 );
    }
}
        