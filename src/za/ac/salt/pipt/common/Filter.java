package za.ac.salt.pipt.common;

import java.net.URL;

/** This class describes a filter. */
public class Filter extends Grid implements SpectrumOperator {
    /**
     * Creates a filter without setting any throughput values. If you use this
     * constructor, ensure that the throughputs will be sert later.
     */
    public Filter() {
	super ();
    }

    /**
     * Creates a filter with the given wavelength-independent throughput.
     * 
     * @param throughput
     *            the (wavelength-independent) throughput
     */
    public Filter(double throughput) {
	super (throughput);
    }

    /**
     * Creates a filter the throughput of which is described by the given array
     * of wavelength values and the array of corresponding throughput values.
     * 
     * @param wavelengths
     *            the wavelengths (in A)
     * @param throughputs
     *            the corresponding throughput values
     * @param n
     *            the number of data points
     */
    public Filter(double wavelengths[], double throughputs[], int n) {
	super (wavelengths, throughputs, n);
    }

    /**
     * Creates a filter the throughput of which is given in the file to which
     * the given URL points.
     * 
     * @param url
     *            the URL of the file containing the wavelengths and
     *            corresponding throughput values
     */
    public Filter(URL url) {
	super (url);
    }

    /**
     * Creates a filter the throughput of which is given by the specified grid
     * of wavelengths and corresponding throughput values.
     * 
     * @param grid
     *            the grid of wavelengths and corresponding throughput values
     */
    public Filter(Grid grid) {
	super (grid);
    }

    /**
     * Create a box filter which is 0 everywhere, and 1 if cutOn < wavelength <
     * cutOff
     * 
     * @param cutOn
     *            blue edge of the filter
     * @param cutOff
     *            red edge of the filter
     */
    public Filter(double cutOn, double cutOff) {

	super (0);

	for (int i = 0; i < this.n (); i++) {
	    if (cutOn < this.x (i) && this.x (i) <= cutOff) {
		this.y[i] = 1.0;
	    }
	}
	return;
    }

    /**
     * Applys the filter to the given spectrum, i.e. scales the grid ordinate
     * values by the throughput values.
     * 
     * @param spectrum
     *            the spectrum passed through the filter
     */
    public void apply (GenericSpectrum spectrum) {
	spectrum.scale (this);
    }

    /**
     * Returns the string "filter".
     * 
     * @return the string "filter"
     */
    public String name () {
	return "filter";
    }
}
