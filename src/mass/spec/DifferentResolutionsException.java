package mass.spec;

/*
 * Used in doing comparing different spectra, such
 * as in background subtraction or merging spectra
 * Exception is thrown if the control scan and the test scan have
 * different step sizes
 */


public class DifferentResolutionsException extends Exception{
    
    public DifferentResolutionsException() {
        super();
    }
    
    public DifferentResolutionsException(String str) {
        super(str);
    }
}

