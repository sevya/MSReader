package mass.spec;


import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection; 
import org.jfree.ui.RefineryUtilities;


public class Plotter extends JFrame {
    
    public static void main (String[] args) {
        XYSeriesCollection collect = new XYSeriesCollection();
        collect.addSeries(new Function () {
            @Override
            double getPoint(double x) {
                return .456*(1-Math.exp(-.5675*x));
            }
        }.getXYSeries(0, 10, "a"));
        collect.addSeries(new Function() {
            @Override
            double getPoint (double x) {
                return .7896*(1-Math.exp(-.825*x));
            }
        }.getXYSeries(0, 10, "b"));
        double[][] vals = new double[2][];
        vals[0] = new double[] {0, 1, 2, 3};
        vals[1] = new double[] {4, 3, 5, 1};
        new Plotter(vals).display();
//        new Plotter(collect).display();
    }
    
    public void display () {
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }
    
    public Plotter (XYSeriesCollection collect) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Chart 1",
                "x-axis",
                "y-axis",
                collect,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel); 
    }
    
    //creates a line chart for a given mass spec
    public Plotter (MassSpectrum spectrum) {
        try {
        XYSeries series = new XYSeries("XY Graph");
        for (int i = 0; i<spectrum.msValues[1].length; i++) {
            series.add(spectrum.msValues[0][i], spectrum.msValues[1][i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                spectrum.msTitle,
                "m/z",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
        setContentPane(chartPanel);
        }
        catch (Exception e ) {
            System.out.println("error");
        }
    }
    
    public Plotter(MassSpectrum spectrum, String str) {
        super(str);
        try {
        XYSeries series = new XYSeries("XY Graph");
        for (int i = 0; i<spectrum.msValues[1].length; i++) {
            series.add(spectrum.msValues[0][i], spectrum.msValues[1][i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                spectrum.msTitle,
                "m/z",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        
        ChartPanel chartPanel = new ChartPanel (chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
        setContentPane(chartPanel);
        }
        catch (Exception e ) {
            System.out.println("error");
        }
    }
    
    public Plotter (double[][] data, boolean lines) {
        XYSeries series = FormatChange.ArrayToXYSeries(data);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
                );

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, lines);
        plot.setRenderer(renderer);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);       
    }
    
    public Plotter(double[][] xyValues) {
        super("Default");
        XYSeries series = new XYSeries("XY Graph");
        for (int i= 0; i<xyValues[1].length; i++) {
            series.add(xyValues[0][i], xyValues[1][i]);
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Chart 1",
                "x-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);       
    }
    
    public Plotter(double[] x, double[] y) {
        XYSeries series = new XYSeries("XY Graph");
        for (int i= 0; i < x.length; i++) {
            series.add(x[i], y[i]);
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Chart 1",
                "x-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel); 
    }
    
    
    
    public Plotter(double[][] one, double[][] two) {
        XYSeries ser1 = FormatChange.ArrayToXYSeries(one, "1");
        XYSeries ser2 = FormatChange.ArrayToXYSeries(two, "2");
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(ser1);
        dataset.addSeries(ser2);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Chart 1",
                "x-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel); 
    }
    
    public Plotter(double[][] xyValues, String str) {
        super(str);
        XYSeries series = new XYSeries("XY Graph");
        for (int i= 0; i<xyValues[1].length; i++) {
            series.add(xyValues[0][i], xyValues[1][i]);
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Chart 1",
                "x-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);       
    }
    
    //creates a line or scatter plot for an array of two columns
    //input of 1 gives a scatter plot, any other input gives line chart
    
    public Plotter (XYSeries xy) {
        super("default");
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(xy);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Chart 1",
                "x-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);       
    }
    
    public Plotter(double[][] xyValues, int chartType) {
        super("Default");
        XYSeries series = new XYSeries("XY Graph");
        for (int i= 0; i<xyValues[1].length; i++) {
            series.add(xyValues[0][i], xyValues[1][i]);
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart;

        
        if (chartType == 1) {
        chart = ChartFactory.createScatterPlot(
                "Chart 1",
                "x-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );  
        }
        else {
        chart = ChartFactory.createXYLineChart(
                "Chart 1",
                "x-axis",
                "y-axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
                );
        
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
        setContentPane(chartPanel);       
    }
    
//    creates a line chart for two arrays of two columns each
//    public Plotter(double[][] ser1, double[][] ser2) {
//        super("Default");
//        XYSeries series1 = new XYSeries("XY Graph");
//        XYSeries series2 = new XYSeries("XY Graph 2");
//        for (int i= 0; i<ser1[1].length; i++) {
//            series1.add(ser1[0][i], ser1[1][i]);
//        }
//        for (int i=0; i<ser2[1].length; i++) {
//            series2.add(ser2[0][i], ser2[1][i]);
//        }
//        
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries(series1);
//        dataset.addSeries(series2);
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                "Chart 1",
//                "x-axis",
//                "y-axis",
//                dataset,
//                PlotOrientation.VERTICAL,
//                true,
//                true,
//                false
//                );
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//        setContentPane(chartPanel);    
//    }
    
    

}

abstract class Function {
    public Function() {}
    
    abstract double getPoint (double x);
    
    public XYSeries getXYSeries (double xstart, double xend, String str) {
        XYSeries series = new XYSeries(str);
        for (double i = xstart; i < xend; i+=.1) {
            series.add(i, getPoint(i));
        } return series;
    }
    
    public XYSeries getXYSeries() {
        return getXYSeries (0, 20, "");
    }
}
