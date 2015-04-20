package mass.spec;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;
import org.uncommons.maths.random.BinomialGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;

public class Peptide implements Serializable {

    String sequence;
    String displaySequence;
    int charge;
    double MW;
    double mz;
    double elutiontime;
    int[] element_composition = new int[5];
    Random rand;
    static final long serialVersionUID = 35364321;
//    static double HMASS = 1.007825;
    List<Double> mass_vector;
    List<Double> prob_vector;
    static int OPTIMUM_THREAD_NO = 5;
    
    public Peptide (String str, int z, double elution) {
        if (str.length() < 1) throw new NullPointerException();
        displaySequence = str;
        sequence = Utils.trimPeptide(str);
        charge = z;
        elutiontime = elution;
        getElementComposition();
        MW = getMolecularWeight();
        mz = (MW+z)/z;
        mass_vector = Collections.synchronizedList(new ArrayList<Double>());
        prob_vector = Collections.synchronizedList(new ArrayList<Double>());
        rand = new Random();
    }
    
    public Peptide () {
        mass_vector = Collections.synchronizedList(new ArrayList<Double>());
        prob_vector = Collections.synchronizedList(new ArrayList<Double>());
        rand = new Random();
    }
    
    private double getMolecularWeight() {
        double mass = element_composition[0]*12+element_composition[1]*1.007825+
                element_composition[2]*14.003074+element_composition[3]*15.994915
                +element_composition[4]*31.972072;
        return mass;
    }

    private void getElementComposition() {
        element_composition = getElement(sequence.substring(0, 1));
        for (int i = 1; i < sequence.length(); i++) {
            add (element_composition, getElement(sequence.substring(i, i+1)));
        } 
    }
    
    private int[] getElement (String aa) {
        aa = aa.toUpperCase();
        if ( "A".equals(aa) ) return new int[] {3, 7, 1, 2, 0};
        else if ( "C".equals(aa) ) return new int[] {3, 7, 1, 2, 1};
        else if ( "V".equals(aa) ) return new int[] {5, 11, 1, 2, 0};
        else if ( "I".equals(aa) ) return new int[] {6, 13, 1, 2, 0};
        else if ( "L".equals(aa) ) return new int[] {6, 13, 1, 2, 0};
        else if ( "M".equals(aa) ) return new int[] {5, 11, 1, 2, 1};
        else if ( "P".equals(aa) ) return new int[] {5, 9, 1, 2, 0};
        else if ( "G".equals(aa) ) return new int[] {2, 5, 1, 2, 0};
        else if ( "F".equals(aa) ) return new int[] {9, 11, 1, 2, 0};
        else if ( "Y".equals(aa) ) return new int[] {9, 11, 1, 3, 0};
        else if ( "W".equals(aa) ) return new int[] {11, 12, 2, 2, 0};
        else if ( "H".equals(aa) ) return new int[] {6, 9, 3, 2, 0};
        else if ( "N".equals(aa) ) return new int[] {4, 8, 2, 3, 0};
        else if ( "Q".equals(aa) ) return new int[] {5, 10, 2, 3, 0};
        else if ( "S".equals(aa) ) return new int[] {3, 7, 1, 3, 0};
        else if ( "T".equals(aa) ) return new int[] {4, 9, 1, 3, 0};
        else if ( "K".equals(aa) ) return new int[] {6, 14, 2, 2, 0};
        else if ( "R".equals(aa) ) return new int[] {6, 14, 4, 2, 0};
        else if ( "D".equals(aa) ) return new int[] {4, 7, 1, 4, 0};
        else if ( "E".equals(aa) ) return new int[] {5, 9, 1, 4, 0};
        else throw new InvalidParameterException();
    }
    
    private void add (int[] one, int[] two) {
        if (one.length != two.length) throw new InvalidParameterException();
        for (int i = 0; i < one.length; i++) one[i] += two[i];
        one[1] -= 2;
        one[3] -= 1;
    }
    
    public double[][] getNonThreadedDistribution (int reps) {
        Thread thread = new Thread (new DistributionCalc(this, reps, 1, mass_vector, prob_vector, new Object()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ex) {
            //do nothing
        }
        double[][] data = new double[2][];
        data[0] = FormatChange.ArraylistToArray(mass_vector);
        data[1] = FormatChange.ArraylistToArray(prob_vector);
        return data;
    }
    
    public double[][] getThreadedDistribution (int reps) {
        int num_threads = OPTIMUM_THREAD_NO;
        Thread[] threads = new Thread[num_threads];
        final Object o = new Object();
        DistributionCalc calc = new DistributionCalc(this, reps, num_threads, mass_vector, prob_vector, o);
        for (int i = 0; i < threads.length; i++) {
//            threads[i] = new Thread(new DistributionCalc(this, reps, num_threads, mass_vector, prob_vector, o));
            threads[i] = new Thread(calc);
            threads[i].start();
        } for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                //do nothing
            }
        }
       double[][] data = new double[2][];
       data[0] = FormatChange.ArraylistToArray(mass_vector);
       data[1] = FormatChange.ArraylistToArray(prob_vector);
       return data;
    }
    
    public double[][] getNewDistribution (int reps) {
        MersenneTwisterRNG rng = new MersenneTwisterRNG();
        CarbonGenerator c = new CarbonGenerator(rng, element_composition[0]);
        HydrogenGenerator h = new HydrogenGenerator(rng, element_composition[1]);
        NitrogenGenerator n = new NitrogenGenerator(rng, element_composition[2]);
        OxygenGenerator o = new OxygenGenerator(rng, element_composition[3]);
        SulfurGenerator s = new SulfurGenerator(rng, element_composition[4]);
        mass_vector = new ArrayList();
        prob_vector = new ArrayList();
        double sum;
        double frequency = 1/(double)reps;
        for (int j = 0; j < reps; j++) {
            sum = (c.getSum()+h.getSum()+n.getSum()+o.getSum()+s.getSum()+(Isotope.H1.mass()*charge))/charge;
            int index = mass_vector.indexOf(sum);
            if (index != -1) {
                prob_vector.set(index, prob_vector.get(index)+frequency);
            } else {
                mass_vector.add(sum);
                prob_vector.add(frequency);
            }
        }
        return new double[][] {FormatChange.ArraylistToArray(mass_vector), 
            FormatChange.ArraylistToArray(prob_vector)};
    }
    
    public static void main (String[] args) {
       Peptide pept = new Peptide("FATWSPSKARLHLQGRSNAWRPQVNNPKEWLQCV", 2, 8.32);
       for (int i = 0; i < 1000; i++) {
           pept.getNonThreadedDistribution(100);
       }
       Timer t = new Timer();
       pept.getThreadedDistribution(1000000);
       t.printTime();
    }
}

class DistributionCalc implements Runnable {
    
    Peptide peptide;
    int reps;
    int num_threads;
    List<Double> mass_vector;
    List<Double> prob_vector;
    Random rand;
    final Object lockObject;
    
    public DistributionCalc (Peptide p, int r, int threadno, List<Double> mv, List<Double> pv, Object o) {
        peptide = p;
        reps = r;
        rand = new Random();
        mass_vector = mv;
        prob_vector = pv;
        num_threads = threadno;
        lockObject = o;
    }
    
    @Override
    public void run() {
        calculateDistribution(reps/num_threads);
    }
    
    private void calculateDistribution (int r) {
//        double frequency = 1/(double)reps;
//        for (int i = 0; i < r; i++) {
//            rand.setSeed(System.nanoTime());
//            double mass = 0;
//            for (int j = 0; j < peptide.element_composition[0]; j++) mass += carbon();
//            for (int j = 0; j < peptide.element_composition[1]; j++) mass += hydrogen();
//            for (int j = 0; j < peptide.element_composition[2]; j++) mass += nitrogen();
//            for (int j = 0; j < peptide.element_composition[3]; j++) mass += oxygen();
//            for (int j = 0; j < peptide.element_composition[4]; j++) mass += sulfur();
//            mass += (double)(peptide.charge*Peptide.HMASS);
//            mass /= (double)(peptide.charge);
//            synchronized (lockObject) {
//                int ind = mass_vector.indexOf(mass);
//                if (ind == -1) {
//                    mass_vector.add(mass);
//                    prob_vector.add(frequency);
//                } else {
//                    double c = prob_vector.get(ind);
//                    prob_vector.set(ind, c+frequency);
//                }
//            }
//        }
        
        MersenneTwisterRNG rng = new MersenneTwisterRNG();
        CarbonGenerator c = new CarbonGenerator(rng, peptide.element_composition[0]);
        HydrogenGenerator h = new HydrogenGenerator(rng, peptide.element_composition[1]);
        NitrogenGenerator n = new NitrogenGenerator(rng, peptide.element_composition[2]);
        OxygenGenerator o = new OxygenGenerator(rng, peptide.element_composition[3]);
        SulfurGenerator s = new SulfurGenerator(rng, peptide.element_composition[4]);
        double sum;
        double frequency = 1/(double)reps;
        for (int j = 0; j < r; j++) {
            sum = (c.getSum()+h.getSum()+n.getSum()+o.getSum()+s.getSum()+(peptide.charge*Isotope.H1.mass()))/(double)peptide.charge;
            synchronized (lockObject) {
                int index = mass_vector.indexOf(sum);
                if (index != -1) {
                    prob_vector.set(index, prob_vector.get(index)+frequency);
                } else {
                    mass_vector.add(sum);
                    prob_vector.add(frequency);
                }
            }
        }
    }
    
    private double carbon () {
        if (rand.nextInt(10000) < 107) return 13.0033548378; 
        else return 12.0;
    }
    
    private double hydrogen () {
        if (rand.nextInt(1000000) < 115) return 2.0141017780;
        else return 1.007825;
    }

    private double nitrogen () {
        if (rand.nextInt(100000) < 368) return 15.0001088984;
        else return 14.0030740052;
    }
    
    private double oxygen () {
       int outcome = rand.nextInt(100000);
       if (outcome < 38) return 16.9991312;
       else if (outcome < 243) return 17.999159;
       else return 15.994915; 
    }
    
    private double sulfur () {
       int outcome = rand.nextInt(10000);
       if (outcome < 2) return 35.96708062;
       else if (outcome < 78) return 32.97145843;
       else if (outcome < 507) return 33.96786665;
       else return 31.9720707; 
    }
}

class CarbonGenerator {
    
    BinomialGenerator eng;
    int reps;
    
    public CarbonGenerator (MersenneTwisterRNG rng, int r) {
        reps = r;
        if (reps == 0) eng = null;
        else eng = new BinomialGenerator(reps, Isotope.C13.prob(), rng);
    }
    
    public double getSum () {
        if (eng == null) return 0;
        double no12, no13;
        no13 = eng.nextValue();
        no12 = reps - no13;
        return Isotope.C12.mass()*no12 + Isotope.C13.mass()*no13;
    }
}

class HydrogenGenerator {
    BinomialGenerator eng;
    int reps;
    
    public HydrogenGenerator (MersenneTwisterRNG rng, int r) {
        reps = r;
        if (reps == 0) eng = null;
        else eng = new BinomialGenerator(reps, Isotope.H2.prob(), rng);
    }

    public double getSum() {
        if (eng == null) return 0;
        double no1, no2;
        no2 = eng.nextValue();
        no1 = reps - no2;
        return Isotope.H1.mass()*no1 + Isotope.H2.mass()*no2;
    }
}

class NitrogenGenerator {
    
    BinomialGenerator eng;
    int reps;
    
    public NitrogenGenerator (MersenneTwisterRNG rng, int r) {
        reps = r;
        if (reps == 0) eng = null;
        else eng = new BinomialGenerator(reps, Isotope.N15.prob(), rng);
    }
    
    public double getSum() {
        if (eng == null) return 0;
        double no14, no15;
        no15 = eng.nextValue();
        no14 = reps - no15;
        return Isotope.N14.mass()*no14 + Isotope.N15.mass()*no15;
    }
}
class AltOxygenGenerator {
    BinomialGenerator eng;
    BinomialGenerator eng2;
    int reps;
    MersenneTwisterRNG mersenne;
    
    public AltOxygenGenerator (MersenneTwisterRNG rng, int r) {
        reps = r;
        if (reps == 0) eng = null;
        else eng = new BinomialGenerator(reps, (Isotope.O17.prob()+Isotope.O18.prob()), rng);
        mersenne = rng;
    }
    
    public double getSum() {
        if (eng == null) return 0;
        int noIsotopes = eng.nextValue();
        if (noIsotopes == 0) return reps*Isotope.O16.mass();
        int no16, no17=0, no18=0;
        no16 = reps - noIsotopes;
        eng2 = new BinomialGenerator(noIsotopes, Isotope.O17.prob()/(Isotope.O17.prob()+Isotope.O18.prob()), mersenne);
        no17 = eng2.nextValue();
        no18 = reps - (no16+no17);
//        for (int i = 0; i < noIsotopes; i++) {
//            double outcome = mersenne.nextDouble();
//            if (outcome < (Isotope.O17.prob()/(Isotope.O17.prob()+Isotope.O18.prob()))) no17++;
//            else no18++;
//        }
        return no16*Isotope.O16.mass() + no17*Isotope.O17.mass() + no18*Isotope.O18.mass();
    }
}


class OxygenGenerator {
    
    BinomialGenerator eng;
    int reps;
    MersenneTwisterRNG mersenne;
        
    public OxygenGenerator (MersenneTwisterRNG rng, int r) {
        reps = r;
        if (reps == 0) eng = null;
        else eng = new BinomialGenerator(reps, (Isotope.O17.prob()+Isotope.O18.prob()), rng);
        mersenne = rng;
    }
    
    public double getSum() {
        if (eng == null) return 0;
        int noIsotopes = eng.nextValue();
        int no16, no17=0, no18=0;
        no16 = reps - noIsotopes;
        for (int i = 0; i < noIsotopes; i++) {
            double outcome = mersenne.nextDouble();
            if (outcome < (Isotope.O17.prob()/(Isotope.O17.prob()+Isotope.O18.prob()))) no17++;
            else no18++;
        }
        return no16*Isotope.O16.mass() + no17*Isotope.O17.mass() + no18*Isotope.O18.mass();
    }
}

class SulfurGenerator {
    
    BinomialGenerator eng;
    int reps;
    MersenneTwisterRNG mersenne;
        
    public SulfurGenerator (MersenneTwisterRNG rng, int r) {
        reps = r;
        if (reps == 0) eng = null;
        else eng = new BinomialGenerator(reps, (Isotope.S33.prob()+Isotope.S34.prob()+Isotope.S36.prob()), rng);
        mersenne = rng;
    }
    
    public double getSum() {
        if (eng == null) return 0;
        int noIsotopes = eng.nextValue();
        int no32, no33=0, no34=0, no36=0;
        no32 = reps - noIsotopes;
        for (int i = 0; i < noIsotopes; i++) {
            double outcome = mersenne.nextDouble();
            if (outcome < (Isotope.S33.prob()/(Isotope.S33.prob()+Isotope.S34.prob()+Isotope.S36.prob()))) no33++;
            else if (outcome < (Isotope.S33.prob()/(Isotope.S33.prob()+Isotope.S34.prob()+Isotope.S36.prob())
                    + Isotope.S34.prob()/(Isotope.S33.prob()+Isotope.S34.prob()+Isotope.S36.prob()))) no34++;
            else no36++;
        }
        return no32*Isotope.S32.mass() + 
                no33*Isotope.S33.mass() + 
                no34*Isotope.S34.mass() + 
                no36*Isotope.S36.mass();
    }
}

 enum Isotope {
        H1 (1.0078246, 0.99985),
        H2 (2.0141021, 0.00015),
        C12 (12.0000000, 0.988930),
        C13 (13.0033554, 0.011070), 
        N14 (14.0030732, 0.996337),
        N15 (15.0001088, 0.003663), 
        O16 (15.9949141, 0.997590), 
        O17 (16.9991322, 0.000374), 
        O18 (17.9991616, 0.002036),
        S32 (31.972070, 0.9502),
        S33 (32.971456, 0.0075),
        S34 (33.967866,  0.0421),
        S36 (35.967080,  0.0002);
        
        private final double mass;   
        private final double prob; 
        
        Isotope (double mass, double prob) {
            this.mass = mass;
            this.prob = prob;
        }
        
        double mass() { return mass; }
        double prob() { return prob; }
    }
