
package mass.spec;

import javax.swing.table.DefaultTableModel;

public class MSMath {

    public static double getMin (double[] array) {
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (Double.isNaN(array[i])) continue;
            else if (Double.isNaN(min)) min = array[i];
            else if (array[i] < min) min = array[i];
        }
        return min;
    }
    
    public static float getMax (float[] data) {
        return getMax (data, 0, data.length);
    }
    
    public static double getMax (double[] data) {
        return getMax (data, 0, data.length);
    }
    
    public static Double getMax (Object[] data) {
        Double max = Double.MIN_VALUE;
        for ( int i = 0; i < data.length; ++i ) {
            if ( (Double)data[i] > max ) {
                max = (Double)data[i];
            }
        }
        return max;
    }
    
    public static int getMaxIndex (double[] data) {
        return getMaxIndex (data, 0, data.length);
    }
    
    public static int getMaxIndex (double[] data, int start, int end) {
        double max = Double.MIN_VALUE;
        int maxind = -1;
        for (int i = start; i < end; i++) {
            if (data[i] > max) {
                max = data[i];
                maxind = i;
            }
        }
        return maxind;
    }
    
    public static float getMax (float[] data, int start, int end) {
        float max = Float.MIN_VALUE;
        for (int i = start; i < end; i++) {
            if (data[i] > max) max = data[i];
        }
        return max;
    }
    
    public static double getMax (double[] data, int start, int end) {
        double max = Double.MIN_VALUE;
        for (int i = start; i < end; i++) {
            if (data[i] > max) max = data[i];
        }
        return max;
    }
    
    public static boolean isDouble (String ex) {
        try {
            Double.parseDouble(ex);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
      
    public static boolean isInt (String st) {
        try {
            Integer.parseInt(st);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }  

    public static double calcCentroid (double [][] data) {
          double xysum = 0, ysum = 0; 
          for (int i = 0; i<data[0].length; i++) {
                xysum += data[0][i] * data[1][i];
                ysum += data[1][i];
            }
          return (xysum/ysum);
    }
    
    public static double calcCentroid (float [][] data) {
          double xysum = 0, ysum = 0; 
          for (int i = 0; i<data[0].length; i++) {
                xysum += data[0][i] * data[1][i];
                ysum += data[1][i];
            }
          return (xysum/ysum);
    }
    
    public static double mwEstimate(String seq) {
        double sum = 0;
        for (int i = 0; i < seq.length(); i++) {
            sum += Utils.getValueOf(seq.substring(i, i+1));
        }
        sum -= (18*(seq.length()-1));
        return sum;
    }
    
    public static double getTableXMax (DefaultTableModel dtm) {
        double max = 0;
        for (int i =0; i<dtm.getRowCount(); i++) {
            if ((Double)dtm.getValueAt(i, 0) > max) {
                max = (Double)dtm.getValueAt(i, 0);
            }
        }
        return max;
    }
       
    public static double getScore (float[][] data, double[][] isotope) {
        isotope = Utils.sort2DArray(isotope, 0);
        double sum = 0;
        int index = 0;
        for (int i = 0; i < isotope[0].length; i++) {
            for (int j = index; j < data[0].length-1; j++) {
                if (isotope[0][i] > data[0][j] && isotope[0][i] < data[0][j+1]) {
                    index = j;
                    double y = valAt(data[0][j], data[1][j], data[0][j+1], data[1][j+1], isotope[0][i]);
                    if (isotope[1][i] > y) sum += (isotope[1][i] - y);
                    break;
                }
            }
        }
        double ysum = 0;
        for (int i = 0; i < isotope[0].length; i++) {
            ysum += isotope[1][i];
        }
        return 1 - (sum/ysum);
    }
    
    public static double valAt (double x1, double y1, double x2, double y2, double xeval) {
        double slope = (y2 - y1) / (x2 - x1);
        return (slope * (xeval - x1) + y1);
    }
    
    public static double valAtY (double x1, double y1, double x2, double y2, double yeval) {
        double slope = (y2 - y1) / (x2 - x1);
        return ((yeval-y1)/slope)+x1;
    }
}
