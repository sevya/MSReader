package mass.spec;

import java.util.*;
import org.apache.commons.math3.fitting.PolynomialFitter;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;

/*
 * Constructs a savitzky golay smoothing point for each given array
 * Construct a savitzky golay object with your data and your desired window and degree,
 * then use getSmoothedPoint (i) to get a smoothed data point for each index
 * 
 * ex:
 * SavitzkyGolay sg = new SavitzkyGolay (mydata, window, degree);
 * for (int i = 0; i < data.length; i++) {
 *     data[i] = sg.getSmoothPoint(i);
 * }
 */

public class SavitzkyGolay {
    float[][] data;
    int degree;
    int window;
    
        public SavitzkyGolay (float[][] vals, int wind, int deg) {
            if (window%2 != 1) window-=1;
            window = wind;
            degree = deg;
            data = new float[2][];
            data[0] = Arrays.copyOf(vals[0], vals[0].length);
            data[1] = Arrays.copyOf(vals[1], vals[1].length);
        }
        
        public SavitzkyGolay (float[] xvals, float[] yvals, int wind, int deg) {
            if (window%2 != 1) window-=1;
            window = wind;
            degree = deg;
            data = new float[2][];
            data[0] = Arrays.copyOf(xvals, xvals.length);
            data[1] = Arrays.copyOf(yvals, yvals.length);
        }
        
        public float getSmoothedPoint (int index) {
            if (index < window) index = window;
            if (index > data[0].length - window) index = data[0].length - window;
            float[][] values = new float [2][];
            values[0] = Arrays.copyOfRange(data[0], index - window, index + window);
            values[1] = Arrays.copyOfRange(data[1], index - window, index + window);
            double[] parameters = fitPolynomial(values);
            float sum = 0;
            for (int i = 0; i < degree; i++) {
                sum += parameters[i]*Math.pow(data[0][index], i);
            }
            return sum;
        }
        
        private double[] fitPolynomial (float[][] data) {
            PolynomialFitter fitter = new PolynomialFitter(new LevenbergMarquardtOptimizer());
             for (int i = 0; i < data[0].length; i++) {
                 fitter.addObservedPoint(data[0][i], data[1][i]);
             }
             double[] weights = new double[degree];
             Arrays.fill(weights, 1.0);
             double[] parameters = fitter.fit(weights);
             return parameters;
        }
        
    }

