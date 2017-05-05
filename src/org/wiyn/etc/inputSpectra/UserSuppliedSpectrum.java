package org.wiyn.etc.inputSpectra;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class describes the user-supplied spectrum given by some file. The file
 * data must be of the form<br />
 * <br />
 * <em>wavelength1 flux1<br />
wavelength2 flux2<br />
wavelength3 flux3<br />
...<br />
wavelengthN fluxN</em><br />
 * <br />
 * where the wavelengths must apear in ascending order. WARNING: The units of
 * the flux are fairly arbitrary. You should use this class only if it is
 * ensured that the flux values will be properly normalized!
 */
public class UserSuppliedSpectrum extends GenericSpectrum {
    /** the URL of the user-supplied data for the spectrum */
    protected URL url;

    /**
     * Sets the internal URL variable and states that an update is required. No
     * spectrum is crerated yet, as the necessary array is so large that it must
     * be used temporarily only and hence won't be created before it is really
     * needed.
     * 
     * @param url
     *            the URL of the data for the spectrum
     */
    public UserSuppliedSpectrum(URL url) {
	// Set the internal URL variable to the given value.
	this.url = url;

	// Free the memory.
	freeMemory ();

	// State that the spectrum must be updated.
	setUpdateNeeded (true);
    }

    /**
     * Updates the spectrum (and record the fact that we have done this). This
     * means that the grid for the current URL is obtained.
     * 
     * @throws InvalidValueException
     *             if no file or URL has been provided or if an IOException is
     *             raised when trying to read from the input stream
     */
    public void update () {
	// Read in the data from the URL.
	try {
	    // Check that a URL has been provided.
	    if (url == null) {
		throw new InvalidValueException (
			"No file or URL has been given for the user-supplied spectrum.");
	    }

	    // Open the resource.
	    InputStream inputStream = url.openStream ();
	    reset (DEFAULT_LB_STARTING_VALUE, DEFAULT_LB_RESOLUTION,
		    DEFAULT_LB_RANGE);
	    read (inputStream);
	} catch (IOException ioe) {
	    throw new InvalidValueException ("The URL " + url
		    + " couldn't be read.");
	}

	// Record the fact that we have updated successfully.
	setUpdateNeeded (false);
    }

    /**
     * Returns the flux (in arbitrary units) at the given wavelength. If
     * necessary, we first update the spectrum.
     * 
     * @param wavelength
     *            the wavelength (in Angstrom)
     * @return the flux at the given wavelength (in arbitrary units)
     */
    public double flux (double wavelength) {
	if (isUpdateNeeded ()) {
	    update ();
	}
	return interp (wavelength);
    }

    /**
     * Sets the URL of the user-supplied data for the spectrum and record the
     * fact that the spectrum must be updated before a flux is computed.
     * 
     * @param url
     *            the URL of the user-supplied data
     */
    public void setURL (URL url) {
	URL oldURL = getURL ();
	URL newURL = url;
	this.url = url;
	setUpdateNeeded (true);
	if (oldURL == null || newURL == null
		|| !oldURL.toString ().equals (newURL.toString ())) {
	    firePropertyChange ("url", oldURL, newURL);
	}
    }

    /**
     * Calls the setURL() method. This method is included for compatibility
     * only.
     * 
     * @param url
     *            the URL of the user-supplied data
     */
    public void safeSetURL (URL url) {
	setURL (url);
    }

    /**
     * Returns the URL of the user-supplied data for the spectrum.
     * 
     * @return the URL of the user-supplied data
     */
    public URL getURL () {
	return url;
    }

    /**
     * Returns the name of this spectrum, which is taken to be
     * "User-Supplied Spectrum".
     * 
     * @return the string "User-Supplied Spectrum"
     */
    public String name () {
	return "User-Supplied Spectrum";
    }
}
