package mass.spec;

/*
 * Used in analyzing input sequences against PDB sequence
 * If a user generated sequence does not match the 
 * sequence input in the heat map menu, a mismatch exception
 * will be thrown
 * 
 */

public class PeptideMismatchException extends Exception {
    public PeptideMismatchException() {
        super();
    }
    
    public PeptideMismatchException (String str) {
        super(str);
    }
}
