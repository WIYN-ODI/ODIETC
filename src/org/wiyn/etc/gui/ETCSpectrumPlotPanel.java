package org.wiyn.etc.gui;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import za.ac.salt.pipt.common.Grid;
import za.ac.salt.pipt.common.Phase;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataElement;

/**
 * This class provides a panel containing a plot for a spectrum. By default, the
 * abscissa values are assumed to be wavelengths given in Angstrom, but no
 * assumption is made concerning the ordinate.
 */
public class ETCSpectrumPlotPanel extends JPanel implements PiptData {
	/**
     * 
     */
	private static final long serialVersionUID = 2671901241896377352L;

	/** a label for wavelengths */
	public static final String WAVELENGTH_LABEL = "Wavelength (A)";

	/** a label for non-diffuse source fluxes */
	public static final String NON_DIFFUSE_FLUX_LABEL = "10^15 * Flux [ergs/sec/Ang(/arcsec^2)]";

	/**
	 * The factor by which the ordinate values are scaled. JFreeChart has issues
	 * when dealing with very small double numbers!
	 */
	private double yScalingFactor = 1e15;

	public final static int OBJECTSPECTRUM = 0;
	public final static int SKYSPECTRUM = 1;
	public final static int THROUGHPUT = 2;

	/** the minimum abscissa value in the plot */
	double xminPlot;

	/** the maximum abscissa value in the plot */
	double xmaxPlot;

	/** the minimum ordinate value in the plot */
	double yminPlot;

	/** the maximum ordinate value in the plot */
	double ymaxPlot;

	JFreeChart myChart;

	/**
	 * Creates the panel with the plot for the given spectrum and value ranges.
	 * If the xAutoscale and yAutoscale parameters are true, zero fluxes at the
	 * interval edges are ignored for the abscissa and the ordinate range of the
	 * binned ordinate values is used. In case null is specified for a range, it
	 * is obtained from the given spectrum.
	 * 
	 * @param spectrum
	 *            the spectrum to be plotted
	 * @param title
	 *            the plot title
	 * @param yLabel
	 *            the ordinate label
	 * @param xRange
	 *            the abscissa range (may be null)
	 * @param yRange
	 *            the ordinate range (may be null)
	 * @param xAutoscale
	 *            states whether to ignore zero fluxes at the abscissa interval
	 *            edges
	 * @param yAutoscale
	 *            states whether to use the value range of the binned (rather
	 *            than original) ordinate values
	 */

	public ETCSpectrumPlotPanel (Grid spectrum, String title, String yLabel,
			double[] xRange, double[] yRange, boolean xAutoscale,
			boolean yAutoscale) {
		// Set the range boundaries for the two axes.

		xminPlot = 3200;
		xmaxPlot = 10000;

		if (yRange != null) {
			yminPlot = yRange[0];
			ymaxPlot = yRange[1];

		} else if (spectrum != null) {
			yminPlot = spectrum.ymin ();
			ymaxPlot = spectrum.ymax ();
		}

		// Create the chart.
		myChart = ChartFactory.createXYLineChart (title, WAVELENGTH_LABEL,
				ETCSpectrumPlotPanel.NON_DIFFUSE_FLUX_LABEL, null,
				PlotOrientation.VERTICAL, false, false, false);

		XYPlot plot = (XYPlot) myChart.getPlot ();
		configThroughput (plot);

		// Create the plot panel and return it.
		ChartPanel chartPanel = new ChartPanel (myChart);
		add (chartPanel);
	}

	private void configThroughput (XYPlot plot) {
		NumberAxis TPAxis = new NumberAxis ("Throughput");
		TPAxis.setRange (0, 1.1);
		TPAxis.setLabelAngle (180 * 2 * Math.PI / 360);
		TPAxis.setLabelPaint (Color.GRAY);
		TPAxis.setLowerBound (0);
		TPAxis.setUpperBound (1.1);
		TPAxis.setAutoRange (false);
		plot.setRangeAxis (ETCSpectrumPlotPanel.THROUGHPUT, TPAxis);
		plot.setRangeAxisLocation (ETCSpectrumPlotPanel.THROUGHPUT,
				AxisLocation.BOTTOM_OR_RIGHT);
		plot.mapDatasetToRangeAxis (ETCSpectrumPlotPanel.THROUGHPUT,
				ETCSpectrumPlotPanel.THROUGHPUT);

		XYLineAndShapeRenderer TPRenderer = new XYLineAndShapeRenderer(true, false);
		TPRenderer.setPaint (Color.GRAY);
		plot.setRenderer (ETCSpectrumPlotPanel.THROUGHPUT, TPRenderer);

		
		TPRenderer = new XYLineAndShapeRenderer(true, false);
		TPRenderer.setPaint (Color.RED);
				plot.setRenderer (ETCSpectrumPlotPanel.OBJECTSPECTRUM, TPRenderer);
		
		TPRenderer = new XYLineAndShapeRenderer(true, false);
		TPRenderer.setPaint (Color.YELLOW);
		plot.setRenderer (ETCSpectrumPlotPanel.SKYSPECTRUM, TPRenderer);
		
	}

	protected void updateThroughput (Grid spectrum) {
		XYPlot plot = (XYPlot) myChart.getPlot ();
		XYSeries xySeriesThroughput = null;
		if (spectrum != null) {
			xySeriesThroughput = getXYSeries (spectrum, false, false);
			plot.setDataset (ETCSpectrumPlotPanel.THROUGHPUT,
					new XYSeriesCollection (xySeriesThroughput));
		}

	}

	protected void updateSpectrum ( Grid objectSpectrum, Grid skySpectrum,
			double[] yrange) {

		// We need to rescale the plots. However, throughput shall not be
		// rescaled.
		double maxY = 0;

		// Create the series collection.
		XYPlot plot = (XYPlot) myChart.getPlot ();

		XYSeries xySeriesObject = null;
		if (objectSpectrum != null) {
			xySeriesObject = getXYSeries (objectSpectrum, false, true);
			xySeriesObject.setDescription ("Object");
			maxY = xySeriesObject.getMaxY ();

		}

		XYSeries xySeriesSky = null;
		if (skySpectrum != null) {
			xySeriesSky = getXYSeries (skySpectrum, false, true);
			xySeriesSky.setDescription ("Sky");
			maxY = Math.max (maxY, xySeriesSky.getMaxY ());
		}

		// XYSeriesCollection xySeriesCollection = new XYSeriesCollection (
		// xySeries);

		plot.setDataset (ETCSpectrumPlotPanel.OBJECTSPECTRUM,
				new XYSeriesCollection (xySeriesObject));

		plot.setDataset (ETCSpectrumPlotPanel.SKYSPECTRUM,
				new XYSeriesCollection (xySeriesSky));

		Range scaledYRange = null;

		if (yrange != null) {

			// this.ymaxPlot = yrange[1] * this.yScalingFactor * 1.2;
			// this.ymaxPlot = (Math.ceil (ymaxPlot * 10) / 10.);
			// this.yminPlot = yrange[0] * this.yScalingFactor;
			// // System.err.println ("Range axis: " + yrange[1] + "  " +
			// // ymaxPlot);
			yminPlot = yrange[0] * this.yScalingFactor;
			ymaxPlot = yrange[1] * this.yScalingFactor;

		} else {
			yminPlot = 0;
			ymaxPlot = Math.ceil (maxY * 10 * 1.2) / 10;
		}
		scaledYRange = new Range (yminPlot, ymaxPlot);
		plot.getRangeAxis ().setRange (scaledYRange);
	}

	/**
	 * Returns the dataset for the given spectrum. The scaling factor for the
	 * ordinate values is set by this method as well.
	 * 
	 * @param spectrum
	 *            the spectrum param xautoscale states whether a zero flux at
	 *            the edges of the abscissa should be ignored
	 * @param yautoscale
	 *            states whether the ordinate boundaries of the binned (rather
	 *            than the original) spectrum should be used for establishing
	 *            the ordinate dscale (which should normally be better option)
	 * @return the dataset
	 */

	private XYSeries getXYSeries (Grid spectrum, boolean xautoscale,
			boolean yautoscale) {
		double xminLocal = xminPlot;
		double xmaxLocal = xmaxPlot;
		// double yminLocal = yminPlot;
		// double ymaxLocal = ymaxPlot;

		double xmin = xminLocal;
		double xmax = xmaxLocal;
		// never plot outside the max range
		xmin = Math.max (xmin, 3200);
		xmax = Math.min (xmax, 10100);
		double xrange = xmax - xmin;
		double xstep = spectrum.dx ();

		/**
		 * Now here's the deal. We don't want to scale and plot every data
		 * point, because we may have 1e5 of them, and only 1e3 pixels on the
		 * display. This requires down-sampling. On the other hand, the scale
		 * may be set on hyper-zoom, in which case we want to plot each and
		 * every point. So come up with a binning factor, which may be unity,
		 * and resample the data into temporary arrays.
		 */

		// get the starting pixel
		int i0 = (int) ((xmin - spectrum.x0 ()) / xstep);
		i0 = Math.max (i0, 0);

		// get the number of points to be plotted
		int npix = (int) (xrange / xstep) + 1;
		npix = Math.min (npix, spectrum.n () - i0); // don't run off the end

		// we want to do successive pairwise combining, stopping before we get
		// below 1024
		int kpix = npix; // assume all the points
		int kbin = 1; // assume no binning
		while (kpix > 1024) {
			kpix /= 2;
			kbin *= 2;
		}

		// allocate a new data array
		double kdata[] = new double[kpix];

		// sample the data into the tmp array
		npix -= npix % kpix;
		for (int i = 0; i < npix; i++) {
			kdata[i / kbin] += spectrum.y[i + i0];

		}

		// preserve the vertical scale
		for (int k = 0; k < kpix; k++) {
			kdata[k] /= (double) kbin;
			if (yautoscale)
				kdata[k] *= this.yScalingFactor;
		}
		xstep *= kbin;

		// Create the series of plot points.
		XYSeries xySeries = new XYSeries ("Spectrum");
		double xDataset = spectrum.x (i0);
		for (int k = 0; k < kpix; k++) {
			xySeries.add (xDataset, kdata[k]);
			// xySeries.add (xDataset, spectrum.interp (xDataset)*
			// yScalingFactor);
			xDataset += xstep;
		}
		xySeries.add (xDataset, kdata[kpix - 1]);
		return xySeries;
	}

	public double getYmaxPlot () {
		return ymaxPlot;
	}

	public void setLabelText (String label) {
		myChart.setTitle (label);

	}

	public void safeSetLabelText (String label) {
		setLabelText (label);
	}

	public String getLabelText () {
		return myChart.getTitle ().getText ();

	}

	public void setYMax (double label) {

	}

	public void safeSetYMax (double label) {
		Range YRange = new Range (this.yminPlot, label);
		XYPlot plot = (XYPlot) myChart.getPlot ();

		plot.getRangeAxis ().setRange (YRange);
	}

	public double getYMax () {
		return 0.0;
	}

	public void safeSetYLabel (String YLabel) {

		XYPlot plot = (XYPlot) myChart.getPlot ();

		plot.getRangeAxis ().setLabel (YLabel);
	}

	public ArrayList<PiptDataElement> getChildren () {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRequired (String childElement, Phase phase) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubTreeComplete (Phase phase) {
		// TODO Auto-generated method stub
		return false;
	}

}
