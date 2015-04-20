package mass.spec;

import java.io.*;
import java.nio.file.*;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileReader;

public class ProtectionMap {
    
    public static void createProtectionMap (HDRun[][] forms, File pdbfile, File pymol, File outputpath, HeatMapGradient gradient, String sequence) {
        double[] deuteration;
        int color;
        
        if (forms.length == 1) {
            List<String> peptidelist = new ArrayList();
            List<Double> deuterium = new ArrayList();
            for (int i = 0; i < forms[0].length; i++) {
                peptidelist.add(Utils.trimPeptide(forms[0][i].peptide.sequence));
                deuterium.add(forms[0][i].A);
            }
            
            try {
                deuteration = calculateResidueDeuteration (peptidelist, deuterium, sequence);
            } catch (PeptideMismatchException exc) {
                JOptionPane.showMessageDialog(null,exc.getMessage(),"Error",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
        
            color = MSReader.COLOR_BLUE;

        } else {
            List<String> peptidelistfree = new ArrayList();
            List<String> peptidelistbound = new ArrayList();
            List<Double> deuteriumfree = new ArrayList();
            List<Double> deuteriumbound = new ArrayList();
            for (int i = 0; i < forms[0].length; i++) {
                peptidelistfree.add(Utils.trimPeptide(forms[0][i].peptide.sequence));
                deuteriumfree.add(forms[0][i].A);
            } for (int i = 0; i < forms[1].length; i++) {
                peptidelistbound.add(Utils.trimPeptide(forms[1][i].peptide.sequence));
                deuteriumbound.add(forms[1][i].A);
            }
            
             try {
                double[] dfree = calculateResidueDeuteration (peptidelistfree, deuteriumfree, sequence);
                double[] dbound = calculateResidueDeuteration (peptidelistbound, deuteriumbound, sequence);
                deuteration = new double [sequence.length()];
                for (int i = 0; i < deuteration.length; i++) {
                    deuteration[i] = dfree[i] - dbound[i];
                }
            } catch (PeptideMismatchException exc) {
                JOptionPane.showMessageDialog(null,exc.getMessage(),"Error",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }

            color = MSReader.COLOR_RED; 
            
        }
        
        int startIndex = -1;
        int[] tailingResidues;
        try {
            startIndex = getStartIndex (pdbfile, sequence);
            tailingResidues = getExtraIndices (pdbfile, startIndex, sequence);
        } catch (PeptideMismatchException exc) {
            JOptionPane.showMessageDialog(null,"Error: PDB sequence does not match input sequence. "
                    + "Your entire sequence must be contained in the PDB sequence - try shortening"
                    + " the input sequence","Error",
                JOptionPane.ERROR_MESSAGE); 
            return;
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,"Unable to read PDB file","Error",
                JOptionPane.ERROR_MESSAGE); 
            return;
        }
            
        createHeatMapExternGrad (gradient, deuteration, pdbfile, pymol, outputpath, color, startIndex, tailingResidues);
    }
    
    public static void createProtectionMap (File[] excel, File pdbfile, File outputpath, File pymol, HeatMapGradient gradient, String sequence) {
        double[] deuteration;
        int color;
        if (excel.length == 1) {
            int doctype;
            String excelpath = excel[0].toString();
            if (excelpath.toLowerCase().contains(".xlsx")) doctype = 0;
            else if (excelpath.toLowerCase().contains(".xls")) doctype = 1;
            else if (excelpath.toLowerCase().contains(".csv")) doctype = 2;
            else if (excelpath.toLowerCase().contains(".txt")) doctype = 2;
            else {
                Utils.showErrorMessage ("Not a valid excel file");
                return;
            }
            
            List<String> peptidelist;
            List<Double> deuterium;
            ArrayList[] results;
            if (doctype == 0) {
                results = getXLSXLevels (excelpath);
            } else if (doctype == 1) {
                results = getXLSLevels (excelpath);
            } else if (doctype == 2) {
                results = getCSVLevels (excelpath);
            } else {
                results = null;
            }
            if (results == null) {
                JOptionPane.showMessageDialog(null,"Error retrieving excel file","Error",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            peptidelist = results[0];
            deuterium = results[1];        

            try {
                deuteration = calculateResidueDeuteration (peptidelist, deuterium, sequence);
            } catch (PeptideMismatchException exc) {
                JOptionPane.showMessageDialog(null,exc.getMessage(),"Error",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            color = MSReader.COLOR_BLUE;
        } else {
            //protection type map
            int doctype1, doctype2;
            String excelpath = excel[0].toString();
            if (excelpath.toLowerCase().contains(".xlsx")) doctype1 = 0;
            else if (excelpath.toLowerCase().contains(".xls")) doctype1 = 1;
            else if (excelpath.toLowerCase().contains(".csv")) doctype1 = 2;
            else if (excelpath.toLowerCase().contains(".txt")) doctype1 = 2;
            else {
                Utils.showErrorMessage ("Not a valid excel file");
                return;
            }
            
            List<String> peptidelistfree;
            List<Double> deuteriumfree;
            ArrayList[] results;
            if (doctype1 == 0) {
                results = getXLSXLevels (excelpath);
            } else if (doctype1 == 1) {
                results = getXLSLevels (excelpath);
            } else if (doctype1 == 2) {
                results = getCSVLevels (excelpath);
            } else {
                results = null;
            }
            if (results == null) {
                JOptionPane.showMessageDialog(null,"Error retrieving excel file","Error",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            peptidelistfree = results[0];
            deuteriumfree = results[1];   
            
            excelpath = excel[1].toString();
            if (excelpath.toLowerCase().contains(".xlsx")) doctype2 = 0;
            else if (excelpath.toLowerCase().contains(".xls")) doctype2 = 1;
            else if (excelpath.toLowerCase().contains(".csv")) doctype2 = 2;
            else if (excelpath.toLowerCase().contains(".txt")) doctype2 = 2;
            else {
                Utils.showErrorMessage ("Not a valid excel file");
                return;
            }
            
            List<String> peptidelistbound;
            List<Double> deuteriumbound;
            if (doctype2 == 0) {
                results = getXLSXLevels (excelpath);
            } else if (doctype2 == 1) {
                results = getXLSLevels (excelpath);
            } else if (doctype2 == 2) {
                results = getCSVLevels (excelpath);
            } else {
                results = null;
            }
            if (results == null) {
                JOptionPane.showMessageDialog(null,"Error retrieving excel file","Error",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            peptidelistbound = results[0];
            deuteriumbound = results[1];
            
            try {
                double[] dfree = calculateResidueDeuteration (peptidelistfree, deuteriumfree, sequence);
                double[] dbound = calculateResidueDeuteration (peptidelistbound, deuteriumbound, sequence);
                deuteration = new double [sequence.length()];
                for (int i = 0; i < deuteration.length; i++) {
                    deuteration[i] = dfree[i] - dbound[i];
                }
            } catch (PeptideMismatchException exc) {
                JOptionPane.showMessageDialog(null,exc.getMessage(),"Error",
                    JOptionPane.ERROR_MESSAGE); 
                return;
            }
            color = MSReader.COLOR_RED;
        }
        int startIndex = -1;
        int[] tailingResidues;
        try {
            startIndex = getStartIndex (pdbfile, sequence);
            tailingResidues = getExtraIndices (pdbfile, startIndex, sequence);
        } catch (PeptideMismatchException exc) {
            JOptionPane.showMessageDialog(null,"Error: PDB sequence does not match input sequence. "
                        + "Your entire sequence must be contained in the PDB sequence - try shortening"
                        + " the input sequence","Error",
                JOptionPane.ERROR_MESSAGE); 
            return;
        } catch (IOException io) {
            JOptionPane.showMessageDialog(null,"Unable to read PDB file","Error",
                JOptionPane.ERROR_MESSAGE); 
            return;
        }
        createHeatMapExternGrad (gradient, deuteration, pdbfile, pymol, outputpath, color, startIndex, tailingResidues);
    }
    
    private static double[] calculateResidueDeuteration (List<String> peptidelist, List<Double> deuterium, String sequence) 
            throws PeptideMismatchException {
        double[] sums = new double[sequence.length()];
        Arrays.fill(sums, 0.0);
        double[] scaleFactors = new double[sequence.length()];
        Arrays.fill(scaleFactors, 0.0);
        for (int j = 0; j < peptidelist.size(); j++) {
            String ex = peptidelist.get(j);
            int index = sequence.toUpperCase().indexOf(ex.toUpperCase());
            double scale = 1/(double)ex.length();
            if (index < 0) throw new PeptideMismatchException ("Residue not found: "+ex);
            for (int i = index; i < index + ex.length(); i++) {
                scaleFactors[i] += scale;
                sums[i] += (scale*deuterium.get(j));
            }
        }
        
        for (int i = 0; i < sums.length; i++) {
            sums[i] /= scaleFactors[i];
        }
        return sums;
    }
    
    private static HDRun[][] getHDXFormArray (File[] files) {
        HDRun[] hdr = new HDRun[files.length];
        ObjectInputStream in = null;
        for (int i = 0; i < files.length; i++) {
            try {
                in = new ObjectInputStream(new FileInputStream(files[i]));
                HDRun hdx = (HDRun)in.readObject();
                hdr[i] = hdx;
                in.close();
            } catch (IOException e ) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
        HDRun[][] sender = new HDRun[2][];
        sender[0] = hdr;
        return sender;
    }
    
    private static HDRun[][] getHDXFormArray (File[] files1, File[] files2) {
        HDRun[] hdr1 = new HDRun[files1.length];
        ObjectInputStream in = null;
        for (int i = 0; i < files1.length; i++) {
            try {
                in = new ObjectInputStream(new FileInputStream(files1[i]));
                HDRun hdx = (HDRun)in.readObject();
                hdr1[i] = hdx;
                in.close();
            } catch (IOException e ) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
        
        HDRun[] hdr2 = new HDRun[files2.length];
        for (int i = 0; i < files2.length; i++) {
            try {
                in = new ObjectInputStream(new FileInputStream(files2[i]));
                HDRun hdx = (HDRun)in.readObject();
                hdr2[i] = hdx;
                in.close();
            } catch (IOException e ) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
        HDRun[][] sender = new HDRun[2][];
        sender[0] = hdr1;
        sender[1] = hdr2;
        return sender;
    }
    
    private static int getStartIndex (File pdb, String sequence) throws PeptideMismatchException, IOException {
        PDBFileReader reader = new PDBFileReader();
        Structure s = reader.getStructure(pdb);
        List<Chain> chains = s.getChains();
        String matcher = sequence.substring(0, 5);
        Chain chain = null;
        for (Chain c: chains) {
            if (c.getAtomSequence().toLowerCase().contains(matcher.toLowerCase())) {
                chain = c; 
                break;
            }
        }
        if (chain == null) throw new PeptideMismatchException();
        int startIndex = chain.getAtomSequence().toLowerCase().indexOf(matcher.toLowerCase());
        List<Group> groups = chain.getAtomGroups();
        if (!groups.get(startIndex).hasAminoAtoms()) throw new PeptideMismatchException();
        return groups.get(startIndex).getResidueNumber().getSeqNum();
    }
    
    private static int[] getExtraIndices (File pdb, int startIndex, String sequence) 
            throws PeptideMismatchException, IOException {
        PDBFileReader reader = new PDBFileReader();
        Structure s = reader.getStructure(pdb);
        List<Chain> chains = s.getChains();
        String matcher = sequence.substring(0, 5);
        Chain chain = null;
        for (Chain c: chains) {
            if (c.getAtomSequence().toLowerCase().contains(matcher.toLowerCase())) {
                chain = c; 
                break;
            }
        }
        if (chain == null) throw new PeptideMismatchException();
        
        int chainStart = -1;
        for (Group g: chain.getAtomGroups()) {
            if (g.hasAminoAtoms()) {
                chainStart = g.getResidueNumber().getSeqNum();
                break;
            }
        }
        
        int chainEnd = -1; 
        for (Group g: chain.getAtomGroups()) {
            if (g.hasAminoAtoms()) {
                chainEnd = g.getResidueNumber().getSeqNum();
            }
        }
        
        int seqEnd = chainStart + chain.getAtomSequence().toLowerCase().indexOf(sequence.toLowerCase()) + sequence.length();
        if (chainStart == startIndex && chainEnd == seqEnd) return null;
        else {
            int[] ret = new int [(startIndex-chainStart) + (chainEnd-seqEnd)];
            int k = 0;
            for (int i = chainStart; i < startIndex; i++) {
                ret[k++] = i;
            }
            for (int i = seqEnd+1; i <= chainEnd; i++) {
                ret[k++] = i;
            }
            return ret;
        }
    }
    
    public static File getPymolLocation() {
        try {
            FindPymol pf = new FindPymol();
            Path startingDir = Paths.get(System.getProperty("file.separator"));
            Files.walkFileTree(startingDir, pf);
            String path = System.getProperty("user.dir");
            String sep = System.getProperty("file.separator");
            String root = path.substring(0, path.indexOf(sep)+1);
            return new File (root+pf.pymol.toString());  
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
    private static ArrayList[] getCSVLevels (String csvpath) {
        ArrayList<String> peptidelist = new ArrayList();
        ArrayList<Double> deuterium = new ArrayList();
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File (csvpath));
            Scanner s = new Scanner(in);
            s.useDelimiter(",|\n|\t");
            while (s.hasNext()) {
                peptidelist.add(s.next());
                deuterium.add(Double.parseDouble(s.next()));
            }
            in.close();
            return new ArrayList[] {peptidelist, deuterium};
        } catch (IOException e) { 
            return null;
        }
    }
    
    private static ArrayList[] getXLSXLevels (String excelpath) {
        FileInputStream f = null;
        try {
            f = new FileInputStream(new File(excelpath));
            ArrayList<String> peptidelist = new ArrayList();
            ArrayList<Double> deuterium = new ArrayList();
            XSSFWorkbook wb = new XSSFWorkbook(f);
            XSSFSheet sheet = wb.getSheetAt(0);
            int type = 0;
            int row = 0;
            while (type != 3) {
                try {
                     type = sheet.getRow(row).getCell(0).getCellType(); 
                } catch (NullPointerException e) { type = 3; }
                if (type == Cell.CELL_TYPE_STRING){
                    String x = sheet.getRow(row).getCell(0).getStringCellValue();
                    x = Utils.trimPeptide(x);
                    peptidelist.add(x);
                }
                try {
                     type = sheet.getRow(row).getCell(1).getCellType(); 
                } catch (NullPointerException e) { type = 3; } 
                if (type == Cell.CELL_TYPE_NUMERIC){
                     deuterium.add(sheet.getRow(row).getCell(1).getNumericCellValue());
                }
                row++;
            }
            f.close();
            return new ArrayList[] {peptidelist, deuterium};
        } catch (IOException e) {
            return null;
        }
    }
    
    private static ArrayList[] getXLSLevels (String excelpath) {
        FileInputStream f = null;
        try {
            f = new FileInputStream(new File(excelpath));
            ArrayList<String> peptidelist = new ArrayList();
            ArrayList<Double> deuterium = new ArrayList();
            HSSFWorkbook wb = new HSSFWorkbook(f);
            HSSFSheet sheet = wb.getSheetAt(0);
            int type = 0;
            int row = 0;
            while (type != 3) {
                try {
                     type = sheet.getRow(row).getCell(0).getCellType(); 
                } catch (NullPointerException e) { type = 3; }
                     if (type == Cell.CELL_TYPE_STRING){
                         String x = sheet.getRow(row).getCell(0).getStringCellValue();
                         x = Utils.trimPeptide (x);
                         peptidelist.add(x);
                     }
                try {
                     type = sheet.getRow(row).getCell(1).getCellType(); 
                } catch (NullPointerException e) { type = 3; } 
                if (type == Cell.CELL_TYPE_NUMERIC){
                     deuterium.add(sheet.getRow(row).getCell(1).getNumericCellValue());
                 }
                row++;
            }
            f.close();
            return new ArrayList[] {peptidelist, deuterium};
        } catch (IOException e) {
            return null;
        }
    }
    
    private static void createHeatMapExternGrad (HeatMapGradient gradient, double[] deuteration, File pdb, File pymol, File scriptpath, int color, int startIndex, int[] extraIndices) {
        int[][] rgb = new int[deuteration.length][3];
        double totalmax = MSMath.getMax(deuteration);
        double min = MSMath.getMin(deuteration);
        for (int i = 0; i < rgb.length; i++) {
            if (color == 0) {
                rgb[i] = gradient.getRGBRed (deuteration[i]);
            } else {
                rgb[i] = gradient.getRGBBlue (deuteration[i]);
            }
        }
        Class resourceClass = ProtectionMap.class;
        InputStream is = resourceClass.getResourceAsStream ("/resources/script.py");
        try {
            PrintWriter out = new PrintWriter(new FileWriter(scriptpath));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String current;
            while ((current=br.readLine()) != null) {
                out.println(current);
            }
            br.close();
            for (int i = 0; i < rgb.length; i++) {
                String output = "colorrgb(["+rgb[i][0]+", "+rgb[i][1]+", "+rgb[i][2]+"], \"resi "+(i+startIndex)+"\")";
                out.println(output);
            }
            if (extraIndices != null) {
                for (int i = 0; i < extraIndices.length; i++) {
                    String output = "colorrgb([255,255,255], \"resi "+(extraIndices[i])+"\")";
                    out.println(output);
                }
            }
            out.println("print \"max: " + totalmax + " D/res   min: " + min + " D/res\"");
            out.close();
            String[] command;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                command = new String[] {"\""+pymol.getAbsolutePath() + "\" "+ 
                    "\""+pdb.getAbsolutePath() + "\" -r \""+scriptpath+"\""};
            } else {
                //command = new String[] {"open \""+pymol.getAbsolutePath() + "\" --args \""+pdb.getAbsolutePath()+"\" -r \""+scriptpath+"\"";
                command = new String[] {"open", pymol.getAbsolutePath(), "--args", pdb.getAbsolutePath(), "-r", scriptpath.getAbsolutePath()}; 
            }
            Runtime.getRuntime().exec(command);
       } catch (IOException e) {
           Utils.showErrorMessage ("Unable to create heat map");
       }
    }
    
    public static void main (String[] args) throws Exception {
        File pymol = new File ("C:/Program Files (x86)/DeLano Scientific/PyMOL/PyMOL.exe");
        File pdb = new File ("C:/Users/Alex/Documents/PDBs/C2 alone.pdb");
        File scriptpath = new File ("C:/Users/Alex/Documents/MSReader files/bin/heatmap.py");
        //String[] command = new String[] {"\""+pymol.getAbsolutePath() + "\" "+ 
        //            "\""+pdb.getAbsolutePath() + "\" -r \""+scriptpath+"\""};
        String[] command = new String[] {pymol.getAbsolutePath(), pdb.getAbsolutePath(), "-r", scriptpath.getAbsolutePath()};
        Runtime.getRuntime().exec(command);
        
    }
    
//    private static void createHeatMap (double[] deuteration, File pdb, File pymol, File scriptpath, int color, int startIndex, int[] extraIndices) {
//        double[][] rgb = new double[deuteration.length][3];
//        double totalmax = MSMath.getMax(deuteration);
//        double max = (color == 0) ? .15 : totalmax;
//        double min = MSMath.getMin(deuteration);
//        for (int i = 0; i < rgb.length; i++) {
//            if (color == 0) rgb[i] = getRGBRed (deuteration[i], max);
//            else {
//                rgb[i] = getRGBBlue (deuteration[i], max);
//            }
//        }
//        Class resourceClass = ProtectionMap.class;
//        InputStream is = resourceClass.getResourceAsStream ("/resources/script.py");
//        try (PrintWriter out = new PrintWriter(new FileWriter(scriptpath))) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            String current;
//            while ((current=br.readLine()) != null) {
//                out.println(current);
//            }
//            br.close();
//            for (int i = 0; i < rgb.length; i++) {
//                String output = "colorrgb(["+rgb[i][0]+", "+rgb[i][1]+", "+rgb[i][2]+"], \"resi "+(i+startIndex)+"\")";
//                out.println(output);
//            }
//            if (extraIndices != null) {
//                for (int i = 0; i < extraIndices.length; i++) {
//                    String output = "colorrgb([255,255,255], \"resi "+(extraIndices[i])+"\")";
//                    out.println(output);
//                }
//            }
//            out.println("print \"max: " + totalmax + "   min: " + min + "\"");
//            out.close();
//            String command = "\""+pymol.getAbsolutePath() + "\" "+ 
//                    "\""+pdb.getAbsolutePath() + "\" -r \""+scriptpath+"\"";
//            Runtime.getRuntime().exec(command);
//       } catch (IOException e) {
//           e.printStackTrace();
//       }
//       
//    }
    
    private static double[] getRGBRed (double deut, double max) {
        if (Double.isNaN(deut)) {
            return new double[] {255, 255, 255};
        } else if (deut > .95 * max) {
            return new double [] {150, 0, 0};
        } else if (deut > .9 *max) {
            return new double [] {165, 0, 0};
        } else if (deut > .85 * max) {
            return new double [] {175, 0, 0};
        } else if (deut > .8 * max) {
            return new double [] {185, 0, 0};
        } else if (deut > .75 * max) {
            return new double [] {255, 0, 0};
        } else if (deut > .7*max) {
            return new double [] {255, 40, 0};
        } else if (deut > .6*max) {
            return new double [] {255, 80, 0};
        } else if (deut > .5*max) {
            return new double [] {255, 100, 0};
        } else if (deut > .4*max) {
            return new double [] {255, 140, 0};
        } else if (deut > .3*max) {
            return new double [] {255, 160, 0};
        } else if (deut > .2*max) {
            return new double [] {255, 200, 0};
        } else if (deut > .1*max) {
            return new double [] {255, 225, 0};
        } else if (deut > -1) {
            return new double [] {255, 255, 0};
        } else if (deut == -1) {
            return new double [] {255, 255, 255};
        } else return null;
     }
     
    private static double[] getRGBBlue (double deut, double max) {
        if (Double.isNaN(deut)) {
            return new double [] {255, 255, 255};
        } else if (deut > .97 * max) {
            return new double [] {0, 0, 150};
        } else if (deut > .94 *max) {
            return new double [] {0, 0, 165};
        } else if (deut > .90 * max) {
            return new double [] {0, 0, 175};
        } else if (deut > .86 * max) {
            return new double [] {0, 0, 185};
        } else if (deut > .83 * max) {
            return new double [] {0, 0, 195};
        } else if (deut > .8*max) {
            return new double [] {0, 0, 200};
        } else if (deut > .75*max) {
            return new double [] {0, 0, 255};
        } else if (deut > .7*max) {
            return new double [] {0, 40, 255};
        } else if (deut > .65*max) {
            return new double [] {0, 80, 255};
        } else if (deut > .6*max) {
            return new double [] {0, 160, 255};
        } else if (deut > .4*max) {
            return new double [] {0, 200, 255};
        } else if (deut > .3*max) {
            return new double [] {0, 225, 255};
        } else if (deut > -1) {
            return new double [] {0, 255, 255};
        } else if (deut == -1) {
            return new double [] {255, 255, 255};
        } else {
            return null;
        }
     }
}


class FindPymol implements FileVisitor {
    String pymol;

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        if (file.toString().toLowerCase().contains("pymol.exe")) {
            pymol = file.toString();
            return TERMINATE;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc) throws IOException {
        return CONTINUE;
    }
    
}