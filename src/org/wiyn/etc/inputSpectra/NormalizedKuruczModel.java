package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class describes a power law spectrum which is normalized so that it
 * matches a V magnitude.
 */
public class NormalizedKuruczModel extends KuruczModel implements
	NormalizedSpectrum {
    /** the V magnitude used for the normalization */
    private double referenzMagnitude = Double.NaN;
    private FluxNormalizationDescriptor myMagSystem = null;

    /** the temperature used in the previous flux calculation */
    private double previousTemperature = Double.NaN;

    /** the logarithm of the gravity used in the previous flux calculation */
    private double previousLogGravity = Double.NaN;

    /** the logarithm of the metallicity used in the previous flux calculation */
    private double previousLogMetallicity = Double.NaN;

    /** the V magnitude used in the previous flux calculation */
    private double previousVMagnitude = Double.NaN;

    /** the normalization factor */
    private double normalizationFactor = Double.NaN;
    private boolean magSystemChanged = true;;

    /**
     * Obtains the Kurucz model for the given temperature, gravity and
     * metallicity and sets the V magnitude used for the normalization to the
     * supplied value.
     * 
     * @param temperature
     *            the temperature (in Kelvin)
     * @param logGravity
     *            the logarithm of the gravity
     * @param logMetallicity
     *            the logarithm of the metallicity
     * @param vMagnitude
     *            the V magnitude used for the normalisation
     */

    public NormalizedKuruczModel(double temperature, double logGravity,
	    double logMetallicity, double vMagnitude) {
	super (temperature, logGravity, logMetallicity);

	this.referenzMagnitude = vMagnitude;
	this.myMagSystem = new FluxNormalizationDescriptor (
		FluxNormalizationDescriptor.FluxModes[0]);
	this.magSystemChanged = true;
    }

    /**
     * Returns the non-normalized power law flux at the given wavelength.
     * 
     * @return the non-normalized power law flux at the given wavelength
     */
    public double nonNormalizedFlux (double wavelength) {
	return super.flux (wavelength);
    }

    /**
     * Returns the normalized power law flux at the given wavelength. The
     * normalization is chosen so that the flux matches the V magnitude at the
     * respective reference wavelength.
     * 
     * @param wavelength
     *            the wavelength
     * @return the normalized flux at the given wavelength
     */
    public double flux (double wavelength) {
	// If the temperature, the gravity, the metallicity or the V magnitude
	// have changed since the last call of this method, the normalization
	// factor needs to be calculated again. Otherwise we may reuse the
	// existing value.
	if (temperature != previousTemperature
		|| logGravity != previousLogGravity
		|| logMetallicity != previousLogMetallicity
		|| referenzMagnitude != previousVMagnitude
		|| magSystemChanged == true) {
	    normalizationFactor = FluxNormalization.getNormalisationFactor (
		    myMagSystem, this);
	}

	// Record the current values of the temperature, gravity, metallicity
	// and V magnitude for the next call to this method.
	previousTemperature = temperature;
	previousLogGravity = logGravity;
	previousLogMetallicity = logMetallicity;
	previousVMagnitude = referenzMagnitude;
	magSystemChanged = false;
	// Normalize the flux and return the result.
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
	this.referenzMagnitude = vMagnitude;
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
	return referenzMagnitude;
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
