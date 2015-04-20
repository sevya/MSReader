package mass.spec;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import org.apache.log4j.*;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArrayList;
import uk.ac.ebi.jmzml.model.mzml.Chromatogram;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.xml.io.MzMLObjectIterator;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;


public class TestClass {

    public static void main (String[] args) throws MzMLUnmarshallerException {
        File mzml = new File ("C:/Users/Alex/Documents/out.mzml/ah_100713_1xBSA_131008061632.mzml");
        System.out.println(mzml.getName());
//        MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(mzml);
//        
//        Set<String> ids = unmarshaller.getChromatogramIDs();
//        Iterator<String> iter = ids.iterator();
//        while (iter.hasNext()) {
//            Chromatogram chrom = unmarshaller.getChromatogramById(iter.next());
//            Number[] time = chrom.getBinaryDataArrayList().getBinaryDataArray().get(0).getBinaryDataAsNumberArray();
//            Number[] intensity = chrom.getBinaryDataArrayList().getBinaryDataArray().get(1).getBinaryDataAsNumberArray();
//            for (int i = 0; i < time.length; i++) {
//                System.out.println(time[i] + "\t"+intensity[i]);
//            }
//            System.out.println(time.length == intensity.length);
//            System.out.println(time.length == unmarshaller.getObjectCountForXpath("/run/spectrumList/spectrum"));
//        }
//        int no_threads = 5;
//        Thread[] threads = new Thread[no_threads+1];
//        int object_count = unmarshaller.getObjectCountForXpath("/run/spectrumList/spectrum");
//        System.out.println(object_count);
//        MassSpectrum[] spectra = new MassSpectrum[object_count];
//        for (int i = 0; i < threads.length; i++) {
//            int start = i*(object_count/no_threads);
//            int end = i*(object_count/no_threads) + (object_count/no_threads);
//            threads[i] = new Thread(new MZMLconverter(unmarshaller, start, end, spectra));
//            threads[i].start();
//        }
//        for (int i = 0; i < threads.length; i++) {
//            try {
//                threads[i].join();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        }
//        //Thread[]
    }
    
    static class MZMLconverter implements Runnable {
        MzMLUnmarshaller unmarshaller;
        int start, end;
        MassSpectrum[] spectra;
        double[] time;
        public MZMLconverter (MzMLUnmarshaller unmarsh, int s, int e, MassSpectrum[] spec, double[] t) {
            unmarshaller = unmarsh;
            start = s;
            end = e;
            spectra = spec;
            time = t;
        }
        
        @Override
        public void run() {
            Spectrum spectrum;
            for (int i = start; i < end; i++) {
                if (i >= unmarshaller.getObjectCountForXpath("/run/spectrumList/spectrum")) {
                    break;
                }
                try {
                    spectrum = unmarshaller.getSpectrumById(unmarshaller.getSpectrumIDFromSpectrumIndex(i));
                    BinaryDataArrayList bdal = spectrum.getBinaryDataArrayList();
                    List<BinaryDataArray> bda = bdal.getBinaryDataArray();
                    Number[] mz = bda.get(0).getBinaryDataAsNumberArray();
                    Number[] intensity = bda.get(1).getBinaryDataAsNumberArray();
                    double[] mz_values = new double[mz.length];
                    double[] intensity_values = new double[intensity.length];
                    for (int j = 0; j < mz_values.length; j++) {
                        mz_values[j] = mz[j].doubleValue();
                        intensity_values[j] = intensity[j].doubleValue();
                        //if (i==1) System.out.println(mz_values[j] + "\t"+intensity_values[j]);
                    }
                    spectra[i] = new MassSpectrum(mz_values, intensity_values,"", "scan no:"+(spectrum.getIndex()+1)+" time:"+time[i]+"min");
                    
                } catch (MzMLUnmarshallerException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
        
    }
}
