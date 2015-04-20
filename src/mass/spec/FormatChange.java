
package mass.spec;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.xy.XYSeries;

public class FormatChange {
    
    public static Double[][] ArrayListToTable(ArrayList<Double> x, ArrayList<Double> y) {
        Double[][] table = new Double [x.size()][2];
        for (int i = 0; i < x.size(); i++) {
            table[i][0] = x.get(i);
            table[i][1] = y.get(i);
        }
        return table;
    }

    public static DecimalFormat getFormat (double step) {
        if (step >= 1) return new DecimalFormat("####");
        else if (step >= .1) return new DecimalFormat("####.#");
        else if (step >= .01) return new DecimalFormat("####.##");
        else return new DecimalFormat("####.###");
    }
    
    public static void FormatArray (double[] array, DecimalFormat form) {
        for (int i = 0; i < array.length; i++) array[i] = Double.parseDouble(form.format(array[i]));
    }
    
    public static Object[][] ArrayToTable (double [][] array) {
        Object[][] table = new Object [array[0].length][array.length];
        for (int i = 0; i < array[0].length; i++) {
            for (int j = 0; j < array.length; j++) {
                table[i][j] = array[j][i];
            }
        } return table;
    }
    
    public static Object[][] ArrayToTable (double[][] array, int start, int finish) {
        Object[][] table = new Object [finish - start + 1][array.length];
        int k = 0;
        for (int i = start; i <= finish; i++) {
            for (int j = 0; j < array.length; j++) {
                table[k][j] = array[j][i];
            }
            k++;
        }
        return table;
    }
    
    public static Object[][] ArrayToTable (Double[][] array, int start, int finish) {
        Object[][] table = new Object [finish - start][array.length];
        int k = 0;
        for (int i = start; i < finish; i++) {
            for (int j = 0; j < array.length; j++) {
                table[k][j] = array[j][i];
            }
            k++;
        }
        return table;
    }
    
    public static Object[][] ArrayToTable (double[][] array, int start, int finish, DecimalFormat df, int formatrow) {
        Object[][] table = new Object [finish - start][array.length];
        int k = 0;
        for (int i = start; i < finish; i++) {
            for (int j = 0; j < array.length; j++) {
                table[k][j] = (j == formatrow) ? df.format(array[j][i]) : array[j][i];
            }
            k++;
        }
        return table;
    }
    
    public static void main (String[] args) {
        List<Integer> list = new ArrayList();
        list.add(2);
        list.add(2);
        list.add(-2);
        list.add(-2);
        System.out.println(Collections.frequency(list, -2)==2);
    }
    public static XYSeries ArrayToXYSeries (double[][] values) {
        if (values.length != 2) throw new InvalidParameterException();
        return ArrayToXYSeries(values[0], values[1], 0, values[0].length, "");
    }
    
    public static XYSeries ArrayToXYSeries (double[][] values, String key) {
        if (values.length != 2) throw new InvalidParameterException();
        return ArrayToXYSeries (values[0], values[1], 0, values[0].length, key);
    }
    
    public static XYSeries ArrayToXYSeries (double[] xvals, double[] yvals) {
        return ArrayToXYSeries (xvals, yvals, 0, xvals.length, "");
    }
    
    public static XYSeries ArrayToXYSeries (double[] xvals, double[] yvals, String str) {
        return ArrayToXYSeries (xvals, yvals, 0, xvals.length, str);
    }
    
    public static XYSeries ArrayToXYSeries (double[] xvals, double[] yvals, int start, int end) {
        return ArrayToXYSeries (xvals, yvals, start, end, "");
    }
    
    public static XYSeries ArrayToXYSeries (double[] xvals, double[] yvals, int start, int end, String key) {
        if (xvals.length != yvals.length) throw new InvalidParameterException();
        XYSeries series = new XYSeries(key);
        for (int i = start; i < end; i++) {
            series.add(xvals[i], yvals[i]);
        } return series;
    }
    
    public static XYSeries ArrayToXYSeries (double[][] values, int startIndex, int endIndex) {
        if (values.length != 2) throw new InvalidParameterException();
        return ArrayToXYSeries (values[0], values[1], startIndex, endIndex, "");
    }
    
    public static XYSeries ArrayToXYSeries (double[][] values, int startIndex, int endIndex, String key) {
        if (values.length != 2) throw new InvalidParameterException();
        return ArrayToXYSeries (values[0], values[1], startIndex, endIndex, key);
    }
    
    public static double[][] DTMToArray (DefaultTableModel dtm) throws NumberFormatException{
        double[][] ex = new double [dtm.getColumnCount()][dtm.getRowCount()];
        for (int i = 0; i < dtm.getRowCount(); i++) {
            for (int j = 0; j < dtm.getColumnCount(); j++) {
                try {
                    ex[j][i] = Double.parseDouble(dtm.getValueAt(i, j).toString());
                } catch (Exception e) {
                    throw new NumberFormatException(); 
                }
            }
        }
        return ex;
    }
    
    public static double [] DTMToArray(DefaultTableModel dtm, int column) throws NumberFormatException{
        double[] ex = new double [dtm.getRowCount()];
        for (int i = 0; i < dtm.getRowCount(); i++) {
            try {
                ex[i] = (Double)dtm.getValueAt(i, column); 
            } catch (Exception e) { throw new NumberFormatException(); }
        }
        return ex;
    }
    
    public static Double[][] DTMToTable (DefaultTableModel dtm) throws NumberFormatException {
        Double[][] ex = new Double [dtm.getRowCount()][dtm.getColumnCount()];
        for (int i = 0; i < dtm.getRowCount(); i++) {
            for (int j = 0; j < dtm.getColumnCount(); j++) {
                try {
                    ex[i][j] = Double.parseDouble(dtm.getValueAt(i, j).toString());
                } catch (Exception e) { throw new NumberFormatException(); }
            }
        }
        return ex;
    }
    
    public static DefaultTableModel PeptidesToDTM (PeptideList t) {
        Object[][] ex = new Object[t.size()][4];
        Peptide pept;
        DecimalFormat form = new DecimalFormat("#####.###");
        for (int i = 0; i < t.size(); i++) {
            pept = t.elementAt(i);
            ex[i][0] = pept.displaySequence;
            ex[i][1] = form.format(pept.mz);
            ex[i][2] = pept.charge;
            ex[i][3] = pept.elutiontime;
        }
        return new DefaultTableModel(ex, t.labels);
    }
    
    public static String[] StringlistToArray (ArrayList<String> array) {
        String[] arr = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            arr[i] = array.get(i);
        }
        return arr;
    }
    
    public static Peptide[] PeptidelistToArray (ArrayList<Peptide> array) {
        Peptide[] arr = new Peptide[array.size()];
        for (int i = 0; i < array.size(); i++) {
            arr[i] = array.get(i);
        }
        return arr;
    }
    
    public static double[] ArraylistToArray (List<Double> al) throws NumberFormatException{
        double [] ex = new double [al.size()];
        for (int i = 0; i < al.size(); i++) {
            try {
                ex[i] = (double)al.get(i);
            } catch (Exception e) { 
                return null;
            }
        }
        return ex;
    }
    
    public static MassSpectrum[] MSArraylistToArray (ArrayList<MassSpectrum> al) throws NumberFormatException{
        MassSpectrum [] ex = new MassSpectrum [al.size()];
        for (int i = 0; i < al.size(); i++) {
            try {
                ex[i] = (MassSpectrum)al.get(i);
            } catch (Exception e) { throw new NumberFormatException(); }
        }
        return ex;
    }
    
    public static ArrayList ArrayToArraylist (Double[] al) {
        ArrayList<Double> ex = new ArrayList(0);
        ex.addAll(Arrays.asList(al));
        return ex;
    }
}
