package mass.spec;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class holds information for an HD exchange run
 * Holds a list of Mass Spectrum, names of runs, for easy analysis
 * 
 */
public class HDExchange {
    
    public ArrayList<MassSpectrum> exchangeSpectra;
    public ArrayList<HDExchangeTimePoint> exchangePoints;
    private Peptide peptide;
    private HDX_Form hdxSummary;
    
    public HDExchange () {
        exchangeSpectra = new ArrayList();
    }
    
    public ArrayList<String> getExchangeSpectraTitles () {
        ArrayList<String> temp = new ArrayList();
        for ( MassSpectrum spec : exchangeSpectra ) {
            temp.add( spec.getFullTitle() );
        }
        return temp;
    }
    
    public void addSpectrum ( MassSpectrum ms ) {
        exchangeSpectra.add( ms );
    }
    
    public void removeSpectrum ( String fullTitle ) {
        for ( int i = 0; i < exchangeSpectra.size(); ++i ) {
            if ( exchangeSpectra.get( i ).getFullTitle().equals( fullTitle ) ) {
                exchangeSpectra.remove( i );
                break;
            }
        }
    }
    
    public void removeAllSpectra () {
        exchangeSpectra.clear();
    }
    
    public int numberOfScans () {
       return exchangeSpectra.size();
    }
    
    public Peptide getPeptide () { return peptide; }
    
    public void setPeptide ( Peptide pep ) { peptide = pep; }
    
    public boolean hasZeroTimePoint () {
        for ( HDExchangeTimePoint sample : exchangePoints ) { 
            if ( sample.getTimePoint() == 0 )  return true;
        }
        return false;
    }
    
    public double[][] getSummaryData () {
        double[][] summaryData = new double[ 2 ][ exchangePoints.size() ];
        for ( int i = 0; i < exchangePoints.size(); ++i ) {
            summaryData[ 0 ][ i ] = exchangePoints.get( i ).getTimePoint();
            if ( hasZeroTimePoint() ) {
                summaryData[ 1 ][ i ] = 
                        exchangePoints.get( i ).getCentroid() - 
                        exchangePoints.get( 0 ).getCentroid();
            } else {
                summaryData[ 1 ][ i ] = exchangePoints.get( i ).getCentroid();
            }
        }
        return summaryData;
    }
    
    public DefaultTableModel getSummaryDataAsTable () {
        return new DefaultTableModel( 
                FormatChange.ArrayToTable( getSummaryData() ), 
                new String[] {"time(min)", "centroid"} 
        );
    }
    
    public XYSeries getSummaryDataAsXYSeries () {
        return FormatChange.ArrayToXYSeries( getSummaryData() );
    }
    
    // Analyzes the loaded spectra to populate HDExchangeTimePoint objects
    // These hold the relevant data of each scan for the peptide of interest
    public void analyze () {
        Collections.sort( exchangeSpectra, new Comparator<MassSpectrum>() {
            @Override
            public int compare (MassSpectrum scan1, MassSpectrum scan2) {
                double time1 = Utils.getDeutTimePoint( scan1.getRunTitle() );
                double time2 = Utils.getDeutTimePoint( scan2.getRunTitle() );
                if ( time1 > time2 ) return 1;
                else if ( time1 < time2 ) return -1;
                else return 0;
            }
        });
        for ( MassSpectrum scan : exchangeSpectra ) {
            // TODO if peakIndex is -1 this means the spectrum has no X values - 
            // find a way to handle this correctly
            // if it is greater than the length of the array, find a way to handle it
            int peakIndex = scan.peakIndex( peptide.mz );
            if ( peakIndex > scan.msValues[0].length) {
                Utils.showErrorMessage("M/z peak not found. Try again...");
                return;
            }
            int windowSize = MSReader.getInstance().getIntProperty("windowSize");
            int startIndex = scan.peakIndex( peptide.mz - windowSize );
            int endIndex = scan.peakIndex( peptide.mz + windowSize );
            
            double[][] dataRange = scan.getRange( startIndex, endIndex);
            
            // Set up data for plotting in exchange window
            XYSeriesCollection dataset = new XYSeriesCollection();
            XYSeries series = FormatChange.ArrayToXYSeries( dataRange );
            dataset.addSeries(series);
            
//            DecimalFormat dataformat = FormatChange.getFormat(stepSize);
//            FormatChange.FormatArray(data[0], dataformat);
            
//            Exchange_Popup ep = new Exchange_Popup( dataRange, scan.getFullTitle() ); 
//            ep.setVisible(true);
            HDExchangeTimePoint timePoint = new HDExchangeTimePoint( dataRange, 
                Utils.getDeutTimePoint( scan.getRunTitle() ) 
            );
            exchangePoints.add( timePoint );
            
        }
        
        for ( HDExchangeTimePoint timePoint : exchangePoints ) {
            timePoint.showWindow();
        }
        
    }
}
