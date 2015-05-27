package mass.spec;

/*
This class exists to serialize deuterium exchange time points, to be 
able to save and retrieve exchange curves. This enables smaller storage
size then having to save the entire HDExchange object itself
*/

import java.io.Serializable;

public class HDRun implements Serializable{
    public Peptide peptide;
    public Object[][] exchangeValues;
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
    
    public HDRun(HDX_Form txf) {
        title = txf.title;
        exchangeValues = txf.parent.exchange.getTable();
        A = txf.a;
        K = txf.k;
        peptide = txf.peptide;
    }

    public void setTitle(String t) {
        title = t;
    }
    public void setPeptide(Peptide p) {
        peptide = p;
    }
    
    public void setExchangeValues(Object[][] e) {
        exchangeValues = e;
    }
    
    public void setFittingConstants (double a, double k) {
        A = a;
        K = k;
    }
}
