package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.Filter;
import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.GridSpectrum;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class provides a static method for obtaining the normalization factor
 * which ensures that the normalized flux matches a V magnitude at the
 * respective reference wavelength.
 */

public class FluxNormalization {
    /** the V band reference wavelength (in Angstroms) */
    public static final double V_BAND_REFERENCE_WAVELENGTH = 5500;

    public static double getNormalisationFactor (
	    FluxNormalizationDescriptor myMagSystem,
	    NormalizedSpectrum theSpectrum) {

	if (myMagSystem == null) {
	    throw new InvalidValueException (
		    "No normalization system is defined. This is a problem in the ETC, class "

		    + " Please report.");

	}

	// Vega V magnitude calibration is relative to the flux of Vega

	if (myMagSystem.magnitudeSystem == FluxNormalizationDescriptor.MAGSYS_VEGA) {

	    return FluxNormalization.VegaVnormalizationFactor (theSpectrum);
	}

	// AB magnitude system is a calibration from physical units.

	if (myMagSystem.magnitudeSystem == FluxNormalizationDescriptor.MAGSYS_AB) {

	    return FluxNormalization.ABnormalizationFactor (theSpectrum,
		    myMagSystem.centralWavelength);
	}

	return 0;
    }

    /**
     * Returns the normalization factor for the given spectrum and V magnitude.
     * 
     * @param spectrum
     *            the spectrum to be normalized
     * @return the normalization factor
     */
    public static double VegaVnormalizationFactor (NormalizedSpectrum spectrum) {
	// Get the flux corresponding to the given V magnitude.
	double vFlux = Math
		.pow (10.0, (-0.4 * spectrum.getMagnitude ()) - 8.43);

	// Get the non-normalized flux at the V band magnitude wavelength.
	double nonNormalizedFluxAtV = spectrum
		.nonNormalizedFlux (V_BAND_REFERENCE_WAVELENGTH);

	// If the flux for the given V magnitude vanishes, we obviously have a
	// problem, as normalizing isn't possible.
	if (nonNormalizedFluxAtV == 0) {
	    throw new InvalidValueException (
		    "No normalization is possible, as the given flux vanishes at the V band wavelength ("
			    + V_BAND_REFERENCE_WAVELENGTH + " A).");
	}

	// Return the ratio of this flux and the non-normalized flux at the V
	// band reference wavelength.
	return vFlux / nonNormalizedFluxAtV;
    }

    /**
     * Normalizes a Spectrum in units ergs/cm^2/s/A to a certain AB magnutide at
     * a reference wavelength.
     * 
     * Algorithms used: m(AB) = -2.5*log Fv - 48.57 note that the flux is in
     * units ergs/cm^2/s/Hz. This means, we have to transmutate from /Hz to /A:
     * 
     * Fl * dl/dv = Fv
     * 
     * dl/dv = d (c/v) / dv = (-) c/v^2 = (-) l^2/c
     * 
     * 
     * @param spectrum
     * @param cw
     * @return
     */
    public static double ABnormalizationFactor (NormalizedSpectrum spectrum,
	    double cw) {

	double abFlux = ABReference.getReferenceFluxAt (cw);

	double nonNormalizedFlux = spectrum.nonNormalizedFlux (cw);
	System.err
		.println ("Flux from single wavelength: " + nonNormalizedFlux);

	nonNormalizedFlux = getAverageFlux ((GenericSpectrum) spectrum, cw, 100);
	System.err.println ("Flux from averaged area: " + nonNormalizedFlux);

	double magnitudeFactor = Math.pow (10, -0.4 * spectrum.getMagnitude ());

	if (nonNormalizedFlux == 0) {
	    throw new InvalidValueException (
		    "No normalization is possible, as the given flux vanishes at the reference band wavelength ("
			    + cw + " A).");
	}
	return abFlux * magnitudeFactor / nonNormalizedFlux;
    }

    public static double getAverageFlux (GenericSpectrum input, double cw,
	    double width) {

	double retVal = 0;

	Filter f = new Filter (cw - width / 2, cw + width / 2);

	retVal = Integrate (input, f) / width;

	return retVal;
    }

    public static double Integrate (GenericSpectrum spectrum, Filter f) {
	double retVal = 0;
	// System.err.println (spectrum.n() + "  " + f.n ());
	if (spectrum instanceof NormalizedSpectrum) {
	    System.err.println ("Normalized");
	    for (int ii = 0; ii < f.n (); ii++)

		retVal += f.y[ii]
			* ((NormalizedSpectrum) spectrum).nonNormalizedFlux (f
				.x (ii));

	} else {
	    for (int ii = 0; ii < f.n (); ii++)

		retVal += f.y[ii] * spectrum.flux (f.x (ii));

	}
	return retVal * spectrum.dx ();
    }

    public static void main (String[] args) {
	GenericSpectrum ab = new NormalizedKuruczModel (3500, 0, -1, 20);
	// GenericSpectrum ab = new ABReference ();
	double cw = 6000;

	double nonNormalizedFlux =  ( (NormalizedSpectrum) ab).nonNormalizedFlux (cw);
	System.err
		.println ("Flux from single wavelength: " + nonNormalizedFlux);
	System.err.println ("Flux from averaged area: "
		+ FluxNormalization.getAverageFlux (ab, 6000, 100));

    }
}
