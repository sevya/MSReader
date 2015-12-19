package mass.spec;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArrayList;
import uk.ac.ebi.jmzml.model.mzml.Chromatogram;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

public class MSChrom {
    String title;
    double[] mz_values;
    MassSpectrum[] spectra;
    double[][] TIC;
    String path;
    Array scan_acquisition_time;
    Array total_intensity;
    Array mass_values;
    Array intensity_values;
    
    // SPECTRA_UNIFORM refers to whether or not all the mass spectra have the
    // same m/z value on the x-axis
    // If so, this array is only kept once, in the MSChrom object, to reduce
    // the amount of data held in memory when working with files
    // In this case each MassSpectrum object holds the intensity values in the 
    // yvals array
    // If not, each MassSpectrum object holds both m/z and intensity in the 
    // msValues array
    boolean SPECTRA_UNIFORM = true;
    
    public MSChrom (File f) {
        path = f.getAbsolutePath();
        String s = f.getParent();
        String[] split = s.split(Pattern.quote(File.separator));
        this.title = split[split.length-1];
        convertCDF(f.toString());
    }
    
    public MSChrom (File f, String type) 
            throws MzMLUnmarshallerException {
        path = f.getAbsolutePath();
        String s = f.getName();
        String[] split = s.split("\\.");
        this.title = split[0];
        if (type.equals("MZML")) {
//            path = f.getAbsolutePath();
            //String s = f.getParent();
            //String[] split = s.toString().split(Pattern.quote(File.separator));
//            this.title = f.getName();
            SPECTRA_UNIFORM = false;
            convertMZML(f.toString(), true);
        } else if (type.equals("CDF")) {
//            path = f.getAbsolutePath();
//            String s = f.getParent();
//            String[] split = s.split(Pattern.quote(File.separator));
//            this.title = split[split.length-1];
            convertCDF(f.toString());
        } else {
            //do nothing
        }
    }
    
        public MSChrom (File f, String type, boolean multiproc) 
                throws MzMLUnmarshallerException {
        path = f.getAbsolutePath();
        String s = f.getName();
        String[] split = s.split("\\.");
        this.title = split[0];
        if (type.equals("MZML")) {
            SPECTRA_UNIFORM = false;
            convertMZML(f.toString(), multiproc);
        } else if (type.equals("CDF")) {
            convertCDF(f.toString());
        } else {
            //do nothing
        }
    }
    
    private MSChrom() {}
    
    private void convertCDF (String filename) {
        readCDF(filename);
        TIC = getTICFromCDF();
        spectra = getSpectraFromCDF();
    }
    
    private void convertMZML (String filename, boolean multiproc) 
            throws MzMLUnmarshallerException {
        System.out.println("starting");
        File mzml = new File (filename);
        MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(mzml);
        Iterator<String> iter = unmarshaller.getChromatogramIDs().iterator();
        Chromatogram chrom = unmarshaller.getChromatogramById(iter.next());
        Number[] time = chrom.getBinaryDataArrayList().getBinaryDataArray().get(0).getBinaryDataAsNumberArray();
        Number[] intensity = chrom.getBinaryDataArrayList().getBinaryDataArray().get(1).getBinaryDataAsNumberArray();

        TIC = new double[2][time.length];
        for (int i = 0; i < time.length; i++) {
            TIC[ 0 ][ i ] = time[ i ].doubleValue();
            TIC[ 1 ][ i ] = intensity[ i ].doubleValue();
        }
        System.out.println(TIC[0].length);
        
        int objectCount = unmarshaller.getObjectCountForXpath("/run/spectrumList/spectrum");
        spectra = new MassSpectrum[objectCount];
        
        if ( !multiproc ) {
            MZMLconverter converter = new MZMLconverter( unmarshaller, 0, objectCount,
            spectra, TIC[0], this.title);
            converter.run();
        } else {
            int no_threads = Runtime.getRuntime().availableProcessors();
            Thread[] threads = new Thread[no_threads+1];

            for (int i = 0; i < threads.length; i++) {
                int start = i*(objectCount/no_threads);
                int end = i*(objectCount/no_threads) + (objectCount/no_threads);
                threads[i] = new Thread(new MZMLconverter(unmarshaller, start, end, spectra, TIC[0], this.title));
                threads[i].start();
            }
            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private void readCDF (String filename) {
        try {
            NetcdfFile net = NetcdfFile.open (filename);
            scan_acquisition_time = net.readSection("scan_acquisition_time");
            total_intensity = net.readSection("total_intensity");
            mass_values = net.readSection("mass_values");
            intensity_values = net.readSection("intensity_values");
            SPECTRA_UNIFORM = areSpectraUniform(mass_values);
            net.close();
        } catch (InvalidRangeException a) {
            System.out.println("Invalid parameters - check the netCDF dimensions");
        }
        catch (IOException i) {
            System.out.println("Error opening file");
        }
    }
    
    private double[][] getTICFromCDF () {
        double tic[][] = new double [2][];
        tic[0] = (double[])scan_acquisition_time.get1DJavaArray(double.class);
        tic[1] = (double[])total_intensity.get1DJavaArray(double.class);
        for (int i = 0; i < tic[0].length; i++) {
            tic[0][i] /= 60;
        }
        return tic;
    }
    
    private MassSpectrum[] getSpectraFromCDF () {
        DecimalFormat myformat = new DecimalFormat("###.##");
        ArrayList<MassSpectrum> vals = new ArrayList();
        MassSpectrum current;
        int fileNo = 0;
        if (SPECTRA_UNIFORM) {
            int msLength = getMSLength(mass_values, 0);
            mz_values = new double [msLength+1];
            arraycopy(mass_values, 0, mz_values, msLength+1);
            int startPt = 0;
            double[] yvals = new double [msLength+1]; 
            while (startPt + msLength < intensity_values.getSize()) {
                arraycopy(intensity_values, startPt, yvals, msLength+1);
                current = new MassSpectrum (yvals, "Elution: "+myformat.format(TIC[0][fileNo++]) + "min");
                current.setRunTitle(this.title);
                vals.add(current);
                startPt += (msLength + 1);
            }
        } else {
            int msLength = getMSLength(mass_values, 0);
            double[] xvals;
            double[] yvals;
            int startPt = 0;
            while (startPt + msLength < intensity_values.getSize()) {
                xvals = new double[msLength+1];
                yvals = new double[msLength+1];
                arraycopy(mass_values, startPt, xvals, msLength+1);
                arraycopy(intensity_values, startPt, yvals, msLength+1);
                current = new MassSpectrum(xvals, yvals, this.title, "Elution: " + myformat.format(TIC[0][fileNo++])  + "min");
                vals.add(current);
                startPt += (msLength + 1);
                msLength = getMSLength(mass_values, startPt);
            }
        }

         return FormatChange.MSArraylistToArray(vals);
    }
    
    static class MZMLconverter implements Runnable {
        MzMLUnmarshaller unmarshaller;
        int start, end;
        MassSpectrum[] spectra;
        double[] time;
        String msChromParentTitle;
        
        public MZMLconverter (MzMLUnmarshaller unmarsh, int s, int e, MassSpectrum[] spec, double[] t, String parentTitle) {
            unmarshaller = unmarsh;
            start = s;
            end = e;
            spectra = spec;
            msChromParentTitle = parentTitle;
            time = t;
        }
        
        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                if (i >= unmarshaller.getObjectCountForXpath("/run/spectrumList/spectrum")) {
                    break;
                }
                try {
                    Spectrum spectrum = unmarshaller.getSpectrumById(unmarshaller.getSpectrumIDFromSpectrumIndex(i));
                    BinaryDataArrayList bdal = spectrum.getBinaryDataArrayList();
                    List<BinaryDataArray> bda = bdal.getBinaryDataArray();

                    Number[] mz = bda.get(0).getBinaryDataAsNumberArray();
                    Number[] intensity = bda.get(1).getBinaryDataAsNumberArray();

                    double[] mz_values = new double[mz.length];
                    double[] intensity_values = new double[intensity.length];
                    
                    for (int j = 0; j < mz_values.length; j++) {
                        mz_values[j] = mz[j].doubleValue();
                        intensity_values[j] = intensity[j].doubleValue();
                    }

                    spectra[i] = new MassSpectrum(mz_values, intensity_values, 
                            msChromParentTitle,
                            "scan no:"+(spectrum.getIndex()+1)+" time:"+
                                    String.format("%.2f",time[i])+"min");
                    spectra[i].setRetentionTime(time[i]);
                    
                } catch (MzMLUnmarshallerException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
    }
 
    public static void main ( String[] args ) throws MzMLUnmarshallerException {
//        MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(new File ("/Users/alexsevy/Documents/MSReader files/"
//                + "14N4 apo/data/20151216_14N4_H20_5_4C.mzML" ));
        MSChrom chromatogram = new MSChrom( new File ("/Users/alexsevy/Documents/MSReader files/"
                + "14N4 apo/data/20151216_14N4_H20_5_4C.mzML" ), "MZML", false );
    }
    
    private boolean areSpectraUniform (Array array) {
        int len = getMSLength (array, 0);
        double[] vals = new double[100];
        for (int i = 0; i < 100; i++) {
            vals[i] = array.getDouble(i);
        }
        for (int i = len+1; i < array.getSize(); i += (len+1)) {
            for (int j = 0; j < 100; j++) {
                if (vals[j] == array.getDouble(i+j)) continue;
                else return false;
                
            }
        }
        return true;
    }
    
    private void arraycopy (Array array, int start1, double[] dest, int length) {
        int counter = 0;
        for (int i = start1; i < start1 + length; i++) {
            dest[counter++] = array.getDouble(i);
        } 
    }
    
    private int getMSLength (Array array, int startPt) {
        for (int i = startPt; i < array.getSize(); i++) {
            if (i+1 == (int)array.getSize()) return (int)array.getSize() - startPt;
            else if (array.getDouble(i+1) > array.getDouble(i)) continue;
            else return i - startPt;
         }
        return -1;
    }
    
    private void graphTIC () {
        new Plotter(FormatChange.ArrayToXYSeries(TIC, "TIC")).display();
        
    }
    
    private void graphSpectrum (int index) {
        double[][] data = new double[2][];
        MassSpectrum ms = spectra[index];
        data[0] = mz_values;
        data[1] = ms.yvals;
        new Plotter(data).display();
    }
    
    public double[][] getEIC (double startion, double endion) {
        double[][] EIC = new double[2][TIC[0].length];
        EIC[0] = Arrays.copyOf(TIC[0], EIC[0].length);
        MassSpectrum currMS;
        if (SPECTRA_UNIFORM) {
            for (int i = 0; i < spectra.length; i++) {
                currMS = spectra[i];
                for (int j = 0; j < mz_values.length; j++) {
                    if (mz_values[j] > startion && mz_values[j] < endion) {
                        EIC[1][i] += currMS.yvals[j];
                    }
                }
            }
        } else {
            for (int i = 0; i < spectra.length; i++) {
            currMS = spectra[i];
                for (int j = 0; j < currMS.msValues[0].length; j++) {
                    if (currMS.msValues[0][j] > startion && currMS.msValues[0][j] < endion) {
                        EIC[1][i] += currMS.msValues[1][j];
                    }
                }
            }
        }
        
        return EIC;
    }
    
    public double[][] getBPC () {
        double[][] bpc = new double[2][TIC[1].length];
        bpc[0] = TIC[0];
        for (int i = 0; i < spectra.length; i++) {
            bpc[1][i] = spectra[i].getYMax();
        }
        return bpc;
    }
    
    public int getElutionIndexFromEIC (double[][] eic, double time) {
        int startindex = Utils.binarySearch (eic[0], time - .5);
        int endindex = Utils.binarySearch (eic[0], time + .5);
        double max = -1;
        int maxindex = -1;
        for (int i = startindex; i < endindex; i++) {
            if (eic[1][i] > max) {
                max = eic[1][i];
                maxindex = i;
            }
        } 
        return maxindex;
    }
    
    public int[] getElutionIndicesFromEIC (double[][] eic, double time) {
        int max = getElutionIndexFromEIC(eic, time);
        int[] indices = new int[2];
        for (int i = max; i > 1; i--) {
            if (eic[1][i] - eic[1][i-1] < 0) {
                indices[0] = i;
                break;
            }
        } for (int i = max; i < eic[0].length - 1; i++) {
            if (eic[1][i] - eic[1][i+1] < 0) {
                indices[1] = i;
                break;
            }
        } return indices;
    }
    
    public void backgroundSubtraction(MSChrom control) throws DifferentResolutionsException{
        for (int i = 0; i < spectra.length; i++) {
            backgroundSubtraction (control, i);
        }
    }
    
    public void backgroundSubtraction (MSChrom control, int timeIndex) 
            throws DifferentResolutionsException {
        
        MassSpectrum samp = spectra[timeIndex];
        MassSpectrum cont = control.spectra[timeIndex];
        double[] scaler = (SPECTRA_UNIFORM) ? new double[mz_values.length] 
                : new double[samp.msValues[0].length];
        Arrays.fill(scaler, 0.0);
        int n = 0;
        
        if (SPECTRA_UNIFORM && control.SPECTRA_UNIFORM) {
            for (int i = 0; i < scaler.length; i++) {
                for (int j = timeIndex - 5; j <= timeIndex + 5; j++) {
                    if (j < 0) j = 0;
                    if (j >= spectra.length) break;
                    samp = spectra[j];
                    if (samp.yvals[i] == 0) scaler[i] += 0;
                    else if (cont.yvals[i] > samp.yvals[i]) scaler[i] += 1;
                    else scaler[i] += cont.yvals[i] / samp.yvals[i];
                    n++;
                } 
                scaler[i] /= (double)n;
                n = 0;
            }
            for (int k = 0; k < scaler.length; k++) {
                spectra[timeIndex].yvals[k] *= (1-scaler[k]);
            }
        } else if (!SPECTRA_UNIFORM && !control.SPECTRA_UNIFORM){
            for (int i = 0; i < scaler.length; i++) {
                for (int j = timeIndex - 5; j <= timeIndex + 5; j++) {
                    if (j < 0) j = 0;
                    if (j >= spectra.length) break;
                    samp = spectra[j];
                    if (samp.msValues[1][i] == 0) scaler[i] += 0;
                    else if (cont.msValues[1][i] > samp.msValues[1][i]) scaler[i] += 1;
                    else scaler[i] += cont.msValues[1][i] / samp.msValues[1][i];
                    n++;
                } 
            scaler[i] /= (double)n;
            n = 0;
            }
            for (int k = 0; k < scaler.length; k++) {
                spectra[timeIndex].msValues[1][k] *= (1-scaler[k]);
            }
        } else {
            throw new DifferentResolutionsException();
        }
    }
    
    public MSChrom copy () {
        if (this == null) return null;
        MSChrom temp = new MSChrom();
        temp.title = title;
        temp.spectra = spectra.clone();
        temp.TIC = TIC.clone();
        temp.SPECTRA_UNIFORM = SPECTRA_UNIFORM;
        if (SPECTRA_UNIFORM) temp.mz_values = mz_values.clone();
        return temp;
    }
    
    public MassSpectrum combineSpectra (int start, int end) throws DifferentResolutionsException {
        if (SPECTRA_UNIFORM) {
            if (start == end) return spectra[start];
            int length = mz_values.length;
            double[] vals = new double[length];
            Arrays.fill(vals, 0.0);
            for (int i = start; i < end; i++) {
                for (int j = 0; j < length; j++) {
                    vals[j] += spectra[i].yvals[j];
                }
            }
            MassSpectrum ms = new MassSpectrum (vals, spectra[start].msTitle + " to " + spectra[end].msTitle);
            ms.runTitle = title;
            return ms;
        } else {
            throw new DifferentResolutionsException();
        }
    }
}
