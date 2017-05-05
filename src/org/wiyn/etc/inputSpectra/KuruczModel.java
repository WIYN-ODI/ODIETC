package org.wiyn.etc.inputSpectra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.GridResource;
import za.ac.salt.pipt.common.GridSpectrum;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class describes a Kurucz atmosphere model. WARNING: The units of the
 * flux are fairly arbitrary. You should use this class only if it is ensured
 * that the flux values will be properly normalized!
 */
public class KuruczModel extends GenericSpectrum {
    /** the temperature (in Kelvin) */
    protected double temperature = Double.NaN;

    /** the logarithm of the gravity */
    protected double logGravity = Double.NaN;

    /** the logarithm of the metallicity */
    protected double logMetallicity = Double.NaN;

    /** the grid for the Kurucz model */
    private GridSpectrum modelGrid;

    /** the Kurucz file number */
    private int fileno = 10;

    /** a datafile required for computing the Kurucz model */
    private final static String website = "http://www.wiyn.org/";

    /** a datafile required for computing the Kurucz model */
    private final static String summary = "/resources/rss_pipt-data/kurucz.summary.txt";

    /** a datafile required for computing the Kurucz model */
    private final static String kp00file = "/resources/rss_pipt-data/kurucz_kp00.txt.gz";

    /** a datafile required for computing the Kurucz model */
    private final static String km10file = "/resources/rss_pipt-data/kurucz_km10.txt.gz";

    /** a datafile required for computing the Kurucz model */
    private final static String km20file = "/resources/rss_pipt-data/kurucz_km20.txt.gz";

    /**
     * Sets the internal variables to the given temperature, gravity and
     * metallicity values and states that an update is needed. The model isn't
     * computed yet, as the required array is so large that it must be used
     * temporarily only and hence shouldn't be obtained before it is really
     * needed.
     * 
     * @param temperature
     *            the temperature (in Kelvin)
     * @param logGravity
     *            the logarithm of the gravity
     * @param logMetallicity
     *            the logarithm of the metallicity
     */
    public KuruczModel(double temperature, double logGravity,
	    double logMetallicity) {
	// Set the internal variables.
	this.temperature = temperature;
	this.logGravity = logGravity;
	this.logMetallicity = logMetallicity;

	// Free the memory.
	freeMemory ();

	// State that an update is required.
	setUpdateNeeded (true);
    }

    /** Frees the memory of al the arrays. */
    public void freeMemory () {
	super.freeMemory ();
	modelGrid = null;
    }

    /**
     * Returns the distance as given by the metric of the Kurucz model space.
     * 
     * @param temperature
     *            the first temperature (in Kelvin)
     * @param logGravity
     *            the logarithm of the first gravity
     * @param logMetallicity
     *            the logarithm of the first metallicity
     * @param temperature0
     *            the second temperature (in Kelvin)
     * @param logGravity0
     *            the logarithm of the second gravity
     * @param logMetallicity0
     *            the logarithm of the second metallicity
     * @return the distance between the two given points in Kurucz model space
     */

    private static double metric (double temperature, double logGravity,
	    double logMetallicity, double temperature0, double logGravity0,
	    double logMetallicity0) {
	// these represent a unit step in each dimension
	double Unit_temperature = 250.0;
	double Unit_logGravity = 0.5;
	double Unit_logMetallicity = 1.0;

	double d = 0.0;
	d += Math.pow (((temperature - temperature0) / Unit_temperature), 2.0);
	d += Math.pow (((logGravity - logGravity0) / Unit_logGravity), 2.0);
	d += Math
		.pow (
			((logMetallicity - logMetallicity0) / Unit_logMetallicity),
			2.0);

	return (d);
    }

    /**
     * Obtains the Kurucz model most closely representing the given temperature,
     * gravity and metallicity.
     */
    public void update () {
	// Record the fact that the model has been updated.
	setUpdateNeeded (false);

	// Create the grid (without the correct values yet). */
	reset (DEFAULT_LB_STARTING_VALUE, DEFAULT_LB_RESOLUTION,
		DEFAULT_LB_RANGE);

	// As we will change the temperature, gravity and metallicity (assigning
	// the best fit values), we need to store the original values.
	double originalTemperature = temperature;
	double originalLogGravity = logGravity;
	double originalLogMetallicity = logMetallicity;

	// examine the reference file
	GridResource gr = new GridResource (website, summary);
	InputStream inputStream = null;
	if (gr != null) {
	    inputStream = gr.getInputStream ();
	}

	if (inputStream != null) {
	    // examine each line
	    InputStreamReader isr = new InputStreamReader (inputStream);
	    BufferedReader br = new BufferedReader (isr);
	    boolean done = false;
	    double best = Double.MAX_VALUE; // best metric so far
	    while (!done) {
		try {
		    String s = br.readLine ();
		    if (s == null) {
			done = true;
		    } else {
			StringTokenizer st = new StringTokenizer (s, " ");
			st.nextToken ();
			int fileno = Integer.parseInt (st.nextToken ());
			st.nextToken ();
			st.nextToken ();
			double bestFitTemperature = Double.parseDouble (st
				.nextToken ());
			st.nextToken ();
			st.nextToken ();
			double bestFitLogGravity = Double.parseDouble (st
				.nextToken ());
			st.nextToken ();
			st.nextToken ();
			int bestFitLogMetallicity = Integer.parseInt (st
				.nextToken ());

			// determine the metric
			double d = KuruczModel.metric (originalTemperature,
				originalLogGravity, originalLogMetallicity,
				bestFitTemperature, bestFitLogGravity,
				bestFitLogMetallicity);
			if (d < best) {
			    temperature = bestFitTemperature;
			    logGravity = bestFitLogGravity;
			    logMetallicity = bestFitLogMetallicity;
			    this.fileno = fileno;
			    best = d;
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace ();
		}

	    }

	    // we've read the summary file, and have the best match.
	    // now open the file and read in the data.
	    gr = null;
	    switch ((int) this.logMetallicity) {
	    case 0:
		gr = new GridResource (website, kp00file);
		break;
	    case -1:
		gr = new GridResource (website, km10file);
		break;
	    case -2:
		gr = new GridResource (website, km20file);
		break;
	    }

	    if (gr != null) {
		try {
		    inputStream = new GZIPInputStream (gr.getInputStream ());
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    System.err.println ("Error reading gzip spectrum file");
		}
	    }
	    if (inputStream != null) {
		isr = new InputStreamReader (inputStream);
		br = new BufferedReader (isr);

		int n = 1221; // values per spectrum
		double x[] = new double[n];
		double y[] = new double[n];

		// which column is the spectrum in?
		int col = (int) (this.logGravity / 0.5); // ouch!

		try {
		    // skip over unwanted spectra in this file
		    for (int i = 0; i < (n + 3) * fileno; i++) {
			br.readLine ();
		    }
		    br.readLine (); // fits file id
		    br.readLine (); // column titles
		    br.readLine (); // dashed lines
		    for (int i = 0; i < n; i++) {
			String s = br.readLine ();
			StringTokenizer st = new StringTokenizer (s, " ");
			x[i] = Double.parseDouble (st.nextToken ());
			// breeze through unwanted columns
			for (int j = 0; j < col; j++) {
			    st.nextToken ();
			}
			y[i] = Double.parseDouble (st.nextToken ());
		    }

		    // Set the model grid.
		    modelGrid = new GridSpectrum ();
		    modelGrid.resample (x, y, n);
		} catch (IOException e) {
		    e.printStackTrace ();
		}
	    }
	}
    }

    /**
     * Returns the flux at the given wavelength for the Kurucz model (in
     * arbitrary units). If necessary, the model is updated first.
     * 
     * @param wavelength
     *            the wavelength (in Angstrom)
     * @return the flux for the Kurucz model (in arbitrary units)
     */
    public double flux (double wavelength) {
	// Check whether we have to update the model.
	if (isUpdateNeeded ()) {
	    update ();
	}

	// Obtain the flux by means of interpolating the model grid data.
	return modelGrid.interp (wavelength);
    }

    /**
     * Sets the temperature to the given value and records the fact that the
     * model needs to be updated before the next flux is computed from it.
     * 
     * @param temperature
     *            the temperature
     */

    public void setTemperature (double temperature) {
	Double oldTemperature = new Double (getTemperature ());
	Double newTemperature = new Double (temperature);
	this.temperature = temperature;
	setUpdateNeeded (true);
	if (oldTemperature.compareTo (newTemperature) != 0) {
	    firePropertyChange ("temperature", oldTemperature, newTemperature);
	}
    }

    /**
     * Checks whether the given temperature value is positive and, if so, assign
     * it to the Kurucz model temperature, using the setTemperature() method.
     * 
     * @param temperature
     *            the temperature
     * @throws InvalidValueException
     *             if the given temperature isn't positive
     */
    public void safeSetTemperature (double temperature) {
	if (temperature <= 0) {
	    throw new InvalidValueException (
		    "The temperature must be non-negative.");
	}
	setTemperature (temperature);
    }

    /**
     * Returns the temperature of the Kurucz model.
     * 
     * @return the temperature of the Kurucz model (in Kelvin)
     */
    public double getTemperature () {
	return temperature;
    }

    /**
     * Sets the logatithm of the gravity to the given value and records the fact
     * that the model needs to be updated before the next flux is computed from
     * it.
     * 
     * @param logGravity
     *            the logarithm of the gravity (in solar units)
     */
    public void setLogGravity (double logGravity) {
	Double oldLogGravity = new Double (getLogGravity ());
	Double newLogGravity = new Double (logGravity);
	this.logGravity = logGravity;
	setUpdateNeeded (true);
	if (oldLogGravity.compareTo (newLogGravity) != 0) {
	    firePropertyChange ("logGravity", oldLogGravity, newLogGravity);
	}
    }

    /**
     * Calls the setLogGravity() method. This method is included for
     * compatibility only.
     * 
     * @param logGravity
     *            the logarithm of the gravity (in solar units)
     */
    public void safeSetLogGravity (double logGravity) {
	setLogGravity (logGravity);
    }

    /**
     * Returns the logarithm of the gravity.
     * 
     * @return the logarithm of the gravity (in solar units)
     */
    public double getLogGravity () {
	return logGravity;
    }

    /**
     * Sets the logarithm of the metallicity to the given value and records the
     * fact that the model needs to be updated before the next flux is computed
     * from it.
     * 
     * @param logMetallicity
     *            the logarithm of the metallicity (in solar units)
     */
    public void setLogMetallicity (double logMetallicity) {
	Double oldLogMetallicity = new Double (getLogMetallicity ());
	Double newLogMetallicity = new Double (logMetallicity);
	this.logMetallicity = logMetallicity;
	setUpdateNeeded (true);
	if (oldLogMetallicity.compareTo (newLogMetallicity) != 0) {
	    firePropertyChange ("logMetallicity", oldLogMetallicity,
		    newLogMetallicity);
	}
    }

    /**
     * Calls the setLogMetallicity() method. This method is included for
     * compatibility only.
     * 
     * @param logMetallicity
     *            the logarithm of the metallicity (in solar units)
     */
    public void safeSetLogMetallicity (double logMetallicity) {
	setLogMetallicity (logMetallicity);
    }

    /**
     * Returns the logarithm of the metallicity.
     * 
     * @return the logarithm of the metallicity (in solar units)
     */
    public double getLogMetallicity () {
	return logMetallicity;
    }

    /**
     * Returns the name of this spectrum, which is taken to be "Kurucz Model".
     * 
     * @return the string "Kurucz Model"
     */
    public String name () {
	return "Kurucz Model";
    }
}