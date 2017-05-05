package org.wiyn.etc.configuration;

import java.util.ArrayList;

import za.ac.salt.pipt.common.Phase;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataElement;

/**
 * This class provides the properties of the telescope. These comprise the
 * zenith distance of the target, the effective mirror area, the seeing (in
 * direction of the zenith) and the corresponding FWHM of the seeing disk in the
 * focal plane.
 * 
 * TODO: Eventually it might be beneficial to move the transmission function of
 * the telescope mirrors here.
 */
public class TelescopeProperties extends GenericProperties implements PiptData {
    /** the zenith distance of the target (in degrees) */
    private double airmass;

    /** the effective mirror area (in cm^2) */
    private double effectiveArea;

    /** the seeing (in direction of the zenith) */
    private double seeing;

    final static public String[] defaultSeeingValues = { "0.4", "0.6", "0.8",
	    "1.0" };

    /** The instrument's error budget in seeing */
    private double DIQDegradation = 0.05;

    /**
     * the focal length of the telescope (in mm)
     */
    public static double FOCAL_LENGTH_TELESCOPE = 22050;

    private double sqr (double x) {
	return x * x;
    }

    public TelescopeProperties() {

	this (1, 0, 0.7);
    }

    /**
     * Sets the telescope properties to the given values.
     * 
     * @param targetZenithDistance
     *            the zenith distance of the target (in degrees)
     * @param effectiveArea
     *            the effective mirror area (in cm^2)
     * @param seeing
     *            the seeing (in direction of the zenith)
     */
    public TelescopeProperties(double targetZenithDistance,
	    double effectiveArea, double seeing) {

	this.airmass = targetZenithDistance;

	// WIYn area in cm^2
	this.effectiveArea = (Math.PI * sqr (3.5 / 2) - Math.PI
		* sqr ((3.5 * 0.42) / 2))
		* sqr (100);

	this.seeing = seeing;
    }

    /**
     * Returns the FWHM of the seeing disk in the focal plane corresponding to
     * the seeing and target zenith distance. We take into account the
     * sec(z)^0.6 and add in quadrature the SALT PSF error budget (0.6").
     * 
     * @return the FWHM of the seeing disk in the focal plane corresponding to
     *         the seeing (in arcseconds)
     */
    public double getFWHM () {
	double fwhm;

	// The FWHM for the zenith distance is just the seeing.
	fwhm = seeing;
	double targetDistance = Math.acos (1 / airmass);

	// Degrade the FWHM to the target's zenith distance.
	fwhm *= Math.pow (1 / Math.cos (targetDistance), 0.6);

	// Add the ODI PSF error budget in quadrature.
	fwhm = Math.sqrt (fwhm * fwhm + DIQDegradation * DIQDegradation);

	// Return the FWHM.
	return (fwhm);
    }

    /**
     * Sets the zenith distance of the target to the given value.
     * 
     * @param airmass
     *            the zenith distance of the target (in degrees)
     */
    public void setAirmass (double airmass) {
	Double oldTargetZenithDistance = new Double (getAirmass ());
	Double newTargetZenithDistance = new Double (airmass);
	this.airmass = airmass;
	if (oldTargetZenithDistance.compareTo (newTargetZenithDistance) != 0) {
	    firePropertyChange ("targetZenithDistance",
		    oldTargetZenithDistance, newTargetZenithDistance);
	}
    }

    /**
     * Checks whether the given value for the zenith distance of the target lies
     * in the interval from 0 to 180 degrees and, if so, assigns it to the
     * zenith distance of the target, using the setTargetZenithDistance()
     * method.
     * 
     * @param airmass
     *            the zenith distance of the target (in degrees)
     * @throws InvalidValueException
     *             if the given zenith distance doesn't lie in the interval from
     *             0 to 180 degrees
     */
    public void safeSetAirmass (double airmass) {
	if (airmass < 0 || airmass > 180) {
	    throw new InvalidValueException (
		    "The target zenith distance must have a value between 0 and 180 degrees.");
	}
	setAirmass (airmass);
    }

    /**
     * Returns the zenith distance of the target.
     * 
     * @return the zenith distance of the target (in degrees)
     */
    public double getAirmass () {
	return airmass;
    }

    /**
     * Returns the effective mirror area.
     * 
     * @return the effective mirror area (in cm^2)
     */
    public double getEffectiveArea () {
	return effectiveArea;
    }

    /**
     * Sets the seeing (in direction of the zenith).
     * 
     * @param seeing
     *            the seeing (in arcseconds)
     */
    public void setSeeing (double seeing) {
	Double oldSeeing = new Double (getSeeing ());
	Double newSeeing = new Double (seeing);
	this.seeing = seeing;
	if (oldSeeing.compareTo (newSeeing) != 0) {
	    firePropertyChange ("seeing", oldSeeing, newSeeing);
	}
    }

    /**
     * Checks whether the given seeing value is positive and, if so, assigns it
     * to the seeing, using the setSeeing() method.
     * 
     * @param seeing
     *            the seeing (in arcseconds)
     * @throws InvalidValueException
     *             if the given seeing isn't positive
     */
    public void safeSetSeeing (double seeing) {
	if (seeing <= 0) {
	    throw new InvalidValueException ("The seeing must be positive.");
	}
	setSeeing (seeing);
    }

    /**
     * Returns the seeing (in direction of the zenith).
     * 
     * @return the seeing (in arcseconds)
     */
    public double getSeeing () {
	return seeing;
    }

    /** Returns an empty list, as there are no children. */
    public ArrayList<PiptDataElement> getChildren () {
	return new ArrayList<PiptDataElement> ();
    }

    /**
     * Returns true.
     * 
     * @param childElement
     *            the name of the child element
     * @param phase
     *            the phase for vwhich the proposal is valid
     * @return true
     */
    public boolean isRequired (String childElement, Phase phase) {
	return true;
    }

    /**
     * Returns true.
     * 
     * @param phase
     *            the phase for vwhich the proposal is valid
     * @return true
     */
    public boolean isSubTreeComplete (Phase phase) {
	return true;
    }
}
