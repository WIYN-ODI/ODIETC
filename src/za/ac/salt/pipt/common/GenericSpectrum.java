package za.ac.salt.pipt.common;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataElement;

/**
 * This class describes a generic spectrum. It provides abstract methods for
 * obtaining the flux at some wavelength and for getting the name of the
 * spectrum, and it offers the method for firing property change events and for
 * adding and removing property change listeners. In addition it provides
 * methods for setting the grid describing the spectrum, resampling with area
 * preservation and quantizing.
 */
public abstract class GenericSpectrum extends Grid implements PiptData {
    /** states whether this spectrum is a diffuse one */
    private boolean diffuse = false;

    /** supports handling property changes */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport (
	    this);

    /** states whether an update is needed before a flux can be computed */
    private boolean updateNeeded = true;

    /**
     * Creates the grid describing this spectrum with the default values for the
     * abscissa starting value, resolution and range. The setting of the actual
     * grid values must be carried out elsewhere by means of the setGridValues()
     * method.
     */
    public GenericSpectrum() {
	this (Grid.DEFAULT_LB_STARTING_VALUE, Grid.DEFAULT_LB_RESOLUTION,
		Grid.DEFAULT_LB_RANGE);
    }

    /**
     * Creates the grid describing this spectrum with the given parameters. The
     * setting of the actual grid values must be carried out elsewhere by means
     * of the setGridValues() method.
     * 
     * @param lbStartingValue
     *            the starting value of the abscissa, given as the binary
     *            logarithmic value
     * @param lbResolution
     *            the abscissa interval length per bin, given as the binary
     *            logarithmic value
     * @param lbRange
     *            the overall abscissa range, given as the binary logarithmic
     *            value
     */
    public GenericSpectrum(int lbStartingValue, int lbResolution, int lbRange) {
	super (lbStartingValue, lbResolution, lbRange);
    }

    /**
     * Creates the spectrum by resampling the given data.
     * 
     * @param wavelengths
     *            the wavelengths to resample
     * @param ordinateData
     *            the (ordinate) data to resample
     * @param n
     *            the number of data points
     */
    public GenericSpectrum(double wavelengths[], double ordinateData[], int n) {
	this.resample (wavelengths, ordinateData, n);
    }

    /**
     * Creates the spectrum by using the file located at the given URL.
     * 
     * @param url
     *            the URL where to find the file for creating the spectrum
     */
    public GenericSpectrum(URL url) {
	try {
	    // open the resource
	    InputStream inputStream = url.openStream ();
	    read (inputStream);
	} catch (IOException e) {
	    System.err.println (this.getClass ().getName () + ": " + e);
	}
    }

    /**
     * Creates a spectrum with the given constant value. The default values for
     * the abscissa starting value, resolution and range are used.
     * 
     * @param value
     *            the (constant) value of the spectrum
     */
    public GenericSpectrum(double value) {
	this ();
	for (int i = 0; i < n (); i++) {
	    y[i] = value;
	}
    }

    /**
     * Clones the given spectrum.
     * 
     * @param spectrum
     *            the spectrum to be cloned
     */
    public GenericSpectrum(GenericSpectrum spectrum) {

	super (spectrum);
	diffuse = spectrum.isDiffuse ();
    }

    /**
     * Does an area-preserving resampling of the given wavelengths and
     * (ordinate) data, using the wavelength grid of this spectrum. The
     * resulting array of resampled data is assigned to that of this spectrum.
     * 
     * @param resampledWavelengths
     *            the wavelengths which are resampled
     * @param resampledData
     *            the corresponding resampled (ordinate) data
     * @param nold
     *            the number of data points
     */
    public void resample (double resampledWavelengths[],
	    double resampledData[], int nold) {
	// make temporary destination arrays
	int nnew = this.n (); // for speed
	double[] xnew = new double[nnew];
	double[] ynew = new double[nnew];

	// initialize the new arrays
	for (int i = 0; i < nnew; i++) {
	    xnew[i] = this.x (i);
	    ynew[i] = 0;
	}

	/*
	 * Now, move through the input array. For each of its bins, sequentially
	 * locate each output bin that overlaps. For each overlap, pro-rate the
	 * input energy into the output bin according to the degree of overlap.
	 */

	int j = 0; /* index into the output array */
	double x1_hi; /* high edge of input zone */
	double x1_lo; /* low edge of input zone */
	double x2_hi; /* high edge of output zone */
	double x2_lo; /* low edge of output zone */
	for (int i = 0; i < nold; i++) {
	    // move the input zone to the next overlap
	    x2_lo = x_lo (xnew, j);
	    while ((x1_hi = x_hi (resampledWavelengths, i)) < x2_lo) {
		i++;
		if (i == nold) {
		    // we are done
		    this.y = ynew;
		    // System.out.println(this.getClass().getName() +
		    // ".resample: done: " + this);
		    return;
		}
	    }

	    // get the other input zone edge
	    x1_lo = x_lo (resampledWavelengths, i);
	    // move the output zone to the next overlap
	    while ((x2_hi = x_hi (xnew, j)) < x1_lo) {
		j++;
		if (j == nnew) {
		    /* we are done */
		    this.y = ynew;
		    return;
		}
	    }

	    // increment output zones overlapped by this input zone
	    // (watch the order of evaluation of the j<n factor below!)
	    while ((j < nnew) && ((x2_lo = x_lo (xnew, j)) < x1_hi)) {

		// get the upper of the current output zone
		x2_hi = x_hi (xnew, j);

		// get the overlap low edge
		double o1 = Math.max (x1_lo, x2_lo);
		// get the overlap high edge
		double o2 = Math.min (x1_hi, x2_hi);

		// reassign the energy in the overlap region

		// this is the energy in the overlap region
		double a = resampledData[i] * (o2 - o1);
		// System.out.println(this.getClass().getName() +
		// ".resample: a: " + a);

		// this is the new energy density
		a /= (x2_hi - x2_lo);
		// System.out.println(this.getClass().getName() +
		// ".resample: a: " + a);

		// add it in
		ynew[j] += a;

		// move to the next output zone
		j++;
	    }

	    /*
	     * now back up one output zone, because it may be hit by the next
	     * input zone.
	     */
	    j--;
	}

	// abandon the original data and replace with the new
	this.y = ynew;
    }

    /**
     * Quantizes this spectrum into photons and returns the result
     * 
     * @return the total number of photons (per second) for this spectrum
     */
    public int quantize () {
	double hc = 1.986484121e-8; // h*c with length = angstroms
	double nphotons = 0;
	int n = this.n ();
	for (int i = 0; i < n; i++) {
	    double x = this.x (i);
	    y[i] *= x / hc;
	    nphotons += y[i]; // per sec per angstrom
	}
	nphotons *= this.dx (); // per sec
	return (int) nphotons;
    }

    /**
     * Returns the integral of this spectrum over the whole wavelength range.
     * 
     * @return the integral over the whole wavelength range
     */
    public double integrate () {
	double integral = 0;
	int n = this.n (); // for speed
	for (int i = 0; i < n; i++) {
	    integral += this.y[i];
	}
	integral *= this.dx (); // equal size bins comes out of the integral
	return integral;
    }

    /**
     * Declares the spectrum to diffuse or non-diffuse, depending on the value
     * of the supplied parameter.
     * 
     * @param diffuse
     *            states whether the spectrum is diffuse (true) or non-diffuse
     *            (false)
     */
    public void setDiffuse (boolean diffuse) {
	this.diffuse = diffuse;
    }

    /**
     * States whether this spectrum is diffuse.
     * 
     * @return true if this spectrum is diffuse
     */
    public boolean isDiffuse () {
	return diffuse;
    }

    /**
     * Returns the wavelength of the low edge of the bin with the given index.
     * 
     * @param index
     *            the index of the bin whose low edge is returned
     * @return the wavelength of the low edge of the bin with the given index
     */
    private static double x_lo (double[] x, int index) {
	double v = 0;

	/* watch out for the low edge of the array */
	if (index > 0) {
	    v = (x[index - 1] + x[index]) / 2; // normal
	} else {
	    v = x[0] - (x[1] - x[0]) / 2; // edge
	}

	return v;
    }

    /**
     * Returns the wavelength of the high edge of the bin with the given index.
     * 
     * @param index
     *            the index of the bin whose high edge is returned
     * @return the wavelength of the high edge of the bin with the given index
     */
    private static double x_hi (double[] x, int index) {
	double v = 0;

	// watch out for the high edge of the array
	if (index < x.length - 1) {
	    v = (x[index] + x[index + 1]) / 2; // normal
	} else {
	    v = x[index] + (x[index] - x[index - 1]) / 2; // edge
	}

	return v;
    }

    /**
     * Fires a property change event with the given property name, old value and
     * new value.
     * 
     * @param propertyName
     *            the property name
     * @param oldValue
     *            the old value
     * @param newValue
     *            the new value
     */
    public void firePropertyChange (String propertyName, Object oldValue,
	    Object newValue) {
	propertyChangeSupport.firePropertyChange (propertyName, oldValue,
		newValue);
    }

    /**
     * Adds the given listener to the list of property change listeners.
     * 
     * @param listener
     *            the property change listener to be added
     */
    public void addPropertyChangeListener (PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener (listener);
    }

    /**
     * Removes the given listener to the list of property change listeners.
     * 
     * @param listener
     *            the property change listener to be removed
     */
    public void removePropertyChangeListener (PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener (listener);
    }

    /**
     * Frees the memory required by the spectrum. As this implies that all the
     * information is lost, the spectrum is marked for an update.
     */
    public void freeMemory () {
	super.freeMemory ();
	updateNeeded = true;
    }

    /**
     * States whether the spectrum must be updated before a flux can be
     * computed.
     * 
     * @return true if the spectrum must be updated before a flux can be
     *         computed
     */
    public boolean isUpdateNeeded () {
	return updateNeeded;
    }

    /**
     * Record whether an update is needed before a flux can be computed.
     * 
     * @param updateNeeded
     *            states whether an update is needed before a flux can be
     *            computed
     */
    public void setUpdateNeeded (boolean updateNeeded) {
	this.updateNeeded = updateNeeded;
    }

    /**
     * Returns the flux (or whatever other spectral quantity is considered) at
     * the given wavelength.
     * 
     * @param wavelength
     *            (in Angstrom)
     * @return the flux at the given wavelength
     */
    public abstract double flux (double wavelength);

    /** Updates the spectrum. */
    public abstract void update ();

    /**
     * Returns the name of the spectrum.
     * 
     * @return the name of the spectrum
     */
    public abstract String name ();

    /** Returns an empty list, as there are no children. */
    public ArrayList<PiptDataElement> getChildren () {
	return new ArrayList<PiptDataElement> ();
    }

    /**
     * Returns true.
     * 
     * @param childElement
     *            the name of the child element
     * @param phase
     *            the phase for vwhich the proposal is valid
     * @return true
     */
    public boolean isRequired (String childElement, Phase phase) {
	return true;
    }

    /**
     * Returns true.
     * 
     * @param phase
     *            the phase for vwhich the proposal is valid
     * @return true
     */
    public boolean isSubTreeComplete (Phase phase) {
	return true;
    }

}
