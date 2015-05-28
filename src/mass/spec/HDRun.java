package mass.spec;

/*
This class exists to serialize deuterium exchange time points, to be 
able to save and retrieve exchange curves. This enables smaller storage
size then having to save the entire HDExchange object itself
*/

import java.io.Serializable;
import java.security.InvalidParameterException;
import org.jfree.data.xy.XYSeries;

public class HDRun implements Serializable{
    private Peptide peptide;
    private Object[][] exchangeValues;
    public double A;
    public double K;
    public String title;
    static final long serialVersionUID = 26009;
    
    public HDRun() { 
        exchangeValues = new Double[2][];
        double[][] temp = MSReader.getHDExchangeInstance().getSummaryData();
        exchangeValues[ 0 ] = Utils.doubleToObject( temp[ 0 ] );
        exchangeValues[ 1 ] = Utils.doubleToObject( temp[ 1 ] );
        peptide = MSReader.getHDExchangeInstance().getPeptide();
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
}
