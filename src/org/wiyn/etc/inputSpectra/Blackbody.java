package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class describes the spectrum of blackbody radiation. WARNING: The units
 * of the flux are fairly arbitrary. You should use this class only if it is
 * ensured that the flux values will be properly normalized!
 */

public class Blackbody extends GenericSpectrum {
	/** 2*pi*hc**2 with angstroms as the unit of length */
	private static final double C1 = 3.74185e11;

	/** hc/k with angstroms as the unit of length */
	private static final double C2 = 1.438820545e8;

	/** the temperature of the blackbody (in Kelvin) */
	protected double temperature = Double.NaN;

	/**
	 * Creates a blackbody spectrum with the given temperature and frees the
	 * memory.
	 * 
	 * @param temperature
	 *            the temperature of the blackbody (in Kelvin)
	 */
	public Blackbody(double temperature) {
		this.temperature = temperature;
		freeMemory();
	}

	/**
	 * Returns the flux of the blackbody in arbitrary units.
	 * 
	 * @param wavelength
	 *            the wavelength (in Angstrom)
	 * @return the flux of the blackbody in arbitrary units
	 */
	public double flux(double wavelength) {
		return (C1 / Math.pow(wavelength, 5.0))
				/ (Math.exp(C2 / (wavelength * temperature)) - 1);
	}

	/**
	 * Updates the spectrum. As there is nothing to update, this means that this
	 * method does nothing.
	 */
	public void update() {
		// do nothing
	}

	/**
	 * Sets the blackbody temperature to the given value.
	 * 
	 * @param temperature
	 *            the blackbody temperature (in Kelvin)
	 */
	public void setTemperature(double temperature) {
		Double oldTemperature = new Double(getTemperature());
		Double newTemperature = new Double(temperature);
		this.temperature = temperature;
		if (oldTemperature.compareTo(newTemperature) != 0) {
			firePropertyChange("temperature", oldTemperature, newTemperature);
		}
	}

	/**
	 * Checks whether the given temperature value is positive and, if so,
	 * assigns it to the blackbody temperature, using the setTemperature()
	 * method.
	 * 
	 * @param temperature
	 *            the blackbody temperature (in Kelvin)
	 * @throws InvalidValueException
	 *             if the given temperature isn't positive
	 */
	public void safeSetTemperature(double temperature) {
		if (temperature <= 0) {
			throw new InvalidValueException(
					"The temperature must be non-negative.");
		}
		setTemperature(temperature);
	}

	/**
	 * Returns the temperature of the blackbody.
	 * 
	 * @return the temperature of the blackbody (in Kelvin)
	 */
	public double getTemperature() {
		return temperature;
	}

	/**
	 * Returns the name of this spectrum, which is taken to be "Blackbody".
	 * 
	 * @return the string "Blackbody"
	 */
	public String name() {
		return "Blackbody";
	}
}
