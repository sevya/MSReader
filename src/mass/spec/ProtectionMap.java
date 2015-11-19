package mass.spec;

import java.io.*;
import java.util.*;

public class ProtectionMap {
    
    // Doesn't require pdb or pymol path
    public static void createProtectionMap (HDRun[][] forms, File outPath, String sequence) {
        double[] deuteration;
        int color;
        
        // If you're analyzing exchange
        if (forms.length == 1) {
            List<String> peptidelist = new ArrayList();
            List<Double> deuterium = new ArrayList();
            for (int i = 0; i < forms[0].length; i++) {
                peptidelist.add(Utils.trimPeptide(forms[0][i].getPeptide().sequence));
                // TODO pulls value from last time point - find a way to make this more flexible
                Object[][] pctValues = forms[0][i].getPercentValues();
                deuterium.add((Double)pctValues[1][pctValues[1].length-1]);
            }

            deuteration = calculateResidueDeuteration (peptidelist, deuterium, sequence );       
        // If you're analyzing protection
        } else { 
            Utils.showErrorMessage("Protection maps are under construction");
            return;
        }
        writeHeatMap( deuteration, outPath );

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
}
        