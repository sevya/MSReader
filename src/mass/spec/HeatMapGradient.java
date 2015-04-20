package mass.spec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class HeatMapGradient implements Serializable {
    ArrayList<Level> redGrades;
    ArrayList<Level> blueGrades;
    static final long serialVersionUID = 489456123;
    File path;
    
    public HeatMapGradient() {
        redGrades = new ArrayList();
        blueGrades = new ArrayList();
    }
    
    public void addRedLevel (double thresh, int[] rgb) {
        redGrades.add(new Level (thresh, rgb));
    }
    
    public void addBlueLevel (double thresh, int[] rgb) {
        blueGrades.add(new Level (thresh, rgb));
    }
    
    public double getRedLevel (int index) {
        return redGrades.get(index).lowerThresh;
    }
    
    public double getBlueLevel (int index) {
        return blueGrades.get(index).lowerThresh;
    }
    
    public void removeRedLevel (int index) {
        redGrades.remove(index);
    }
    
    public void removeBlueLevel (int index) {
        blueGrades.remove(index);
    }
    
    public void setPath (File f) {
        path = f;
    }
    
    public int[] getRGBRed (double value) {
        if (Double.isNaN(value)) return new int[] {255, 255, 255};
        Collections.sort(redGrades);
        for (int i = 0; i < redGrades.size(); i++) {
            if (value >= redGrades.get(i).lowerThresh) return redGrades.get(i).rgb;
            else continue;
        }
        return new int[] {255, 255, 0};
    }
    
    public int[] getRGBBlue (double value) {
        if (Double.isNaN(value)) return new int[] {255, 255, 255};
        Collections.sort(blueGrades);
        for (int i = 0; i < blueGrades.size(); i++) {
            if (value >= blueGrades.get(i).lowerThresh) return blueGrades.get(i).rgb;
            else continue;
        }
        return new int[] {0, 255, 255};
    }
    
    public String getRGBBlueAsString (int index) {
        return "["+blueGrades.get(index).rgb[0]+", "+blueGrades.get(index).rgb[1]+", "+blueGrades.get(index).rgb[2]+"]";
    }
    
    public String getRGBRedAsString (int index) {
        return "["+redGrades.get(index).rgb[0]+", "+redGrades.get(index).rgb[1]+", "+redGrades.get(index).rgb[2]+"]";
    }
    
    public int[] getRGBBlueFromIndex (int index) {
        return blueGrades.get(index).rgb;
    }
    
    public int[] getRGBRedFromIndex (int index) {
        return redGrades.get(index).rgb;
    }
    
    public void save () {
        if (path.exists()) path.delete();
        if (path==null) throw new NullPointerException();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream (path));
            oos.writeObject(this);
            oos.close();
        } catch (Exception e) { 
            Utils.showErrorMessage("Unable to save gradient");
        }
    }
    
    private void printRed() {
        for (Level a: redGrades) {
            System.out.println(a.toString());
        }
    }
    
    private void printBlue() {
        for (Level a: blueGrades) {
            System.out.println(a.toString());
        }
    }
    
    public static void main (String[] args) {
        HeatMapGradient grade = new HeatMapGradient();
        grade.addRedLevel (.1425, new int[] {150, 0, 0});
        grade.addRedLevel (.135, new int[] {165, 0, 0});
        grade.addRedLevel (.1275, new int[] {175, 0, 0});
        grade.addRedLevel (.12, new int[] {185, 0, 0});
        grade.addRedLevel (.1125, new int[] {255, 0, 0});
        grade.addRedLevel (.105, new int[] {255, 40, 0});
        grade.addRedLevel (.09, new int[] {255, 80, 0});
        grade.addRedLevel (.075, new int[] {255, 100, 0});
        grade.addRedLevel (.06, new int[] {255, 140, 0});
        grade.addRedLevel (.045, new int[] {255, 160, 0});
        grade.addRedLevel (.03, new int[] {255, 200, 0});
        grade.addRedLevel (.015, new int[] {255, 225, 0});
        grade.addBlueLevel (.379917, new int[] {0, 0, 150});
        grade.addBlueLevel (.368167, new int[] {0, 0, 165});
        grade.addBlueLevel (.3525, new int[] {0, 0, 175});
        grade.addBlueLevel (.336833, new int[] {0, 0, 185});
        grade.addBlueLevel (.325083, new int[] {0, 0, 195});
        grade.addBlueLevel (.313333, new int[] {0, 0, 200});
        grade.addBlueLevel (.29375, new int[] {0, 0, 255});
        grade.addBlueLevel (.274167, new int[] {0, 40, 255});
        grade.addBlueLevel (.254583, new int[] {0, 80, 255});
        grade.addBlueLevel (.235, new int[] {0, 160, 255});
        grade.addBlueLevel (.156667, new int[] {0, 200, 255});
        grade.addBlueLevel (.1175, new int[] {0, 225, 255});
        
        for (int a: grade.getRGBBlue (.325084)) System.out.print(a+"\t");
    }
    
    private class Level implements Comparable<Level>, Serializable {
        double lowerThresh;
        int[] rgb;
        static final long serialVersionUID = 484561423;
        Level (double a, int[] b) {
            lowerThresh = a;
            rgb = b;
        }

        @Override
        public String toString() {
            return ">"+lowerThresh+": ["+rgb[0]+", "+rgb[1]+", "+rgb[2]+"]";
        }
        
        @Override
        public int compareTo (Level other) {
            if (lowerThresh < other.lowerThresh) return 1;
            else if (lowerThresh > other.lowerThresh) return -1;
            else return 0;
        }
    }
}
