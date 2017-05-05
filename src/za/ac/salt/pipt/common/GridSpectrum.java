package za.ac.salt.pipt.common;

import java.net.URL;


/** This class provides an implementation of the abstract GenericSpectrum methods for a Grid based spectrum. */
public class GridSpectrum extends GenericSpectrum
{
    /** Creates a default grid based spectrum. */
    public GridSpectrum()
    {
	super();
    }


    /** Creates the spectrum from the given wavelengths and ordinate data.
     * @param wavelengths the wavelengths
     * @param ordinateData the corresponding ordinate data
     * @param n the number of data points */
    public GridSpectrum(double[] wavelengths, double[] ordinateData, int n)
    {
	super(wavelengths, ordinateData, n);
    }


    /** Creates the spectrum from the file to which the given URL points.
     * @param url the URL of the file containing the spectrum data */
    public GridSpectrum(URL url)
    {
	super(url);
    }


    /** Creates a spectrum with the given constant value. The default values for the abscissa starting value, resolution and range are used.
     * @param value the (constant) value of the spectrum */
    public GridSpectrum(double value)
    {
	super(value);
    }


    /** Clones the given spectrum.
     * @param spectrum */
    public GridSpectrum(GenericSpectrum spectrum)
    {
	
	super(spectrum);
    }


    /** Returns the flux (or whatever other spectral quantity is considered) at the given wavelength.
     * @param wavelength the wavelength (in A)
     * @return the flux */
    public double flux(double wavelength)
    {
	return interp(wavelength);
    }


    /** Returns the string "grid based spectrum".
     * @return the string "grid-based spectrum" */
    public String name()
    {
	return "grid based spectrum";
    }


    /** Updates the spectrum. Must be overridden by the extending class. */
    public void update()
    {
	// do nothing; override in subclass
    }
}
