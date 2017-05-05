package org.wiyn.etc.configuration;

import java.util.ArrayList;

import za.ac.salt.pipt.common.Phase;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataElement;


/** This class covers the solar properties of a target. These comprise the year of observation (as a double number; the season in the year is relevant for the airglow), the solar elongation and the ecliptic latitude. */
public class SolarProperties extends GenericProperties implements PiptData
{
    /** the year of observation (as a double number; the season in the year is relevant) */
    private double observationYear;

    /** the solar elongation (in degrees) */
    private double solarElongation;

    /** the ecliptic latitude (in degrees) */
    private double eclipticLatitude;


    /** Sets the solar properties to the given values.
     * @param observationYear the year of observation (as a double number)
     * @param solarElongation the solar elongation (in degrees)
     * @param eclipticLatitude the ecliptic latitude (in degrees) */
    public SolarProperties(double observationYear, double solarElongation, double eclipticLatitude)
    {
	this.observationYear = observationYear;
	this.solarElongation = solarElongation;
	this.eclipticLatitude = eclipticLatitude;
    }


    /** Sets the year of observation to the given value (as a double number; the season in the year is relevant for the airglow).
     * @param observationYear the year of observation */
    public void setObservationYear(double observationYear)
    {
	Double oldObservationYear = new Double(getObservationYear());
	Double newObservationYear = new Double(observationYear);
	this.observationYear = observationYear;
	if (oldObservationYear.compareTo(newObservationYear) != 0) {
	    firePropertyChange("observationYear", oldObservationYear, newObservationYear);
	}
    }


    /** Checks whether the given value for the year of observation is greater than 1900 and, if so, assigns it to the year of observation, using the setObservationYear() method.
     * @param observationYear the year of observation
     * @throws InvalidValueException if a year of observation before 1900 is passed */
    public void safeSetObservationYear(double observationYear)
    {
	if (observationYear < 1900) {
	    throw new InvalidValueException("The year of observation must be 1900 or later.");
	}
	setObservationYear(observationYear);
    }


    /** Returns the year of observation.
     * @return the year of observation */
    public double getObservationYear()
    {
	return observationYear;
    }


    /** Sets the solar elongation to the given value.
     * @param solarElongation the solar elongation (in degrees) */
    public void setSolarElongation(double solarElongation)
    {
	Double oldSolarElongation = new Double(getSolarElongation());
	Double newSolarElongation = new Double(solarElongation);
	this.solarElongation = solarElongation;
	if (oldSolarElongation.compareTo(newSolarElongation) != 0) {
	    firePropertyChange("solarElongation", oldSolarElongation, newSolarElongation);
	}
    }


    /** Checks whether the given solar elongation value lies in the interval from 0 to 180 degrees and, if so, assigns it to the solar elongation, using the setSolarElongation() method.
     * @param solarElongation the solar elongation (in degrees)
     * @throws InvalidValueException if the given solar elongation doesn't lie in the interval from 0 to 180 degrees */
    public void safeSetSolarElongation(double solarElongation)
    {
	if (solarElongation < 0 || solarElongation > 180) {
	    throw new InvalidValueException("The solar elongation must have a value between 0 and 180 degrees.");
	}
	setSolarElongation(solarElongation);
    }


    /** Returns the solar elongation.
     * @return the solar elongation (in degrees) */
    public double getSolarElongation()
    {
	return solarElongation;
    }


    /** Sets the ecliptic latitude to the given value.
     * @param eclipticLatitude the ecliptic latitude (in degrees) */
    public void setEclipticLatitude(double eclipticLatitude)
    {
	Double oldEclipticLatitude = new Double(getEclipticLatitude());
	Double newEclipticLatitude = new Double(eclipticLatitude);
	this.eclipticLatitude = eclipticLatitude;
	if (oldEclipticLatitude.compareTo(newEclipticLatitude) != 0) {
	    firePropertyChange("eclipticLatitude", oldEclipticLatitude, newEclipticLatitude);
	}
    }


    /** Checks whether the given ecliptic latitude value lies in the interval from -90 to 90 degrees and, if so, assigns it to the ecliptic latitude, using the setEclipticLatitude() method.
     * @param eclipticLatitude the ecliptic latitude (in degrees)
     * @throws InvalidValueException if the given ecliptic latitude doesn't lie in the interval from -90 to 90 degrees */
    public void safeSetEclipticLatitude(double eclipticLatitude)
    {
	if (eclipticLatitude < -90 || eclipticLatitude > 90) {
	    throw new InvalidValueException("The ecliptic latitude must lie between -90 and 90 degrees.");
	}
	setEclipticLatitude(eclipticLatitude);
    }


    /** Returns the ecliptic latitude.
     * @return the ecliptic latitude (in degrees) */
    public double getEclipticLatitude()
    {
	return eclipticLatitude;
    }


    /** Returns an empty list, as there are no children. */
    public ArrayList<PiptDataElement> getChildren()
    {
	return new ArrayList<PiptDataElement>();
    }


    /** Returns true.
     * @param childElement the name of the child element
     * @param phase the phase for vwhich the proposal is valid
     * @return true */
    public boolean isRequired(String childElement, Phase phase)
    {
	return true;
    }


    /** Returns true.
     * @param phase the phase for vwhich the proposal is valid
     * @return true */
    public boolean isSubTreeComplete(Phase phase)
    {
	return true;
    }
}
