package org.wiyn.etc.configuration;

import org.wiyn.etc.inputSpectra.Atmosphere;
import org.wiyn.etc.inputSpectra.SkySpectrum;
import org.wiyn.etc.inputSpectra.TargetSpectrum;

import za.ac.salt.pipt.common.Filter;
import za.ac.salt.pipt.common.GridSpectrum;

/**
 * This class serves a container for all the objects required for generating a
 * spectrum. Setter and getter methods are provided throughout. In addition it
 * provides a Filter object for describing the atmosphere/area/mirror
 * throughput.
 */

public class SpectrumGenerationData {
	/** the target spectrum */
	private TargetSpectrum targetSpectrum;

	/** the solar properties */
	private SolarProperties solarProperties;

	/** the lunar properties */
	private LunarProperties lunarProperties;

	/** the telescope properties */
	private TelescopeProperties telescopeProperties;

	/**
	 * Creates the container with the given values.
	 * 
	 * @param targetSpectrum
	 *            the target spectrum
	 * @param solarProperties
	 *            the solar properties
	 * @param lunarProperties
	 *            the lunar properties
	 * @param telescopeProperties
	 *            the telescope properties
	 */

	public SpectrumGenerationData(TargetSpectrum targetSpectrum,
			SolarProperties solarProperties, LunarProperties lunarProperties,
			TelescopeProperties telescopeProperties) {

		// Set the internal variables to the given values.
		setTargetSpectrum(targetSpectrum);
		setSolarProperties(solarProperties);
		setLunarProperties(lunarProperties);
		setTelescopeProperties(telescopeProperties);
	}

	/**
	 * Returns a Filter object describing the atmosphere/area/mirror throughput.
	 * 
	 * TODO: Add telluric extinction.
	 * 
	 * @return a Filter object describing the atmosphere/area/mirror throughput
	 */
	public Filter getFilter() {
		// start off with unity
		Filter resultFilter = new Filter(1.0);

		// gather the light.
		resultFilter.scale(telescopeProperties.getEffectiveArea());

		// scale the airmass here.
		// Daniel Harbeck 2009/01/30: Changed storage in telescope properties
		// tostore airmass directly!

		double airmass = telescopeProperties.getAirmass();

		Atmosphere a = new Atmosphere();
		GridSpectrum g = new GridSpectrum(1);
		a.apply(g, airmass);
		resultFilter.scale(g);

		return resultFilter;
	}

	/**
	 * Sets the target spectrum to the given value.
	 * 
	 * @param targetSpectrum
	 *            the target spectrum
	 */
	private void setTargetSpectrum(TargetSpectrum targetSpectrum) {
		this.targetSpectrum = targetSpectrum;
	}

	/**
	 * Updates and returns the target spectrum.
	 * 
	 * @return the target spectrum
	 */
	public TargetSpectrum getTargetSpectrum() {
		targetSpectrum.update();
		return targetSpectrum;
	}

	/**
	 * Creates and returns the sky spectrum.
	 * 
	 * @return the sky spectrum
	 */
	public SkySpectrum getSkySpectrum() {
		SkySpectrum skySpectrum = new SkySpectrum(this);
		skySpectrum.update();
		return skySpectrum;
	}

	/**
	 * Sets the solar properties to the given value.
	 * 
	 * @param solarProperties
	 *            the solar properties
	 */
	private void setSolarProperties(SolarProperties solarProperties) {
		this.solarProperties = solarProperties;
	}

	/**
	 * Returns the solar properties.
	 * 
	 * @return the solar properties
	 */
	public SolarProperties getSolarProperties() {
		return solarProperties;
	}

	/**
	 * Sets the lunar properties to the given value.
	 * 
	 * @param lunarProperties
	 *            the lunar properties
	 */
	private void setLunarProperties(LunarProperties lunarProperties) {
		this.lunarProperties = lunarProperties;
	}

	/**
	 * Returns the lunar properties.
	 * 
	 * @return the lunar properties
	 */
	public LunarProperties getLunarProperties() {
		return lunarProperties;
	}

	/**
	 * Sets the telescope properties to the given value.
	 * 
	 * @param telescopeProperties
	 *            the telescope properties
	 */
	private void setTelescopeProperties(TelescopeProperties telescopeProperties) {
		this.telescopeProperties = telescopeProperties;
	}

	/**
	 * Returns the telescope properties.
	 * 
	 * @return the telescope properties
	 */
	public TelescopeProperties getTelescopeProperties() {
		return telescopeProperties;
	}
}
