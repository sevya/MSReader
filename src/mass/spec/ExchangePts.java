package mass.spec;

import java.security.InvalidParameterException;
import java.util.*;
import org.jfree.data.xy.XYSeries;

public class ExchangePts {
    public ArrayList<Double> centroid;
    public ArrayList<Double> deltaCentroid;
    public ArrayList<Double> timePointList;
    public ArrayList<String> key;
    public ArrayList<Double> dperr;
    Peptide peptide;
    
    public ExchangePts () {
        centroid = new ArrayList();
        deltaCentroid = new ArrayList();
        timePointList = new ArrayList();
        key = new ArrayList();
        dperr = new ArrayList();
    }
    
    public void changePt (String k, double cent) {
        int index = key.indexOf(k);
        if (index < 0) throw new NoSuchElementException();
        centroid.set(index, cent);
    }
    
    public void addTimePoint (double time, double cent, String str) {
        centroid.add(cent);
        timePointList.add(time);
        key.add(str);
    }
    
    public void setPeptide (Peptide p) {
        peptide = p;
    }
    
    public void removeTimePoint (String str) {
        int index = key.indexOf(str);
        key.remove(index);
        timePointList.remove(index);
        centroid.remove(index);
    }
    
    public boolean hasZeroPt () {
        return (timePointList.indexOf(0.0) != -1);
    }
    
    private void getDeltaCentroid () {
        if (!hasZeroPt()) throw new NoSuchElementException();
        deltaCentroid.clear();
        double zerocent = centroid.get(timePointList.indexOf(0.0));
        for (int i = 0; i < centroid.size(); i++) {
            deltaCentroid.add(centroid.get(i) - zerocent);
        }
    }
    
    private void getDperR () {
        getDeltaCentroid();
        dperr.clear();
        for (int i = 0; i < deltaCentroid.size(); i++) {
            dperr.add((deltaCentroid.get(i) * peptide.charge) / peptide.sequence.length());
        }
    }
    
    public XYSeries getXYSeries () {
        if (hasZeroPt()) {
            getDperR();
            XYSeries series = new XYSeries (" ");
            for (int i = 0; i < timePointList.size(); i++) {
                series.add(timePointList.get(i), dperr.get(i));
            }
            return series;
        } else {
            XYSeries series = new XYSeries (" ");
            for (int i = 0; i < timePointList.size(); i++) {
                series.add(timePointList.get(i), centroid.get(i));
            }
            return series;
        }
    }
    
    public double[][] getArray () {
        if (hasZeroPt()) {
            getDperR();
            double[][] series = new double[2][timePointList.size()];
            for (int i = 0; i < timePointList.size(); i++) {
                series[0][i] = timePointList.get(i);
                series[1][i] = dperr.get(i);
            }
            return series;
        } else {
            double[][] series = new double[2][timePointList.size()];
            for (int i = 0; i < timePointList.size(); i++) {
                series[0][i] = timePointList.get(i);
                series[1][i] = centroid.get(i);
            }
            return series;
        }
    }
    
    public Object[][] getTable () {
        if (hasZeroPt()) {
            getDperR();
            Double[][] table = FormatChange.ArrayListToTable(timePointList, dperr);
            sort(table, 0);
            return table;
        }
        else {
            Double[][] table = FormatChange.ArrayListToTable(timePointList, centroid);
            sort(table, 0);
            return table;
        }
    }
    
    private static void sort (Double[][] table, final int index) {
        Arrays.sort(table, new Comparator<Double[]>() {
            @Override
            public int compare(Double[] one, Double[] two) {
                if (index >= one.length || index >= two.length) {
                    throw new InvalidParameterException();
                }
                return one[index].compareTo(two[index]);
            }
        });
    }
}

