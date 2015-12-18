package mass.spec;

/*
This class exists to serialize deuterium exchange time points, to be 
able to save and retrieve exchange curves. This enables smaller storage
size then having to save the entire HDExchange object itself
*/

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jfree.data.xy.XYSeries;

public class HDRun implements Serializable{
    private Peptide peptide;
    private Object[][] exchangeValues;
    private Object[][] centroidValues;
    private Object[][] percentValues;
    public double A;
    public double K;
    public String title;
    static final long serialVersionUID = 26009;
    
    // Default constructor grabs HDExchange instance as a singleton from MSReader
    // trying to phase out HDExchange as a singleton to allow multiple objects
    public HDRun() { 
        this( MSReader.getHDExchangeInstance() );
    }
    
    public HDRun( HDExchange exchange ) {
        peptide = exchange.getPeptide();
        exchangeValues = new Double[2][];
        centroidValues = new Double[2][];
        percentValues = new Double[2][];
        double[][] temp = exchange.getSummaryData();
        exchangeValues[ 0 ] = Utils.doubleToObject( temp[ 0 ] );
        exchangeValues[ 1 ] = Utils.doubleToObject( temp[ 1 ] );
        temp = exchange.getCentroidData();
        centroidValues[ 0 ] = Utils.doubleToObject( temp[ 0 ] );
        centroidValues[ 1 ] = Utils.doubleToObject( temp[ 1 ] );
        percentValues[ 0 ] = new Double[exchangeValues[0].length];
        percentValues[ 1 ] = new Double[exchangeValues[1].length];
        System.arraycopy( exchangeValues[0], 0, percentValues[0], 0, exchangeValues[0].length);
        for ( int i = 0; i < exchangeValues[1].length; ++i ) {
            percentValues[1][i] = (100*((Double)centroidValues[1][i] - peptide.mz))/peptide.maxDeuteration();
        }
        
        title = peptide.sequence;
    }
    
    // Constructor directly setting values for testing purposes
    public HDRun( double[][] values ) { 
        if( values[ 0 ].length != values[ 1 ].length ) {
            Utils.showErrorMessage( "Error invalid HDRuns");
            return;
        }
        exchangeValues = new Double[2][];
        exchangeValues[ 0 ] = Utils.doubleToObject( values[ 0 ] );
        exchangeValues[ 1 ] = Utils.doubleToObject( values[ 1 ] );
    }
    
//    public HDRun(HDX_Form txf) {
//        title = txf.title;
//        exchangeValues = MSReader.getHDExchangeInstancetxf.parent.exchange.getTable();
//        A = txf.a;
//        K = txf.k;
//        peptide = txf.peptide;
//    }

    public void setTitle(String t) {
        title = t;
    }
    
    public Peptide getPeptide () { return peptide; } 
    
    public void setPeptide(Peptide p) {
        peptide = p;
    }
    
    public void setExchangeValues(Object[][] e) {
        exchangeValues = e;
    }
    
    public Object[][] getExchangeValues () { 
        Double avgZero = getAvgCentroidAtTime( 0.0 );
        for ( int i = 0; i < centroidValues[0].length; ++i ) {
            exchangeValues[ 1 ][ i ] = (Double)centroidValues[1][i] - avgZero;
        }
        return exchangeValues;
    }
    
    public Object[][] getPercentValues () {
        Double avgZero = getAvgCentroidAtTime( 0.0 );
        for ( int i = 0; i < centroidValues[0].length; ++i ) {
            percentValues[ 1 ][ i ] = 100*(((Double)centroidValues[1][i] - avgZero))/peptide.maxDeuteration();
        }
        return percentValues;
    }
    
    public Double getAvgCentroidAtTime( Double timePoint ) {
        Double avgValue = 0.0;
        int count = 0;
        for ( int ii = 0; ii < centroidValues[0].length; ++ii ) {
            if ( Math.abs(((Double)centroidValues[0][ii]) - timePoint) < Math.pow(10, -4)) {
                avgValue += (Double)centroidValues[1][ii];
                count++;
            }
        }
        avgValue /= count;
        return avgValue;
    }
    
    public Double getAvgPercentAtTime( Double timePoint ) {
        Object[][] pctValues = this.getPercentValues();
        Double avgValue = 0.0;
        int count = 0;
        for ( int ii = 0; ii < pctValues[0].length; ++ii ) {
            if ( Math.abs((Double)pctValues[0][ii] - timePoint) < Math.pow(10, -4) ) {
                avgValue += (Double)pctValues[1][ii];
                count++;
            }
        }
        avgValue /= count;
        return avgValue;
    }
    
    public double[] getPercentAtTime( Double timePoint ) {
        Object[][] pctValues = this.getPercentValues();
        List<Double> values = new ArrayList();
        for ( int ii = 0; ii < pctValues[0].length; ++ii ) {
            if ( Math.abs((Double)pctValues[0][ii] - timePoint) < Math.pow(10, -4) ) {
                values.add( (Double)pctValues[1][ii] );
            }
        }
        return FormatChange.ArraylistToArray(values);
    }
    
    public List<Double> getTimePoints() {
        List<Double> list = Arrays.asList( 
                FormatChange.ObjectArraytoDouble( centroidValues[0] 
                ) );
        Set<Double> set = new HashSet();
        set.addAll(list);
        list = new ArrayList<Double>();
        list.addAll(set);
        Collections.sort( list );
        return list;
    }
    
    public void setFittingConstants (double a, double k) {
        A = a;
        K = k;
    }
    
    public XYSeries toXYSeries () {
        // TODO: add some logic for including regression line with A and K parameters
        XYSeries series = new XYSeries( title );
        Object[][] pctValues = getPercentValues();
        for (int i = 0; i < exchangeValues[0].length; ++i) {
            series.add( (double)(Double)pctValues[ 0 ][ i ], (double)(Double)pctValues[ 1 ][ i ]);
        } 
        return series;
    }
    
    public String getSummaryString() {
        DecimalFormat format = new DecimalFormat("###.##");
        String outstr = "";
        outstr += peptide.displaySequence;
        outstr += "\tTime(min)\t";
        for ( Object obj : centroidValues[ 0 ] ) { 
            outstr += format.format(obj);
            outstr += "\t";
        }
        outstr += "\n\tCentroid\t";
        for ( Object obj : centroidValues[ 1 ] ) { 
            outstr += format.format(obj);
            outstr += "\t";
        }
        //Hack to fix the problem in calculating difference from multiple zero time points
        Double avgZero = 0.0;
        int count = 0;
        for ( int i = 0; i < exchangeValues[0].length; ++i ) {
            if ( Math.abs(((Double)centroidValues[0][i]) - 0) < Math.pow(10, -4)) {
                avgZero += (Double)centroidValues[1][i];
                count++;
            }
        }
        avgZero /= count;
        outstr += "\n\tCentroid delta\t";
        for ( Object obj : centroidValues[ 1 ] ) {
            outstr += format.format(((Double)obj - avgZero));
            outstr += "\t";
        }
        outstr += "\n\t% Deut\t";
        for ( Object obj : centroidValues[ 1 ] ) {
            outstr += format.format(100*(((Double)obj - avgZero))/peptide.maxDeuteration());
//            outstr += format.format(obj);
            outstr += "\t";
        }
        outstr += "\n";
        return outstr;
    }
    
    public static void main ( String[] args ) {
        HDRun hdr = Utils.readHDRun( new File( "/Users/alexsevy/Documents/MSReader files/14N4 apo/hdx pt 2/L.LIYDASSLESGVPSRFSGSGSGTE.F.hdx" ) );

        System.out.println( hdr.getAvgPercentAtTime(30.0) );
    }
}
