package org.wiyn.etc.inputSpectra;

import java.net.URL;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class describes a blackbody spectrum which is normalized so that it
 * matches a V magnitude.
 */
public class VNormalizedUserSuppliedSpectrum extends UserSuppliedSpectrum
	implements NormalizedSpectrum {
    /** the V magnitude used for the normalization */
    // private double vMagnitude = Double.NaN;
    /** the temperature used in the previous flux calculation */
 //   private URL previousURL;

    /** the V magnitude used in the previous flux calculation */
//    private double previousVMagnitude = Double.NaN;

    /** the normalization factor */
    private double normalizationFactor = Double.NaN;

    private FluxNormalizationDescriptor myMagSystem;
    private boolean magSystemChanged = true;
    private boolean referencemagChanged = true;
    private double referenceMag = 20;

    /**
     * Sets the URL for the user-supplied data and the V magnitude used for the
     * normalization to the given values.
     * 
     * @param url
     *            the URL of the user-supplied data
     * @param vMagnitude
     *            the V magnitude used for the normalisation
     */
    public VNormalizedUserSuppliedSpectrum(URL url, double vMagnitude) {
	super (url);
	this.referenceMag = vMagnitude;
    }

    /**
     * Returns the non-normalized flux at the given wavelength.
     * 
     * @return the non-normalized flux at the given wavelength
     */
    public double nonNormalizedFlux (double wavelength) {
	return super.flux (wavelength);
    }

    /**
     * Returns the normalized flux at the given wavelength. The normalization is
     * chosen so that the flux matches the V magnitudec at the respective
     * reference wavelength.
     * 
     * @param wavelength
     *            the wavelength
     * @return the normalized flux at the given wavelength
     */
    public double flux (double wavelength) {
	if (this.referencemagChanged || magSystemChanged) {
	    normalizationFactor = FluxNormalization.getNormalisationFactor (
		    myMagSystem, this);
	    magSystemChanged = false;
	    referencemagChanged = false;
	}
	return normalizationFactor * nonNormalizedFlux (wavelength);
    }

    /**
     * Sets the V magnitude to the given value.
     * 
     * @param vMagnitude
     *            the V magnitude
     */
    public void setMagnitude (double vMagnitude) {
	Double oldVMagnitude = new Double (getMagnitude ());
	Double newVMagnitude = new Double (vMagnitude);
	this.referenceMag = vMagnitude;
	this.referencemagChanged = true;
	if (oldVMagnitude.compareTo (newVMagnitude) != 0) {
	    firePropertyChange ("vMagnitude", oldVMagnitude, newVMagnitude);
	}
    }

    /**
     * Checks whether the given V magnitude value lies in the interval from
     * VNormalizedSpectrum.DEFAULT_MINIMUM_ALLOWED_V_MAGNITUDE to
     * VNormalizedSpectrum.DEFAULT_MAXIMUM_ALLOWED_V_MAGNITUDE and, if so,
     * assigns it to the V magnitude, using the setVMagnitude() method.
     * 
     * @param vMagnitude
     *            the V magnitude
     * @throws InvalidValueException
     *             if the given V magnitude value doesn't lie in the interval
     *             from VNormalizedSpectrum.DEFAULT_MINIMUM_ALLOWED_V_MAGNITUDE
     *             to VNormalizedSpectrum.DEFAULT_MAXIMUM_ALLOWED_V_MAGNITUDE
     */
    public void safeSetMagnitude (double vMagnitude) {
	if (vMagnitude < DEFAULT_MINIMUM_ALLOWED_V_MAGNITUDE
		|| vMagnitude > DEFAULT_MAXIMUM_ALLOWED_V_MAGNITUDE) {
	    throw new InvalidValueException (
		    "The V magnitude must lie in the interval from "
			    + DEFAULT_MINIMUM_ALLOWED_V_MAGNITUDE + " to "
			    + DEFAULT_MAXIMUM_ALLOWED_V_MAGNITUDE + ".");
	}
	setMagnitude (vMagnitude);
    }

    /**
     * Returns the V magnitude.
     * 
     * @return the V magnitude
     */
    public double getMagnitude () {
	return this.referenceMag;
    }

    public FluxNormalizationDescriptor getMagSystem () {

	if (this.myMagSystem == null)

	    this.myMagSystem = new FluxNormalizationDescriptor (
		    FluxNormalizationDescriptor.FluxModes[0]);

	return this.myMagSystem;
    }

    public void setMagSystem (FluxNormalizationDescriptor d) {
	if (this.myMagSystem == null) {
	    this.myMagSystem = new FluxNormalizationDescriptor (d);
	} else {
	    this.magSystemChanged = true;
	    this.myMagSystem.set (d);

	}
	firePropertyChange ("MagSystem", null, d);

    }

    public FluxNormalizationDescriptor[] getFluxModes () {
	return FluxNormalizationDescriptor.FluxModes;
    }
}
