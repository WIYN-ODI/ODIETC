package org.wiyn.etc.inputSpectra;

/**
 * Describes a flux normalizsation
 */

public class FluxNormalizationDescriptor {

    public static int MAGSYS_INDEF = 0;
    public static int MAGSYS_VEGA = 1;
    public static int MAGSYS_AB = 2;

    public static FluxNormalizationDescriptor[] FluxModes = {

	    new FluxNormalizationDescriptor ("Vega V", 5500, MAGSYS_VEGA),
	    new FluxNormalizationDescriptor ("SDSS u", 3551, MAGSYS_AB),
	    new FluxNormalizationDescriptor ("SDSS g", 4686, MAGSYS_AB),
	    new FluxNormalizationDescriptor ("SDSS r", 6165, MAGSYS_AB),
	    new FluxNormalizationDescriptor ("SDSS i", 7481, MAGSYS_AB),
	    new FluxNormalizationDescriptor ("SDSS z", 8931, MAGSYS_AB),
	    new FluxNormalizationDescriptor ("SDSS V AB comparison", 5500,
		    MAGSYS_AB) };
    /**
     * The central wavelength of the passband filter in which we normalize
     * 
     */
    double centralWavelength;
    /**
     * The magnitude system in which we normalize
     * 
     */
    int magnitudeSystem = 0;

    /** Filter description for normalisation */
    String Name = null;

    public FluxNormalizationDescriptor(FluxNormalizationDescriptor reference) {
	this (reference.Name, reference.centralWavelength,
		reference.magnitudeSystem);
    }

    protected FluxNormalizationDescriptor(String Name,
	    double centralWavelength, int magnitudeSystem) {
	super ();
	this.Name = Name;
	this.centralWavelength = centralWavelength;
	this.magnitudeSystem = magnitudeSystem;

    }

    public double getCentralWavelength () {
	return centralWavelength;
    }

    public int getMagnitudeSystem () {
	return magnitudeSystem;
    }

    public String getName () {
	return Name;
    }

    public String toString () {

	return getName ();
    }

    public int compareTo (FluxNormalizationDescriptor myFluxNormalisation) {

	if (this.centralWavelength != myFluxNormalisation.centralWavelength
		|| this.magnitudeSystem != myFluxNormalisation.magnitudeSystem)
	    return 1;

	return 0;
    }

    public void set (FluxNormalizationDescriptor reference) {
	this.centralWavelength = reference.centralWavelength;
	this.magnitudeSystem = reference.magnitudeSystem;
	this.Name = reference.Name;
    }

}
