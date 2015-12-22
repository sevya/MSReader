package mass.spec;

import java.security.InvalidParameterException;

public class Timer {
    
    long start;
    
    public Timer() {
        start = System.nanoTime();
    }
    
    public float getTime() {
        return (System.nanoTime() - start)/(float)Math.pow(10, 9);
    }
    
    public void reset() {
        start = System.nanoTime();
    }
    
    public void printTime() {
        System.out.println(getTime()+" seconds elapsed");
    }
}
