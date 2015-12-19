package mass.spec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.xy.XYSeries;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

/*
This class holds information for the overall HD exchange experiment. This 
encompasses individual spectra with raw data, exchange time point objects, 
the peptide of interest to be analyzed, and an HDX Form summary graph.

TODO: figure out a way to prevent all spectrum from being held twice, once in 
the exchangeSpectra list, and again in the HDExchangeTimePoint object
*/
public class HDExchange implements Serializable {
    
    public ArrayList<MassSpectrum> exchangeSpectra;
    public ArrayList<HDExchangeTimePoint> exchangePoints;
    private Peptide peptide;
    private HDX_Form hdxSummary;
    static final long serialVersionUID = 26069;
    
    public HDExchange () {
        exchangeSpectra = new ArrayList();
    }
    
    public HDExchange ( boolean trim ) {
        exchangeSpectra = new ArrayList();
    }
    
    public void trimAllSpectra () {
        for ( HDExchangeTimePoint timePoint : exchangePoints ) {
            timePoint.trim();
        }
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
    
    public void removeTimePoint ( Integer idNumber ) {
        for ( int i = 0; i < exchangePoints.size(); ++i ) { 
            if ( exchangePoints.get(i).getIDNumber().equals( idNumber ) ) { 
                exchangePoints.remove( i );
                break;
            }
        }
        updateSummary();
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
            if ( Math.abs(sample.getTimePoint() - 0) < Math.pow( 10, -4) ) return true;
        }
        return false;
    }
    
    public double[][] getCentroidData () {
        double[][] summaryData = new double[ 2 ][ exchangePoints.size() ];
        for ( int i = 0; i < exchangePoints.size(); ++i ) {
            summaryData[ 0 ][ i ] = exchangePoints.get( i ).getTimePoint();
            summaryData[ 1 ][ i ] = exchangePoints.get( i ).getCentroid();
        }
        return summaryData;
    }
    
    public double[][] getPercentData () {
        double[][] summaryData = new double[ 2 ][ exchangePoints.size() ];
        for ( int i = 0; i < exchangePoints.size(); ++i ) {
            summaryData[ 0 ][ i ] = exchangePoints.get( i ).getTimePoint();
            if ( hasZeroTimePoint() ) {
                Double avgCentroidZero = getAvgCentroidAtTime( 0.0 );
                summaryData[ 1 ][ i ] = 
                        100*( exchangePoints.get( i ).getCentroid() - 
                        avgCentroidZero ) /
                        peptide.maxDeuteration();
            } else {
                summaryData[ 1 ][ i ] = exchangePoints.get( i ).getCentroid();
            }
        }
        return summaryData;
    }
    
    public Double getAvgCentroidAtTime( Double timePoint ) {
        double[][] centroidValues = getCentroidData();
        Double avgValue = 0.0;
        int count = 0;
        for ( int ii = 0; ii < centroidValues[0].length; ++ii ) {
            if ( Math.abs((Double)centroidValues[0][ii] - timePoint) < Math.pow(10, -4) ) {
                avgValue += (Double)centroidValues[1][ii];
                count++;
            }
        }
        avgValue /= count;
        return avgValue;
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
        DecimalFormat time_format = new DecimalFormat("###.#");
        DecimalFormat centroid_format = new DecimalFormat("###.##");
        return new DefaultTableModel( 
                FormatChange.ArrayToTable( getSummaryData(), time_format, centroid_format ), 
                new String[] {"time(min)", "centroid"} 
        );
    }
    
    public DefaultTableModel getPercentDataAsTable () {
        DecimalFormat time_format = new DecimalFormat("###.#");
        DecimalFormat centroid_format = new DecimalFormat("###.##");
        return new DefaultTableModel( 
                FormatChange.ArrayToTable( getPercentData(), time_format, centroid_format ), 
                new String[] {"time(min)", "centroid"} 
        );
    }
    
    public XYSeries getSummaryDataAsXYSeries () {
        return FormatChange.ArrayToXYSeries( getSummaryData() );
    }
    
    public XYSeries getPercentDataAsXYSeries () {
        return FormatChange.ArrayToXYSeries( getPercentData() );
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
        exchangePoints = new ArrayList();
        List<Integer> randomIDs = Utils.uniqueRandom( exchangeSpectra.size() );
        for ( int ii = 0; ii < exchangeSpectra.size(); ++ii ) {
//        for ( MassSpectrum scan : exchangeSpectra ) {
            MassSpectrum scan = exchangeSpectra.get( ii );
            // TODO if peakIndex is -1 this means the spectrum has no X values - 
            // find a way to handle this correctly
            // if it is greater than the length of the array, find a way to handle it
            int peakIndex = scan.peakIndex( peptide.mz );
            if ( peakIndex > scan.msValues[0].length) {
                Utils.showErrorMessage("M/z peak not found. Try again...");
                return;
            }
            
            int windowSize = MSReader.getInstance().getIntProperty("windowSize");
            float[][] dataRange = scan.getWindow( peptide.mz, windowSize );
            
            if ( Utils.getDeutTimePoint( scan.getRunTitle() ) == -1 ) {
                Utils.showErrorMessage( "Error: file "+scan.getRunTitle()+" is named "
                    + "improperly. Could not extract time point." );
                return;
            }

            HDExchangeTimePoint timePoint = new HDExchangeTimePoint( this, dataRange, 
                Utils.getDeutTimePoint( scan.getRunTitle() ), scan.getRetentionTime(),  
                randomIDs.get( ii )
            );

            exchangePoints.add( timePoint );

        }
        
        for ( HDExchangeTimePoint timePoint : exchangePoints ) {
            timePoint.showWindow();
        }
     
        hdxSummary = new HDX_Form (this);
        hdxSummary.setVisible( true );
    }
    
    public void updateSummary () {
        hdxSummary.updateAll();
    }
}
