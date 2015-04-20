package mass.spec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeptideList implements Serializable {
    
    private ArrayList<Peptide> elements;
    public String[] labels = {"Sequence", "M/z", "Z", "Elution time"};
    static final long serialVersionUID = 646546;
    public File path;
    
    public PeptideList() {
        elements = new ArrayList();
    }
    
    public int size() {
        return elements.size();
    }
    
    public void setPath (File f) {
        path = f;
    }
    
    public void save () {
        if (path.exists()) path.delete();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream (path));
            oos.writeObject(this);
        } catch (Exception e) { 
            Utils.showErrorMessage ("Unable to save");
        } finally {
            if ( oos == null ) try {
                oos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void addPeptide(Peptide p) {
        elements.add(p);
    }
    
    public void removePeptide (int index) {
        elements.remove(index);
    }
    
    public Peptide elementAt (int index) {
        return elements.get(index);
    }
    
    public void sort (int sorttype) {
        if (sorttype > 3 || sorttype < 0) throw new IndexOutOfBoundsException();
        switch (sorttype){
            case 0:
                Collections.sort(elements, new Comparator<Peptide>() {
                    @Override
                    public int compare(Peptide o1, Peptide o2) {
                        return o1.displaySequence.compareTo(o2.displaySequence);
                    }
                });
                break;
            case 1:
                Collections.sort(elements, new Comparator<Peptide>() {
                    @Override
                    public int compare(Peptide o1, Peptide o2) {
                        if (o1.mz > o2.mz) return 1;
                        else if (o1.mz < o2.mz) return -1;
                        else return 0;
                    }
                });
                break;
            case 2:
                Collections.sort(elements, new Comparator<Peptide>() {
                    @Override
                    public int compare(Peptide o1, Peptide o2) {
                        if (o1.charge > o2.charge) return 1;
                        else if (o1.charge < o2.charge) return -1;
                        else return 0;
                    }
                });
                break;
            case 3:
                Collections.sort(elements, new Comparator<Peptide>() {
                    @Override
                    public int compare(Peptide o1, Peptide o2) {
                        if (o1.elutiontime > o2.elutiontime) return 1;
                        else if (o1.elutiontime < o2.elutiontime) return -1;
                        else return 0;
                    }
                });
                break;
        }
    }
    
    private void print () {
        for (int i = 0; i < elements.size(); i++) {
            System.out.println(elements.get(i).displaySequence+"\t"+elements.get(i).mz+"\t"+elements.get(i).charge+"\t"+elements.get(i).elutiontime);
        }
    }
    
    public static void main (String[] args) {
        
    }
}