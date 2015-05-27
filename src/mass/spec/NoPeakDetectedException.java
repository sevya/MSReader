package mass.spec;

/*
 * Used in detecting peaks in Auto HDX
 * Throws an exception when there is no peak detected in the defined section
 */

public class NoPeakDetectedException extends Exception{
    
    public NoPeakDetectedException() {
        super();
    }
    
    public NoPeakDetectedException(String str) {
        super(str);
    }
}

class InvalidTimePointException extends Exception{
    
    public InvalidTimePointException() {
        super();
    }
    
    public InvalidTimePointException(String str) {
        super(str);
    }
}