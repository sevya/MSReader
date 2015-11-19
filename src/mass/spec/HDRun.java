package mass.spec;

/*
This class exists to serialize deuterium exchange time points, to be 
able to save and retrieve exchange curves. This enables smaller storage
size then having to save the entire HDExchange object itself
*/

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
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
            percentValues[1][i] = (100*(Double)exchangeValues[1][i])/peptide.maxDeuteration();
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
    
    public Object[][] getExchangeValues () { return exchangeValues; }
    
    public Object[][] getPercentValues () { return percentValues; }
    
    public void setFittingConstants (double a, double k) {
        A = a;
        K = k;
    }
    
    public XYSeries toXYSeries () {
        // TODO: add some logic for including regression line with A and K parameters
        XYSeries series = new XYSeries( title );
        for (int i = 0; i < exchangeValues[0].length; ++i) {
            series.add( (double)(Double)exchangeValues[ 0 ][ i ], (double)(Double)exchangeValues[ 1 ][ i ]);
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
        outstr += "\n\tCentroid delta\t";
        for ( Object obj : exchangeValues[ 1 ] ) { 
            outstr += format.format(obj);
            outstr += "\t";
        }
        outstr += "\n\t% Deut\t";
        for ( Object obj : percentValues[ 1 ] ) { 
            outstr += format.format(obj);
            outstr += "\t";
        }
        outstr += "\n";
        return outstr;
    }
}
