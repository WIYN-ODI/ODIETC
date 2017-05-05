package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class describes a power law spectrum which is normalized so that it
 * matches a V magnitude.
 */
public class VNormalizedPowerLaw extends PowerLaw implements NormalizedSpectrum {
    /** the V magnitude used for the normalization */
    private double vMagnitude = Double.NaN;

    /** the index used in the previous flux calculation */
    private double previousIndex = Double.NaN;

    /** the V magnitude used in the previous flux calculation */
    private double previousVMagnitude = Double.NaN;

    /** the normalization factor */
    private double normalizationFactor = Double.NaN;

    /**
     * Sets the index of the power law and the the V magnitude used for the
     * normalization to the given values. Note that the index must have the
     * correct sign, so that, e.g., the index -2 would refer to a power law of
     * the form wavelength^-2.
     * 
     * @param index
     *            the index of the power law
     * @param vMagnitude
     *            the V magnitude used for the normalisation
     */
    public VNormalizedPowerLaw(double index, double vMagnitude) {
	super (index);
	this.vMagnitude = vMagnitude;
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
     * normalization is chosen so that the flux matches the V magnitudec at the
     * respective reference wavelength.
     * 
     * @param wavelength
     *            the wavelength
     * @return the normalized flux at the given wavelength
     */
    public double flux (double wavelength) {
	// If either the index or the V magnitude have changed since the last
	// call of this method, the normalization factor needs to be calculated
	// again. Otherwise we may reuse the existing value.
	if (index != previousIndex || vMagnitude != previousVMagnitude) {
	    normalizationFactor = FluxNormalization
		    .VegaVnormalizationFactor (this);
	}

	// Record the current values of the index and V magnitude for the next
	// call to this method.
	previousIndex = index;
	previousVMagnitude = vMagnitude;

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
	this.vMagnitude = vMagnitude;
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
	return vMagnitude;
    }

    public FluxNormalizationDescriptor getMagSystem () {
	// TODO Auto-generated method stub
	return FluxNormalizationDescriptor.FluxModes[0];
    }

    public void setMagSystem (FluxNormalizationDescriptor d) {
	// TODO Auto-generated method stub

    }

    public FluxNormalizationDescriptor[] getFluxModes () {
	
	return new FluxNormalizationDescriptor[] { FluxNormalizationDescriptor.FluxModes[0] };
    }
}
