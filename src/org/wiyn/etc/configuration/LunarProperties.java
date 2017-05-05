package org.wiyn.etc.configuration;

import java.util.ArrayList;

import za.ac.salt.pipt.common.Phase;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataElement;


/** This class provides the lunar properties of some target. These comprise the zenith distance of the moon, the lunar phase and the lunar elongation. */
public class LunarProperties extends GenericProperties implements PiptData
{
    /** the zenith distance of the moon (in degrees) */
    private double moonZenithDistance;

    /** the lunar phase (in degrees from 0 to 180; 0 means full moon) */
    private double lunarPhase;

    /** the lunar elongation (in degrees) */
    private double lunarElongation;


    /** Sets the lunar properties to the given values.
     * @param moonZenithDistance the zenith distance of the moon
     * @param lunarPhase the phase of the moon (in degrees from 0 to 180; 0 means full moon)
     * @param lunarElongation the lunar elongation (in degrees) */
    public LunarProperties(double moonZenithDistance, double lunarPhase, double lunarElongation)
    {
	this.moonZenithDistance = moonZenithDistance;
	this.lunarPhase = lunarPhase;
	this.lunarElongation = lunarElongation;
    }


    /** Sets the zenith distance of the moon to the given value.
     * @param moonZenithDistance the zenith distance of the moon (in degrees) */
    public void setMoonZenithDistance(double moonZenithDistance)
    {
	Double oldMoonZenithDistance = new Double(getMoonZenithDistance());
	Double newMoonZenithDistance = new Double(moonZenithDistance);
	this.moonZenithDistance = moonZenithDistance;
	if (oldMoonZenithDistance.compareTo(newMoonZenithDistance) != 0) {
	firePropertyChange("moonZenithDistance", oldMoonZenithDistance, newMoonZenithDistance);
	}
    }


    /** Checks whether the given value for the zenith distance of the moon lies in the range from 0 to 180 degrees and, if so, assigns it to the zenith distance of the moon, using the setZenithDistance() method.
     * @param moonZenithDistance the zenith distance of the moon (in degrees)
     * @throws InvalidValueException if the given zenith distance doesn't lie in the interval from 0 to 180 degrees */
    public void safeSetMoonZenithDistance(double moonZenithDistance)
    {
	if (moonZenithDistance < 0 || moonZenithDistance > 180) {
	    throw new InvalidValueException("The moon zenith distance must lie between 0 and 180 degrees.");
	}
	setMoonZenithDistance(moonZenithDistance);
    }


    /** Returns the zenith distance of the moon.
     * @return the zenith distance of the moon (in degrees) */
    public double getMoonZenithDistance()
    {
	return moonZenithDistance;
    }


    /** Sets the lunar phase to the given value.
     * @param lunarPhase the lunar phase (in degrees from 0 to 180; 0 denotes full moon) */
    public void setLunarPhase(double lunarPhase)
    {
	Double oldLunarPhase = new Double(getLunarPhase());
	Double newLunarPhase = new Double(lunarPhase);
	this.lunarPhase = lunarPhase;
	if (oldLunarPhase.compareTo(newLunarPhase) != 0) {
	    firePropertyChange("lunarPhase", oldLunarPhase, newLunarPhase);
	}
    }


    /** Checks whether the givemn lunar phase value lies in the interval from 0 to 180 degrees and, if so, assigns it to the lunar phase, using the setLunarPhase() method.
     * @param lunarPhase the lunar phase (in degrees from 0 to 180; 0 denotes full moon)
     * @throws InvalidValueException if the given lunar phase doesn't lie in the interval from 0 to 180 degrees */
    public void safeSetLunarPhase(double lunarPhase)
    {
	if (lunarPhase < 0 || lunarPhase > 180) {
	    throw new InvalidValueException("The lunar phase must have a value between 0 and 180 degrees.");
	}
	setLunarPhase(lunarPhase);
    }


    /** Returns the lunar phase.
     * @return the lunar phase ((in degrees from 0 to 180; 0 denotes full moon) */
    public double getLunarPhase()
    {
	return lunarPhase;
    }


    /** Sets the lunar elongation.
     * @param lunarElongation the lunar elongation (in degrees) */
    public void setLunarElongation(double lunarElongation)
    {
	Double oldLunarElongation = new Double(getLunarElongation());
	Double newLunarElongation = new Double(lunarElongation);
	this.lunarElongation = lunarElongation;
	if (oldLunarElongation.compareTo(newLunarElongation) != 0) {
	    firePropertyChange("lunarElongation", oldLunarElongation, newLunarElongation);
	}
    }


    /** Checks whether the given solar elongation angle lies in the interval from 0 to 180 degrees and, if so, assigns it to the solar elongation, using the setSolarElongation() method.
     * @param lunarElongation the lunar elongation (in degrees)
     * @throws InvalidValueException if the given lunar elongation doesn't lie in the interval from 0 to 180 degrees */
    public void safeSetLunarElongation(double lunarElongation)
    {
	if (lunarElongation < 0 || lunarElongation > 180) {
	    throw new InvalidValueException("The lunar elongation must have a value between 0 and 180 degrees.");
	}
	setLunarElongation(lunarElongation);
    }


    /** Returns the lunar elongation.
     * @return the lunar elongation (in degrees) */
    public double getLunarElongation()
    {
	return lunarElongation;
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
