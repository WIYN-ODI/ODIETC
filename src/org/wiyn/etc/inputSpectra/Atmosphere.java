package org.wiyn.etc.inputSpectra;

import org.wiyn.etc.odi.ODI;

import za.ac.salt.pipt.common.Filter;
import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.Grid;
import za.ac.salt.pipt.common.SpectrumOperator;

/**
 * Implements an atmosphere. The extinction curve gives the extinction in
 * magnitudes per airmass by the earth's atmosphere.
 */
public class Atmosphere extends Filter implements SpectrumOperator {

    /** Creates an atmospheric extinction curve for Sutherland, South Africa. */
    public Atmosphere() {
	// super(xdata, ydata, xdata.length);
	super (ODI.loadResourceFilter ("/resources/rss_pipt-data",
		"kpnoextinct.dat"));
    }

    /**
     * Applies the extinction at the zenith to the given spectrum.
     * 
     * @param spectrum
     *            the spectrum passing through the atmosphere
     */
    public void apply (GenericSpectrum spectrum) {
	this.apply (spectrum, 1);
    }

    /**
     * Applies the extinction for the given airmass to the given spectrum. The
     * atmosphere and the given spectrum must have been sampled at the same
     * points.
     * 
     * @param spectrum
     *            the spectrum passing through the atmosphere
     * @param airmass
     *            the airmass towards the target
     */
    public void apply (GenericSpectrum spectrum, double airmass) {
	// apply the extinction curve point by point
	for (int i = 0; i < spectrum.n (); i++) {
	    // double x = s.x(i);
	    // double m = this.interp(x);
	    double m = this.y[i];
	    double e = Math.pow (10, -0.4 * m * airmass);
	    spectrum.y[i] *= e;
	}
    }

    /**
     * Applies the extinction for a wavelength-dependent airmass to the given
     * spectrum. The atmosphere, the airmass and the given spectrum must have
     * been sampled at the same points.
     * 
     * @param spectrum
     *            the spectrum passing through the atmosphere
     * @param airmasses
     *            the grid representing the airmass as a function of wavelength
     */
    public void apply (GenericSpectrum spectrum, Grid airmasses) {
	// apply the extinction curve point by point
	for (int i = 0; i < spectrum.n (); i++) {
	    // double x = s.x(i); // the current wavelength
	    // double m = this.interp(x);
	    double m = this.y[i];
	    // double airmass = g.interp(x);
	    double airmass = airmasses.y[i];
	    double e = Math.pow (10, -0.4 * m * airmass);
	    spectrum.y[i] *= e;
	}
    }

    /**
     * Removes the atmospheric extinction from a spectrum for the given airmass.
     * 
     * @param spectrum
     *            the spectrum passing through the atmosphere
     * @param airmass
     *            The airmass towards the target
     */
    public void remove (GenericSpectrum spectrum, double airmass) {
	// apply the extinction curve point by point
	for (int i = 0; i < spectrum.n (); i++) {
	    // double x = s.x(i);
	    // double m = this.interp(x);
	    double m = this.y[i];
	    double e = Math.pow (10, -0.4 * m * airmass);
	    spectrum.y[i] /= e;
	}
    }

    /**
     * Removes the atmospheric extinction from a spectrum for a
     * wavelength-dependent airmass.
     * 
     * @param spectrum
     *            the spectrum passing through the atmosphere
     * @param airmasses
     *            the grid representing the airmass as a function of wavelength
     */
    public void remove (GenericSpectrum spectrum, Grid airmasses) {
	// apply the extinction curve point by point
	for (int i = 0; i < spectrum.n (); i++) {
	    // double x = spectrum.x(i); // the current wavelength
	    // double m = this.interp(x);
	    double m = this.y[i];
	    // double airmass = g.interp(x);
	    double airmass = airmasses.y[i];
	    double e = Math.pow (10, -0.4 * m * airmass);
	    spectrum.y[i] /= e;
	}
    }
}
