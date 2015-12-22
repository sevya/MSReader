package mass.spec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class MZMLParser {
    static List<Float> ticIntensity = new ArrayList();
    static List<Float> ticTime = new ArrayList();
    static List<float[]> mzValues = new ArrayList();
    static List<float[]> intensityValues = new ArrayList();
    static List<StringBuffer> mzBinary = new ArrayList();
    static List<StringBuffer> intensityBinary = new ArrayList();
    static float time_decompressing;
    static float time_converting;
    
    static class MZMLHandler extends DefaultHandler {
        boolean binaryData = false;
        boolean dataTime = false;
        boolean ms1 = false;
        boolean mz = false;
        boolean intensity = false;
        boolean exit = false;
        float RT = 0;
        StringBuilder buffer = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, 
            Attributes atts) throws SAXException {
            if ( qName.equalsIgnoreCase("binaryDataArray") ) {
                binaryData = true;
            }
            dataTime = qName.equalsIgnoreCase("binary");

            if ( qName.equalsIgnoreCase("cvParam") ) {
                if ( atts.getValue("name").equals("ms level") ) {
                    ms1 = (atts.getValue("value").equals("1"));
                } else if ( atts.getValue("name").equals("total ion current") ) {
                    ticIntensity.add( Float.parseFloat( atts.getValue("value") ) );
                } else if ( atts.getValue("name").equals("scan start time") ) {
                    RT = Float.parseFloat( atts.getValue("value") );
                    ticTime.add( RT );
                }

                if ( atts.getValue("name").equals("m/z array") && binaryData && ms1 ) {
                    mz = true;
                    buffer.setLength(0);
                } else if ( atts.getValue("name").equals("intensity array") && binaryData  && ms1 ) {
                    intensity = true;
                    buffer.setLength(0);
                }

                if ( atts.getValue("name").equals( "total ion current chromatogram" ) ) exit=true;
            }
        }

        @Override
        public void endElement(String uri, String localName,
                String qName) throws SAXException {
            if ( qName.equalsIgnoreCase("binary") ) dataTime = false;
            if ( qName.equalsIgnoreCase("binaryDataArray") ) {
                binaryData = false;
                try {
                    String binaryString = buffer.toString();
                    float[] data = decodeBinary(binaryString, true);
                    if ( mz ) {
                        mzValues.add(data);
                        mz = false;
                    }
                    else if ( intensity ) {
                        intensityValues.add(data);
                        intensity = false;
                    }

                } catch (DataFormatException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
            if ( exit ) return;
            try {
                if ( binaryData  && dataTime) {
                    if ( mz || intensity )  {
                        buffer.append(ch, start, length);
                    }
                } 
            } catch ( Exception exc ) { exc.printStackTrace(); }
        }
        
        public static float[] decodeBinary( String binary, boolean zipped ) 
            throws DataFormatException, UnsupportedEncodingException {
            
            byte[] binArray = null;
            binArray = DatatypeConverter.parseBase64Binary(binary);
        
            if ( binArray == null ) {
                System.out.println("cannot decode data");
                throw new DataFormatException();
            }
            byte[] decompressedData;
            Timer timer = new Timer();
            if ( zipped ) decompressedData = decompress(binArray);
            else decompressedData = binArray;
            timer.reset();
            return convertData( decompressedData, 64 );
        }
    
        private static byte[] decompress(byte[] compressedData) {
            byte[] decompressedData;

            // using a ByteArrayOutputStream to not having to define the result array size beforehand
            Inflater decompressor = new Inflater();

            decompressor.setInput(compressedData);
            // Create an expandable byte array to hold the decompressed data
            ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                try {
                    int count = decompressor.inflate(buf);
                    if (count == 0 && decompressor.needsInput()) {
                        break;
                    }
                    bos.write(buf, 0, count);
                } catch (DataFormatException e) {
                    throw new IllegalStateException("Encountered wrong data format " +
                            "while trying to decompress binary data!", e);
                }
            }
            try {
                bos.close();
            } catch (IOException e) {
                // ToDo: add logging
                e.printStackTrace();
            }
            // Get the decompressed data
            decompressedData = bos.toByteArray();

            if (decompressedData == null) {
                throw new IllegalStateException("Decompression of binary data produced no result (null)!");
            }
            return decompressedData;
        }
        
        private static float[] convertData(byte[] data, int precision) {
            int step = (precision==64) ? 8 : 4;
            float[] resultArray = new float[data.length / step];

            ByteBuffer bb = ByteBuffer.wrap(data);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            for (int indexOut = 0; indexOut < data.length; indexOut += step) {
                float num = (float) bb.getDouble(indexOut );
                resultArray[(indexOut / step)] = num;
            }
            return resultArray;
        }   
    }
    
    public static MSChrom parse( String file ) {
         try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();            
                        
            DefaultHandler handler = new MZMLHandler();
            time_decompressing = 0;
            time_converting = 0;
            saxParser.parse(file, handler);        
            Timer timer = new Timer();
            MassSpectrum[] spectra = new MassSpectrum[mzValues.size()];
            for ( int i = 0; i < mzValues.size(); ++i ) {
                float[][] data = new float[2][];
                data[0] = mzValues.get(i);
                data[1] = intensityValues.get(i);
                spectra[i] = new MassSpectrum(data, "spectrum");
            }
            float[][] tic = new float[2][];
            tic[0] = new float[ticTime.size()];
            tic[1] = new float[ticIntensity.size()];

            MSChrom chrom = new MSChrom( tic, spectra, "example");
            timer.printTime();
            return chrom;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   
    public static void main ( String[] args ) throws Exception {
        Timer time = new Timer();
        MSChrom one = MZMLParser.parse("/Users/alexsevy/Documents/MSReader files/14N4 apo/data/20151216_14N4_H20_5_4C.mzML");
        time.printTime();
        System.out.println("time decompressing: "+time_decompressing/(float)Math.pow(10, 9));
        System.out.println("time converting: "+time_converting/(float)Math.pow(10, 9));

        time.reset();
        MSChrom two = new MSChrom(new File("/Users/alexsevy/Documents/MSReader files/14N4 apo/data/20151216_14N4_H20_5_4C.mzML"), "MZML");
        time.printTime();
   }

}
    

