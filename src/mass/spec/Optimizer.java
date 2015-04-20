package mass.spec;

import java.security.InvalidParameterException;
import java.util.*;
import org.jfree.data.xy.XYSeriesCollection;

/*Optimizer for bivariate equations
 * 
 * Uses a generalized reduced gradient algorithm to minimize the chi-square value from 
 * the user generated function and x y values.
 * 
 * 
 * Requires implementation of the method function that takes three parameters and returns a double
 * takes as parameters the first variable, the second variable, and the value of x
 * returns the value of the function at that point
 * 
 * 
 * Then running opt() will return an array of your two optimized variables
 * Opt takes two parameters, which are initial guesses for each of the two variables
 * 
 * ex:
 * class TestClass extends Optimizer {
 *  public TestClass (double[] x, double[] y) {
 *   super (x, y);
 * }
 *  
 * public double function (double v1, double v2, double xpoint) {
 *  return v1*(1-Math.exp(-v2*xpoint));
 * }
 * 
 * double[] results = new TestClass (myXValues, myYValues).optimize(initialGuess1, initialGuess2);
 * 
 * or:
 * 
 * double[] results = new Optimizer () {
 *  @Override
 *  public double function (double v1, double v2, double xpoint) {
 *      return v1*(1-Math.exp(-v2*xpoint));
 *  }
 * }.optimize(.25, .25);
 */

public abstract class Optimizer {

    double[] x;
    double[] y;
    double increment;
    List<Integer> outcomeLog;
    private final int DEC_A = -1;
    private final int INC_A = 1;
    private final int DEC_K = -2;
    private final int INC_K = 2;
    private final int INCREMENTED = 0;
    
    public Optimizer (double[] xvals, double[] yvals) {
        if (xvals.length != yvals.length) throw new IndexOutOfBoundsException();
        x = xvals;
        y = yvals;
        increment = 10;
        outcomeLog = new ArrayList();
    }
    
    public double[] optimize (double A, double K) {
        if (outcomeLog.size() > 4) outcomeLog.remove(0);
        if ((Collections.frequency(outcomeLog, INC_A)==2) && (Collections.frequency(outcomeLog, DEC_A)==2)) {
            increment/= 10;
            outcomeLog.add(INCREMENTED);
            return optimize (A, K);
        } else if ((Collections.frequency(outcomeLog, INC_K)==2) && (Collections.frequency(outcomeLog, DEC_K)==2)) {
            increment/= 10;
            outcomeLog.add(INCREMENTED);
            return optimize (A, K);
        }
        double slopeAboveA = slopeAboveA (A, K, increment);
        double slopeBelowA = slopeBelowA (A, K, increment);
        double slopeAboveK = slopeAboveK (A, K, increment);
        double slopeBelowK = slopeBelowK (A, K, increment);
//        System.out.println("A: "+A+"\tK: "+K);
//        System.out.println("A slopes: "+slopeBelowA + " - "+slopeAboveA);
//        System.out.println("K slopes: "+slopeBelowK + " - "+slopeAboveK);
//        System.out.println("increment = "+increment);
        int outcomeA, outcomeK;
        if (slopeAboveA >= 0 && slopeBelowA >= 0) {
            outcomeA = 0;
        } else if (slopeAboveA < 0 && slopeBelowA < 0) {
            if (slopeAboveA < slopeBelowA) outcomeA = 1;
            else outcomeA = -1;
        } else if (slopeAboveA < 0) {
            outcomeA = 1;
        } else if (slopeBelowA < 0) {
            outcomeA = -1;
        } else if (Double.isNaN (slopeBelowA)) {
            outcomeA = 1;
        } else if (Double.isNaN (slopeAboveA)) {
            outcomeA = -1;
        } else {
            throw new InvalidParameterException();
        }
        
        if (slopeAboveK >= 0 && slopeBelowK >= 0) {
            outcomeK = 0;
        } else if (slopeAboveK < 0 && slopeBelowK < 0) {
            if (slopeAboveK < slopeBelowK) outcomeK = 1;
            else outcomeK = -1;
        } else if (slopeAboveK < 0) {
            outcomeK = 1;
        } else if (slopeBelowK < 0) {
            outcomeK = -1;
        } else if (Double.isNaN(slopeBelowK)) {
            outcomeK = 1;
        } else if (Double.isNaN(slopeAboveK)) {
            outcomeK = -1;
        } else {
            throw new InvalidParameterException();
        }
        if (outcomeK == 0 && outcomeA == 0) {
            if (increment < Math.pow (10, -6)) {
                return new double[] {A, K};
            }
            else {
                increment/= 10;
                outcomeLog.add(INCREMENTED);
                return optimize (A, K);
            }
        } else if (outcomeK != 0 && outcomeA == 0) {
            if (outcomeK == 1) {
                outcomeLog.add(INC_K);
                return optimize (A, K+increment);
            }
            else {
                outcomeLog.add(DEC_K);
                return optimize (A, K-increment);
            }
        } else if (outcomeK == 0 && outcomeA != 0) {
            if (outcomeA == 1) {
                outcomeLog.add(INC_A);
                return optimize (A+increment, K);
            }
            else {
                outcomeLog.add(DEC_K);
                return optimize (A-increment, K);
            }
        } else {
            if (outcomeA == 1 && outcomeK == 1) {
                if (slopeAboveA < slopeAboveK) {
                    outcomeLog.add(INC_A);
                    return optimize (A+increment, K);
                }
                else {
                    outcomeLog.add(INC_K);
                    return optimize (A, K+increment);
                }
            } else if (outcomeA == -1 && outcomeK == 1) {
                if (slopeBelowA < slopeAboveK) {
                    outcomeLog.add(DEC_A);
                    return optimize (A-increment, K);
                }
                else {
                    outcomeLog.add(INC_K);
                    return optimize (A, K+increment);
                }
            } else if (outcomeA == 1 && outcomeK == -1) {
                if (slopeAboveA < slopeBelowK) {
                    outcomeLog.add(INC_A);
                    return optimize (A+increment, K);
                }
                else {
                    outcomeLog.add(DEC_K);
                    return optimize (A, K-increment);
                }
            } else if (outcomeA == -1 && outcomeK == -1) {
                if (slopeBelowA < slopeBelowK) {
                    outcomeLog.add(DEC_A);
                    return optimize (A-increment, K);
                }
                else {
                    outcomeLog.add(DEC_K);
                    return optimize (A, K-increment);
                }
            } else {
                throw new InvalidParameterException();
            }
        } 
    }
    
    private double slopeAboveA (double A, double K, double inc) {
        return computefunction (A + inc, K) - computefunction (A, K);
    }
    
    private double slopeBelowA (double A, double K, double inc) {
        return computefunction (A - inc, K) - computefunction (A, K);
    }
    
    private double slopeAboveK (double A, double K, double inc) {
        return computefunction (A, K + inc) - computefunction (A, K);
    }

    private double slopeBelowK (double A, double K, double inc) {
        return computefunction (A, K - inc) - computefunction (A, K);
    }
    
    abstract double function (double A, double K, double xval); 

    private double computefunction (double A, double K) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += Math.pow(y[i] - function(A, K, x[i]), 2);
        }
        return sum;
    }
}
