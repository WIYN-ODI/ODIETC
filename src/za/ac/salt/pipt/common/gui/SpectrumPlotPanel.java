package za.ac.salt.pipt.common.gui;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import za.ac.salt.pipt.common.Grid;


/** This class provides a panel containing a plot for a spectrum. By default, the abscissa values are assumed to be wavelengths given in Angstrom, but no assumption is made concerning the ordinate. */
public class SpectrumPlotPanel extends JPanel
{
    /** a label for wavelengths */
    public static final String WAVELENGTH_LABEL = "Wavelength (A)";

    /** a label for non-diffuse source fluxes */
    public static final String NON_DIFFUSE_FLUX_LABEL = "Flux";

    /** a label for diffuse fluxes */
    public static final String DIFFUSE_FLUX_LABEL = "Diffuse Flux";

    /** a label for throughputs */
    public static final String THROUGHPUT_LABEL = "Throughput";

    /** a label for photon numbers */
    public static final String PHOTON_NUMBER_LABEL = "Photons (photons / A)";

    /** a label for signal-to-noise ratios */
    public static final String SNR_LABEL = "SNR";

    /** The factor by which the ordinate values are scaled */
    private double yScalingFactor;

    /** the minimum abscissa value in the plot */
    double xminPlot;

    /** the maximum abscissa value in the plot */
    double xmaxPlot;

    /** the minimum ordinate value in the plot */
    double yminPlot;

    /** the maximum ordinate value in the plot */
    double ymaxPlot;


    /** Creates the panel with the plot for the given spectrum and value ranges. If the xAutoscale and yAutoscale parameters are true, zero fluxes at the interval edges are ignored for the abscissa and the ordinate range of the binned ordinate values is used. In case null is specified for a range, it is obtained from the given spectrum.
     * @param spectrum the spectrum to be plotted
     * @param title the plot title
     * @param yLabel the ordinate label
     * @param xRange the abscissa range (may be null)
     * @param yRange the ordinate range (may be null)
     * @param xAutoscale states whether to ignore zero fluxes at the abscissa interval edges
     * @param yAutoscale states whether to use the value range of the binned (rather than original) ordinate values */
    public SpectrumPlotPanel(Grid spectrum, String title, String yLabel, double[] xRange, double[] yRange, boolean xAutoscale, boolean yAutoscale)
    {
	// Set the range boundaries for the two axes.
	if (xRange != null) {
	    xminPlot = xRange[0];
	    xmaxPlot = xRange[1];
	}
	else {
	    xminPlot = spectrum.xmin();
	    xmaxPlot = spectrum.xmax();
	}
	if (yRange != null) {
	    yminPlot = yRange[0];
	    ymaxPlot = yRange[1];
	}
	else {
	    yminPlot = spectrum.ymin();
	    ymaxPlot = spectrum.ymax();
	}

	// Create the dataset for the plot.
	XYSeries xySeries = getXYSeries(spectrum, xAutoscale, yAutoscale);

	// Add the scaling factor to the ordinate axis label.
	yLabel = (float) yScalingFactor + " * " + yLabel;

	// Create the series collection.
	XYSeriesCollection xySeriesCollection = new XYSeriesCollection(xySeries);

	// Create the chart.
	JFreeChart chart = ChartFactory.createXYLineChart(title, WAVELENGTH_LABEL, yLabel, xySeriesCollection, PlotOrientation.VERTICAL, false, false, false);

	// Set the (scaled) ordinate range of the plot.
	XYPlot plot = (XYPlot) chart.getPlot();
	Range scaledYRange = new Range(yScalingFactor * yminPlot, yScalingFactor * ymaxPlot);
	plot.getRangeAxis().setRange(scaledYRange);

	// Create the plot panel and return it.
	ChartPanel chartPanel = new ChartPanel(chart);
	add(chartPanel);
    }


    /** Returns the dataset for the given spectrum. The scaling factor for the ordinate values is set by this method as well.
     * @param spectrum the spectrum
     * param xautoscale states whether a zero flux at the edges of the abscissa should be ignored
     * @param yautoscale states whether the ordinate boundaries of the binned (eather than the original) spectrum should be used for establishing the ordinate dscale (which should normally be better option)
     * @return the dataset */
    private XYSeries getXYSeries(Grid spectrum, boolean xautoscale, boolean yautoscale)
    {
	double xminLocal = xminPlot;
	double xmaxLocal = xmaxPlot;
	double yminLocal = yminPlot;
	double ymaxLocal = ymaxPlot;

	// find the x-extrema
	double xmin;
	double xmax;
	if (xautoscale) {
	    // exclude zeroes at the edges
	    int imin = 0;
	    for (int i = 0; i < spectrum.n() - 1; i++) {
		if (spectrum.y[i] != 0) {
		    imin = i;
		    break;
		}
	    }
	    int imax = spectrum.n();
	    for (int i = spectrum.n()-1; i >= 0; i--) {
		if (spectrum.y[i] != 0) {
		    imax = i;
		    break;
		}
	    }
	    xmin = spectrum.x(imin);
	    xmax = spectrum.x(imax);
	    // beware a grid of zeroes!
	    if (xmin >= xmax) {
		// revert
		xmin = xminLocal;
		xmax = xmaxLocal;
	    }
	} else {
	    xmin = xminLocal;
	    xmax = xmaxLocal;
	}

	// never plot outside the max range
	xmin = Math.max(xmin, 3200);
	xmax = Math.min(xmax, 9000);
	double xrange = xmax - xmin;
	double xstep = spectrum.dx();
		
	/**
	 * Now here's the deal.
	 * We don't want to scale and plot every data point,
	 * because we may have 1e5 of them,
	 * and only 1e3 pixels on the display.
	 * This requires down-sampling.
	 * On the other hand,
	 * the scale may be set on hyper-zoom,
	 * in which case we want to plot each and every point.
	 * So come up with a binning factor,
	 * which may be unity,
	 * and resample the data into temporary arrays.
	 */
		
	// get the starting pixel
	int i0 = (int)((xmin - spectrum.x0()) / xstep);
	i0 = Math.max(i0, 0);
		
	// get the number of points to be plotted
	int npix = (int)(xrange / xstep) + 1;
	npix = Math.min(npix, spectrum.n() - i0);	// don't run off the end
		
	// we want to do successive pairwise combining, stopping before we get below 1024
	int kpix = npix;	// assume all the points
	int kbin = 1;		// assume no binning
	while (kpix > 1024) {	
	    kpix /= 2;
	    kbin *= 2;
	}
		
	// allocate a new data array
	double kdata[] = new double[kpix];

	// sample the data into the tmp array
	npix -= npix % kpix;
	for (int i = 0; i < npix; i++) {
	    kdata[i/kbin] += spectrum.y[i+i0];
	}

	// preserve the vertical scale
	for (int k = 0; k < kpix; k++) {
	    kdata[k] /= (double)kbin;
	}
	xstep *= kbin;

	// get the y-extrema
	double ymin, ymax;
	if (yautoscale) {
	    // search for the min and max
	    ymin = ymax = kdata[0];
	    for (int k = 1; k < kpix; k++) {
		if (kdata[k] < ymin) {
		    ymin = kdata[k];
		} else if (kdata[k] > ymax) {
		    ymax = kdata[k];
		}
	    }
	} else {
	    ymin = yminLocal;
	    ymax = ymaxLocal;
	}
	double yrange = ymax - ymin;

	// Set the scaling factor for the y axis.
	setOrdinateScalingFactor(ymin, ymax);

	// Create the series of plot points.
	XYSeries xySeries = new XYSeries("spectrum");
	double xDataset = spectrum.x(i0);
	for (int k = 0; k < kpix; k++) {
	    xySeries.add(xDataset, yScalingFactor * kdata[k]);
	    xDataset += xstep;
	}
	xySeries.add(xDataset, yScalingFactor * kdata[kpix - 1]);
	return xySeries;
    }


    /** Sets the scaling factor yScalingFactor for the ordinate value. The factor is chosen such that yScalingFactor * max(|ymin|, |ymax|) <= 1. In case of ymin == ymax == 0, s = 1 is adopted.
     * @param ymin the minimum ordinate value
     * @param ymax the maximum ordinate value */
    private void setOrdinateScalingFactor(double ymin, double ymax)
    {
	double yAbsMax = Math.max(Math.abs(ymin), Math.abs(ymax));
	if (yAbsMax != 0) {
	    yScalingFactor = 1. / Math.pow(10, Math.ceil(Math.log(yAbsMax) / Math.log(10)));
	}
	else {
	    yScalingFactor = 1;
	}
    }
}


	