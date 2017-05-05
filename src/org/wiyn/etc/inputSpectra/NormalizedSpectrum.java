package org.wiyn.etc.inputSpectra;

/**
 * This interface provides various methods for V magnitude normalized spectra.
 * These include the getter and setter method for the V magnitude as well as a
 * method for retrieving the non-normalized flux values. In addition it defines
 * default values for the minimum and maximum allowed V magnitudes.
 */

public interface NormalizedSpectrum {
    /** the default value for the minimum allowed V magnitude */
    public static final double DEFAULT_MINIMUM_ALLOWED_V_MAGNITUDE = -99;

    /** the default value for the maximum allowed V magnitude */
    public static final double DEFAULT_MAXIMUM_ALLOWED_V_MAGNITUDE = 99;

    /**
     * Returns the flux at the given wavelength. This method must be implemented
     * by the subclass.
     * 
     * @param wavelength
     *            (in Angstrom)
     * @return the flux at the given wavelength
     */
    public abstract double flux (double wavelength);

    /**
     * Returns the name of the spectrum. This method must be provided by the
     * inheriting class.
     * 
     * @return the name of the spectrum
     */
    public abstract String name ();

    /**
     * Returns the non-normalized ("original") flux at the given wavelength.
     * 
     * @param wavelength
     *            the wavelength (in Angstrom)
     * @return the non-normalized flux at the given wavelength
     */
    public double nonNormalizedFlux (double wavelength);

    /**
     * Sets the V magnitude used for the normalization.
     * 
     * @param vMagnitude
     *            the V magnitude used for the normalization
     */
    public void setMagnitude (double vMagnitude);

    /**
     * Returns the V magnitude used for the normalization.
     * 
     * @return the V magnitude used for the normalization
     */
    public double getMagnitude ();

    public FluxNormalizationDescriptor getMagSystem ();

    public void setMagSystem (FluxNormalizationDescriptor d);

    public abstract FluxNormalizationDescriptor[] getFluxModes ();
}
