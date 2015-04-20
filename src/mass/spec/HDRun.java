package mass.spec;

/*
 * Primary function is to serialize deuterium exchange time points, to be 
 * able to save and retrieve exchange curves
 */

import java.io.Serializable;

public class HDRun implements Serializable{
    public Peptide peptide;
    public Object[][] exchangeValues;
    public double A;
    public double K;
    public String title;
    static final long serialVersionUID = 26009;
    
    public HDRun() { }
    
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
