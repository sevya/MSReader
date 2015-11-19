package mass.spec;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;


public class Utils {
    
    public static String osjoin (String one, String two) {
        return one + File.separator + two;
    }
    
    public static File osjoin (File one, String two) {
        return new File (one + File.separator + two);
    }
    
    public static void logException (File loc, Exception e, String str) {
        File writer = Utils.osjoin(loc,"logger.txt");
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(writer, true)));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            out.println(dateFormat.format(date));
            if (str != null) out.println(str);
            out.println(e.toString());
            for (StackTraceElement item: e.getStackTrace()) {
                out.println(item.toString());
            }
            out.println("\n\n\n");
            out.close();
        } catch (IOException io) {
            e.printStackTrace();
        }
    }
    
    public static void logException ( Exception e, String str) {
        File writer = Utils.osjoin( MSReader.getInstance().bin,"logger.txt");
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(writer, true)));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            out.println(dateFormat.format(date));
            if (str != null) out.println(str);
            out.println(e.toString());
            for (StackTraceElement item: e.getStackTrace()) {
                out.println(item.toString());
            }
            out.println("\n\n\n");
            out.close();
        } catch (IOException io) {
            e.printStackTrace();
        }
    }
    
    public static void showErrorMessage (String str) {
        JOptionPane.showMessageDialog(null, str, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showMessage (String str) {
        JOptionPane.showMessageDialog (null, str);
    }
    
    public static void logException (File loc, Exception e) {
        logException (loc, e, null);
    }
    
    public static void logException ( Exception e) {
        logException ( MSReader.getInstance().bin, e, null);
    }
    
    // binary search of an array that returns the insertion point of the target
    public static int binarySearch (double[] array, double key) {
        int index = Arrays.binarySearch (array, key);
        if (index < 0) index = -(index + 1);
        return index;
    }
    
    // Generic method for binary search of a list
    public static int binarySearch ( List<Double> array, Double key ) {
        int index = Collections.binarySearch (array, key);
        if (index < 0) index = -(index + 1);
        return index;
    }
    
    public static int closestTo ( List< Double > array, Double key ) {
        Double minDistance = Math.pow( key - array.get( 0 ), 2 );
        int minIndex = 0;
        for ( int i = 1; i < array.size(); ++i ) {
            if ( Math.pow( key - array.get( i ), 2 ) < minDistance ) {
                minDistance = Math.pow( key - array.get( i ), 2 );
                minIndex = i;
            }
        }
        return minIndex;
    }
        
    public static String trimPeptide (String str) {
        str = str.trim();
        int spot = str.indexOf(".");
        if (spot != -1) str = str.substring(spot+1, str.length());
        spot = str.indexOf(".");
        if (spot != -1) str = str.substring(0, spot);
        
        spot = str.indexOf("-");
        if (spot != -1) str = str.substring(spot+1, str.length());
        spot = str.indexOf("-");
        if (spot != -1) str = str.substring(0, spot);
        
        spot = str.indexOf("_");
        if (spot != -1) str = str.substring(spot+1, str.length());
        spot = str.indexOf("_");
        if (spot != -1) str = str.substring(0, spot);
        
        spot = str.indexOf("|");
        if (spot != -1) str = str.substring(spot+1, str.length());
        spot = str.indexOf("|");
        if (spot != -1) str = str.substring(0, spot);

        return str;
    }
    
        
    public static double[][] sort2DArray (double[][] values, final int sortIndex) {
        if (values[0].length != values[1].length) throw new InvalidParameterException();
        double[][] temp = new double [values[0].length][2];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = new double[] {values[0][i], values[1][i]};
        }
        Arrays.sort(temp, new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                if (o1[sortIndex] > o2[sortIndex]) return 1;
                else if (o1[sortIndex] < o2[sortIndex]) return -1;
                else return 0;
            }
            
        });
        double [][] ret = new double [2][values[0].length];
        for (int i = 0; i < ret[0].length; i++) {
            ret[0][i] = temp[i][0];
            ret[1][i] = temp[i][1];
        }
        return ret;
    }
    
        
    public static void dtmsort (DefaultTableModel dtm, final int column) {
        Double[][] table = FormatChange.DTMToTable(dtm);
        Arrays.sort(table, new Comparator<Double[]>() {
            @Override
            public int compare(Double[] a, Double[] b) {
 return Double.compare(a[column], b[column]);
            }
        }); 
        String[] names = new String[dtm.getColumnCount()];
        for (int i = 0; i < names.length; i++) {
            names[i] = dtm.getColumnName(i);
        }
        dtm = new DefaultTableModel (table, names);
    }
    
        
    public static double getValueOf (String amino) {
        amino = amino.toUpperCase();
        if ( "I".equals( amino ) ) return 131.1736;
        else if ( "L".equals( amino ) ) return 131.1736;
        else if ( "K".equals( amino ) ) return 146.1882;
        else if ( "M".equals( amino ) ) return 149.2124;
        else if ( "T".equals( amino ) ) return 119.1197;
        else if ( "W".equals( amino ) ) return 204.2262;
        else if ( "V".equals( amino ) ) return 117.1469;
        else if ( "R".equals( amino ) ) return 174.2017;
        else if ( "H".equals( amino ) ) return 155.1552;
        else if ( "F".equals( amino ) ) return 165.19;
        else if ( "A".equals( amino ) ) return 89.0935;
        else if ( "N".equals( amino ) ) return 132.1184;
        else if ( "D".equals( amino ) ) return 133.1032;
        else if ( "C".equals( amino ) ) return 121.159;
        else if ( "E".equals( amino ) ) return 147.1299;
        else if ( "Q".equals( amino ) ) return 146.1451;
        else if ( "G".equals( amino ) ) return 75.0669;
        else if ( "P".equals( amino ) ) return 115.131;
        else if ( "S".equals( amino ) ) return 105.093;
        else if ( "Y".equals( amino ) ) return 181.1894;
        else return 0;
    }
    
    public static String[] trypsindigest (String str, int missed) {
        ArrayList<Integer> indices = new ArrayList();
        ArrayList<String> peptides = new ArrayList();
        char[] tiago = str.toUpperCase().toCharArray();
        for (int i = 0; i < tiago.length; i++) {
            if (tiago[i]==('K') || tiago[i]==('N')) {
                indices.add(i+1);
            }
        }
        if (!indices.contains(0)) indices.add(0);
        if (!indices.contains(str.length())) indices.add(str.length());
        Collections.sort(indices);
        //for (int a: indices) System.out.println(a);
        int j = 1;
        while (j <= (missed+1)) { 
            for (int i = 0; i < indices.size() - j; i++) {
                peptides.add(str.substring(indices.get(i), indices.get(i+j)));
            }
            j++;
        }
        return FormatChange.StringlistToArray(peptides);
    }
    
    public static String[] all_peptide_subsets(String str ) {
        ArrayList<String> temp_list = new ArrayList<String>();
        for ( int i = 0; i < str.length() - 1; ++i ) {
            for ( int j = i+1; j < str.length(); ++j ) {
                temp_list.add( str.substring( i, j ) );
            }
        }
        String[] temp_arr = new String[temp_list.size()];
        temp_arr = temp_list.toArray( temp_arr );
        return temp_arr;
    }
    
    public static String[] pepsincleavage(String str, int missed) {
        String temp;
        ArrayList<Integer> indices = new ArrayList();
        ArrayList<String> peptides = new ArrayList();
        for (int i = 0; i < str.length() - 1; i++) {
            temp = str.substring(i, i+1);
            if (temp.toLowerCase().equals("a") || temp.toLowerCase().equals("v") ||
                    temp.toLowerCase().equals("l") || temp.toLowerCase().equals("i") ||
                    temp.toLowerCase().equals("f") || temp.toLowerCase().equals("y") ||
                    temp.toLowerCase().equals("w")) {
                indices.add(i);
            }
        }
        
        for (int i = 0; i < indices.size() - 1; i++) {
            for (int j = 1; j <= missed; j++) {
                if (i+j >= indices.size()) break;
                peptides.add(str.substring(indices.get(i), indices.get(i+j)));
                peptides.add(str.substring(indices.get(i) + 1, indices.get(i+j)));
                peptides.add(str.substring(indices.get(i), indices.get(i+j)+1));
                peptides.add(str.substring(indices.get(i) + 1, indices.get(i+j)+1));
            }
        }
        return FormatChange.StringlistToArray(peptides);    
    }
    
    //splits arrays for use in multi threaded operations
    //returns an two dimensional int array
    //a collection of arrays holding two ints each
    //the two ints in the array represent the start and end point (not inclusive)
    
    public static <T> int[][] getSplitArrayIndices (T[] array, int parts) {
        return getSplitArrayIndices (array, parts, 0, array.length);
    }
    
    public static <T> int[][] getSplitArrayIndices (T[] array, int parts, int start, int end) {
        int indiv_length = (end-start)/parts;
        int remainder = (end-start)%parts;
        if (remainder == 0) {
            int[][] divided = new int [parts][2];
            for (int i = 0; i < divided.length; i++) {
                divided[i] = new int[] {i*indiv_length+start, (i+1)*indiv_length+start};
            }
            return divided;
        } else {
            int[][] divided = new int [parts][indiv_length];
            for (int i = 0; i < divided.length; i++) {
                if (i == divided.length-1) {
                    divided[i] = new int[] {i*indiv_length+start, (i+1)*(indiv_length)+remainder+start};
                }
                else {
                    divided[i] = new int[] {i*indiv_length+start, (i+1)*(indiv_length)+start};
                }
            }
            return divided;
        }
    }
    
    // Gets time point in minutes based on name of file
    // Must be named with D20Xmin(hr)
    // Returns -1 if the name is invalid
    public static double getDeutTimePoint (String name) {
        double val = -1;
        boolean min = true;
        name = name.replaceAll("_", "."); 
        if (name.toLowerCase().contains("hour") || 
                name.toLowerCase().contains("hr")) {
            min = false;
        }
        if (name.toLowerCase().contains("h2")) val = 0; 
        else {
            try {
                int spot = name.toLowerCase().indexOf("d2");
                String str = name.substring(spot+3, name.length());
                while (!MSMath.isDouble(str)) {
                    str = str.substring(0, str.length() - 1);
                }
                val = Double.parseDouble(str);
                if (!min) val *= 60; 
            } catch (StringIndexOutOfBoundsException e) {
                return -1;
            }
        }
        return val;
    }
    
    public static JFreeChart drawChart( XYSeriesCollection dataset, String title, String xlabel, String ylabel ) {
        return ChartFactory.createXYLineChart(
                title,
                xlabel,
                ylabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
                );
    }
    
    public static void normalize( double[] array ) {
        double max = MSMath.getMax( array );
        for ( int i = 0; i < array.length; ++i ) array[ i ] /= max;
    }
    
    public static double[] doubleToPrimitive( Double[] array ) {
        double[] prim_arr = new double[ array.length ];
        for ( int i = 0; i < array.length; ++i ) {
            prim_arr[ i ] = (double)array[i];
        }
        return prim_arr;
    }
    
    public static Double[] doubleToObject( double[] array ) {
        Double[] obj_arr = new Double[ array.length ];
        for ( int i = 0; i < array.length; ++i ) {
            obj_arr[ i ] = (Double)array[i];
        }
        return obj_arr;
    }
    
    public static Double[][] compare( HDRun sample1, HDRun sample2 ) {
        Object[][] values1 = sample1.getExchangeValues();
        Object[][] values2 = sample2.getExchangeValues();
        // Check that both runs have the same number of time points
        assert values1[ 0 ].length == values2[ 0 ].length;
        
        Double[][] difference = new Double[ 2 ][ values1[ 0 ].length ];
        for ( int i = 0; i < values1[ 0 ].length; ++i ) {
            // Check that both runs have the same values for each time point
            if ( !values1[ 0 ][ i ].equals( values2[ 0 ][ i ] ) ) {
                throw new IndexOutOfBoundsException();
            }
            difference[ 0 ][ i ] = (Double)values1[ 0 ][ i ];
            difference[ 1 ][ i ] = (Double)values1[ 1 ][ i ] - (Double)values2[ 1 ][ i ];
        }
        return difference;
    }
    
    public static void main (String[] args)
    {
        Peptide pept = new Peptide("E.ASLGVSSACPYQGKSSF.F", 
               2, 12.01);
        System.out.println(pept.sequence);
        System.out.println(pept.displaySequence);
    }
}
