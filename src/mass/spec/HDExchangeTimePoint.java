package mass.spec;

// Class for holding data related to an HD exchange time point

import java.util.Arrays;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang.ArrayUtils;
import org.jfree.data.xy.XYSeries;

/* 
Holds the data for a single time point of measurement of HD exchange. 
Contains the time point in minutes, centroid mass, difference in 
centroid mass from zero time point, and deuteration per residue. 
*/

public class HDExchangeTimePoint {
    
    Exchange_Popup window_;
    private double[][] dataRange_;
    private double[][] tempData_;
    private double centroid_;
    private final double timePoint_;
    
    public HDExchangeTimePoint ( //Exchange_Popup window, 
            double[][] range, double timePoint ) {
        dataRange_ = range;
        timePoint_ = timePoint;
        // Have to calculate centroid before creating ExchangePopup window
        // That way when it reaches up to get the values they're precalculate
        calculateValues();
        window_ = new Exchange_Popup( this ); 
        
    }
    
    public void showWindow () {
        window_.setVisible(true);
    }
    public void setData ( double[][] range ) { dataRange_= range; }
    
    public double[][] getData () { return dataRange_; }
    
    public double getCentroid () { return centroid_; }
    
    public double getTimePoint () { return timePoint_; } 
    
    private void backUpData () { 
        tempData_ = new double [ 2 ][];
        tempData_[ 0 ] = Arrays.copyOfRange( dataRange_[ 0 ], 0, dataRange_[ 0 ].length );
        tempData_[ 1 ] = Arrays.copyOfRange( dataRange_[ 1 ], 0, dataRange_[ 1 ].length );
    }
    
    public void deleteRows ( int startIndex, int length ) {
        backUpData();
        for ( int i = 0; i < length; i++ ) {
            dataRange_[ 0 ] = ArrayUtils.remove( dataRange_[ 0 ], startIndex );
            dataRange_[ 1 ] = ArrayUtils.remove( dataRange_[ 1 ], startIndex );
        }  
        calculateValues();
        MSReader.getHDExchangeInstance().updateSummary();
    }
    
    public void undoDelete () { 
        dataRange_ = tempData_;
        calculateValues();
        MSReader.getHDExchangeInstance().updateSummary();

    }
    
    public DefaultTableModel getDataAsTable () {
        DefaultTableModel table = new DefaultTableModel ( 
                FormatChange.ArrayToTable( dataRange_ ), 
                new String[] {"m/z", "intensity"} 
        );
        return table;
    }
    
    public XYSeries getDataAsXYSeries () {
        return FormatChange.ArrayToXYSeries( dataRange_ );
    }
    
    private void calculateValues () {
        centroid_ = MSMath.calcCentroid( dataRange_ );
    }
    
     public void trim () {
        backUpData();
        try {
            int[] indices = peakDetector();
            dataRange_[ 0 ] = Arrays.copyOfRange(dataRange_[ 0 ], indices[ 0 ], indices[ 1 ]);
            dataRange_[ 1 ] = Arrays.copyOfRange(dataRange_[ 1 ], indices[ 0 ], indices[ 1 ]);
            calculateValues();
            MSReader.getHDExchangeInstance().updateSummary();

        } catch (NoPeakDetectedException e) {
            window_.setError(e.getMessage());
        }
    }
    
     private int[] peakDetector () throws NoPeakDetectedException {
         // TODO figure out how this is detecting peaks and document it a little
        double[][] tope = MSReader.getHDExchangeInstance().getPeptide()
                .getThreadedDistribution( (int)Math.pow(10, 5) );
        Utils.sort2DArray (tope, 0);
        int endindex = tope[0].length-1;
        for (int i = 0; i < tope[0].length; i++) {
            if (tope[1][i] > .005) endindex = i;
        }
        double max = MSMath.getMax( dataRange_[1] );
        max /= MSMath.getMax(tope[1]);
        for (int i = 0; i < tope[1].length; i++) tope[1][i] = tope[1][i] * max;
        
        if (timePoint_ != 0) {
            double shift = getShift( dataRange_, new double[][] {tope[0].clone(), tope[1].clone()});
            for (int i = 0; i < tope[0].length; i++) tope[0][i] += shift;
        }
        double score = MSMath.getScore( dataRange_, tope );
        window_.setTitle(score+"");
        
        if (score < .5) throw new NoPeakDetectedException("No peak detected - does not match input peptide");
        int start = Utils.binarySearch ( dataRange_[0], tope[0][0] );
        
        int beforeindex = 0;
        max = MSMath.getMax( dataRange_[ 1 ] );
        double lowerthresh = (max > 10000) ? 500 : max*.05;
        for (int i = start; i > 0; i--) {
            if (dataRange_[1][i] < lowerthresh) {
                beforeindex = i-1;
                break;
            } 
        }
        
        int after = Utils.binarySearch (dataRange_[0], tope[0][endindex]);
        int afterindex = dataRange_[0].length;
        for (int i = after; i < dataRange_[0].length; i++) {
            if (dataRange_[1][i] < lowerthresh) {
                afterindex = i+1;
                break;
            }
        }
        if (MSMath.getMax(dataRange_[1], beforeindex, afterindex) < 5000) throw new NoPeakDetectedException("No peak detected - signal too weak");
        
        return new int[] {beforeindex, afterindex};
    } 
     
     private double getShift (double[][] data, double[][] isotope) {
        int counter = 1;
        double score = MSMath.getScore(data, isotope);
        double maxscore = score;
        double maxshift = 0;
        double shift = 1/(double)MSReader.getHDExchangeInstance().getPeptide().charge;
        while (isotope[0][isotope[0].length-1] < data[0][data[0].length-1]) {
            for (int i = 0; i < isotope[0].length; i++) isotope[0][i] += shift;
            score = MSMath.getScore(data, isotope);
            if (score > maxscore) {
                maxscore = score;
                maxshift = (double)counter*shift;
            }
            counter++;
        }
        return maxshift;
     }
   
}
