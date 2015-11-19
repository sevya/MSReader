package mass.spec;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MassSpectrum implements Serializable{
    public double[][] msValues;
    //alternate implementation without holding x values
    public double[] yvals;
    //
    public String msTitle;
    public String runTitle;
    private double retentionTime; 
    
    public MassSpectrum (double[][] values, String title) {
        msValues = new double[2][values[0].length];
//        System.arraycopy (values[0], 0, msValues[0], 0, values[0].length);
//        System.arraycopy (values[1], 0, msValues[1], 0, values[1].length);
        msValues = values;
        msTitle = title;
    }
    
    //alternate implementation without holding x values
    public MassSpectrum (double[] values, String str) {
        yvals = new double[values.length];
        System.arraycopy (values, 0, yvals, 0, values.length);
        msTitle = str;
    }
    
    public MassSpectrum(double[] x, double[] y ) {
        if (x.length != y.length) throw new IndexOutOfBoundsException();
        msValues = new double [2][x.length];
        System.arraycopy(x, 0, msValues[0], 0, x.length);
        System.arraycopy(y, 0, msValues[1], 0, y.length);
    }
    
    public MassSpectrum(double[] x, double[] y, String run_title, String ms_title ) {
        if (x.length != y.length) throw new IndexOutOfBoundsException();
        msValues = new double [2][x.length];
        System.arraycopy(x, 0, msValues[0], 0, x.length);
        System.arraycopy(y, 0, msValues[1], 0, y.length);
        runTitle = run_title;
        msTitle = ms_title;
    }
    
    public String getRunTitle () { return runTitle; }
    
    public String getSpectrumTitle () { return msTitle; }
    
    public String getFullTitle () { return msTitle+" "+runTitle; }
    
    public void setRunTitle ( String title ) { runTitle = title; }
    
    public void setSpectrumTitle ( String title ) { msTitle = title; }
    
    public void setRetentionTime ( double rt ) { retentionTime = rt; }
    
    public double getRetentionTime () { return retentionTime; }
    
    public int peakIndex ( double peakValue ) {
        if ( msValues == null ) {
            return -1;
        } else {
            return Utils.binarySearch( msValues[0], peakValue );
        }
    }
    
    public double[][] getRange ( int startIndex, int endIndex ) {
        double[][] temp = new double [2][];
        temp[ 0 ] = Arrays.copyOfRange(this.msValues[0], startIndex, endIndex);
        temp[ 1 ] = Arrays.copyOfRange(this.msValues[1], startIndex, endIndex);
        return temp;
    }
    
    public double[][] getWindow ( double peakValue, double windowSize ) {
        int startIndex = peakIndex( peakValue - windowSize );
        int endIndex = peakIndex( peakValue + windowSize );

        return getRange( startIndex, endIndex);
    }
    public double getYMax() {
        if (msValues == null) return getYMax (0, yvals.length);
        else return getYMax (0, msValues[1].length);
    }
    
    public double getYMax (int start, int end) {
        double max;
        if (msValues == null) {
            max = yvals[start];
            for (int i = start; i < end; i++) {
                if (yvals[i] > max) max = yvals[i];
            }
        } else {
            max = msValues[1][start];
            for (int i = start; i < end; i++) {
                if (msValues[1][i] > max) max = msValues[1][i];
            }
        }
        return max;
    }
        
    public void smoothSavitzkyGolay (int window, int degree) {
        SavitzkyGolay sg = new SavitzkyGolay (msValues, window, degree);
        for (int i = window; i < msValues[0].length-window; i++) {
            msValues[1][i] = sg.getSmoothedPoint(i);
        }
    }
    
    public void smoothSavitzkyGolay (double[] xvals, int window, int degree) {
        SavitzkyGolay sg = new SavitzkyGolay (xvals, yvals, window, degree);
        for (int i = window; i < xvals.length-window; i++) {
            yvals[i] = sg.getSmoothedPoint(i);
        }
    }
    
    public void smoothMovingAverage (int filter) {
        double sum = 0.0;
        if (filter%2 == 0 ) {
            filter -= 1;
        }
        int window = (filter-1)/2;
        if (msValues == null) {
            for (int i = window; i < yvals.length-window; i++){
                for (int j = -1*window; j<=window; j++) {
                    sum += yvals[i-j];
                }
                yvals[i] = sum/filter;
                sum = 0;
            }
        } else {
            for (int i = window; i < msValues[1].length-window; i++){
                for (int j = -1*window; j<=window; j++) {
                    sum += msValues[1][i-j];
                }
            msValues[1][i] = sum/filter;
            sum = 0;
            }
        }
    }
    
    // Convert spectrum to non-uniform if it's not already
    // this means copying over the m/z values from the MSChrom parent object
    // to the child MassSpectrum object
    public MassSpectrum convertToNonUniform ( MSChrom c ) {
        MassSpectrum returner;
        if ( c.SPECTRA_UNIFORM ) {
            returner = new MassSpectrum( c.mz_values, this.yvals );
            returner.setRunTitle( this.getRunTitle() );
            returner.setSpectrumTitle( this.getSpectrumTitle() );
            return returner;
        } else {
            returner = new MassSpectrum( this.msValues, this.getSpectrumTitle() );
            returner.setRunTitle( this.getRunTitle() );
            return returner;
        }
        
    }
    
    public static void main (String[] args) {
        boolean bool = false;
        double x = .7;
        if (x > ((bool)?.5:.8)) System.out.println("yep"); 
        else System.out.println("nope");
    }
}

