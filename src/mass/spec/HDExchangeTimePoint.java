package mass.spec;

// Class for holding data related to an HD exchange time point

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private float[][] dataRange_;
    private final List<float[][]> backupData_;
    private double centroid_;
    private final double timePoint_;
    private double retentionTime_;
    private final HDExchange parent;
    private final int idNumber_;
    
    public HDExchangeTimePoint ( //Exchange_Popup window, 
            HDExchange par, float[][] range, double timePoint, double retentionTime, int id_number ) {
        parent = par;
        dataRange_ = range;
        timePoint_ = timePoint;
        retentionTime_ = retentionTime;
        // Have to calculate centroid before creating ExchangePopup window
        // That way when it reaches up to get the values they're precalculated
        calculateValues();
        idNumber_ = id_number;
        window_ = new Exchange_Popup( this ); 
        backupData_ = new ArrayList();
    }
    
    public Peptide getPeptide() { return parent.getPeptide(); }
    
    public void showWindow () {
        window_.setVisible(true);
    }
    public void setData ( float[][] range ) { dataRange_= range; }
    
    public float[][] getData () { return dataRange_; }
    
    public double getCentroid () { return centroid_; }
    
    public String getCentroidString () { return String.format("%.3f", centroid_); }
    
    public double getTimePoint () { return timePoint_; } 
    
    public Integer getIDNumber () { return idNumber_; }
    
    private void backUpData () {        
        float[][] tempData = new float [ 2 ][];
        tempData[ 0 ] = Arrays.copyOfRange( dataRange_[ 0 ], 0, dataRange_[ 0 ].length );
        tempData[ 1 ] = Arrays.copyOfRange( dataRange_[ 1 ], 0, dataRange_[ 1 ].length );
        backupData_.add( tempData );
    }
    
    public void deleteRows ( int startIndex, int length ) {
        backUpData();
        for ( int i = 0; i < length; i++ ) {
            dataRange_[ 0 ] = ArrayUtils.remove( dataRange_[ 0 ], startIndex );
            dataRange_[ 1 ] = ArrayUtils.remove( dataRange_[ 1 ], startIndex );
        }  
        calculateValues();
        parent.updateSummary();
    }
    
    public void undoDelete () { 
//        dataRange_ = tempData_;
        dataRange_ = backupData_.remove( backupData_.size()-1 );
        calculateValues();
        parent.updateSummary();

    }
    
    public DefaultTableModel getDataAsTable () {
        DefaultTableModel table = new DefaultTableModel ( 
            FormatChange.ArrayToTable( dataRange_, 
            new DecimalFormat[] { new DecimalFormat("###.##"), new DecimalFormat("0.0E0") } ), 
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
    
    public void setRetentionTime (double rt) { retentionTime_ = rt; }
    
    public double getRetentionTime () { return retentionTime_; }
    
    public void trim () {
        backUpData();
        try {
            int[] indices = peakDetector();
            dataRange_[ 0 ] = Arrays.copyOfRange(dataRange_[ 0 ], indices[ 0 ], indices[ 1 ]);
            dataRange_[ 1 ] = Arrays.copyOfRange(dataRange_[ 1 ], indices[ 0 ], indices[ 1 ]);
            calculateValues();
            parent.updateSummary();

        } catch (NoPeakDetectedException e) {
            window_.setError(e.getMessage());
        }
    }
    
    // Removes this time point from HD exchange instance
    public void removeTimePoint() {
        parent.removeTimePoint( idNumber_ );
    }
    
    private int[] peakDetector () throws NoPeakDetectedException {
         // TODO figure out how this is detecting peaks and document it a little
        double[][] tope = parent.getPeptide()
                .getDistribution((int)Math.pow(10, 5), true, true);
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
        int start = Utils.binarySearch ( dataRange_[0], (float)tope[0][0] );
        
        int beforeindex = 0;
        max = MSMath.getMax( dataRange_[ 1 ] );
        double lowerthresh = (max > 10000) ? 500 : max*.05;
        for (int i = start; i > 0; i--) {
            if (dataRange_[1][i] < lowerthresh) {
                beforeindex = i-1;
                break;
            } 
        }
        
        int after = Utils.binarySearch (dataRange_[0], (float)tope[0][endindex]);
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
     
     private double getShift (float[][] data, double[][] isotope) {
        int counter = 1;
        double score = MSMath.getScore(data, isotope);
        double maxscore = score;
        double maxshift = 0;
        double shift = 1/(double)parent.getPeptide().charge;
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
