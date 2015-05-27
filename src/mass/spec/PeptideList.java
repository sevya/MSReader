package mass.spec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PeptideList implements Serializable {
    
    private ArrayList<Peptide> elements;
    public String[] labels = {"Sequence", "M/z", "Z", "Elution time"};
    static final long serialVersionUID = 646546;
    public File path;
    private int sortKey;
    
    public PeptideList() {
        elements = new ArrayList();
        sortKey = 1;
    }
    
    public int size() {
        return elements.size();
    }
    
    public void setPath (File f) {
        path = f;
    }
    
    public void save () {
        if (path.exists()) path.delete();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream (path));
            try {
                oos.writeObject(this);
            } finally { 
                oos.close();
            } 
        } catch ( IOException e ) { 
            Utils.logException( e, "Unable to save" );
        } 
    }
    
    public void addPeptide( Peptide p ) {
        if ( p!= null ) { 
            elements.add( p ); 
            sort();
        }
    }
    
    public void removePeptide ( int index ) {
        if ( index >= elements.size() || index < 0 ) {
            throw new IndexOutOfBoundsException();
        }
        elements.remove(index);
        sort();
    }
    
    public Peptide elementAt ( int index ) {
        if ( index >= elements.size() || index < 0 ) {
            throw new IndexOutOfBoundsException();
        }
        return elements.get(index);
    }
    
    public int getSortKey () { return sortKey; }
    
    public void setSortKey ( int key ) { 
        if ( key > 3 || key < 0) throw new IndexOutOfBoundsException();
        sortKey = key; 
        sort();
    }
    
    public File getPath () { return path; }
    
    public void sort () {
        switch ( sortKey ){
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
}