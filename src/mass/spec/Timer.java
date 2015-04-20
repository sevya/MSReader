package mass.spec;

import java.security.InvalidParameterException;

public class Timer {
    
    long start;
    
    public Timer() {
        start = System.nanoTime();
    }
    
    public long getTime() {
        return (System.nanoTime() - start)/(long)Math.pow(10, 6);
    }
    
    public void reset() {
        start = System.nanoTime();
    }
    
    public void printTime() {
        System.out.println(getTime());
    }
}
