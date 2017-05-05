package org.wiyn.etc.inputSpectra;

import java.io.InputStream;

import za.ac.salt.pipt.common.GridResource;
import za.ac.salt.pipt.common.GridSpectrum;

/**
 * This class provides a solar spectrum.
 */
public class SolarSpectrum extends GridSpectrum {

	/** a host where to look for the solar spectrum */
	private static final String HOST = "http://www.wiyn.org/";

	/** a file where to look for the solar spectrum */
	private static final String PATH = "/resources/rss_pipt-data/solar.10A.txt";

	/** a solar spectrum */
	private static SolarSpectrum ss = null;

	/** Creates the solar spectrum. */
	public SolarSpectrum() {
		// Create the grid (without the correct values yet). */
		reset(DEFAULT_LB_STARTING_VALUE, DEFAULT_LB_RESOLUTION,
				DEFAULT_LB_RANGE);
		if (SolarSpectrum.ss == null) {
			GridResource gr = new GridResource(HOST, PATH);
			InputStream inputStream = gr.getInputStream();
			if (inputStream != null) {
				this.read(inputStream);
				// cache it
				SolarSpectrum.ss = (SolarSpectrum) (new SolarSpectrum(this));
			}
		} else {
			for (int i = 0; i < ss.n(); i++) {
				this.y[i] = ss.y[i];
			}
		}
	}

	/** Creates the solar spectrum as a clone of the given solar spectrum. */
	public SolarSpectrum(SolarSpectrum ss) {
		this.reset(ss.j(), ss.k(), ss.m());
		for (int i = 0; i < ss.n(); i++) {
			this.y[i] = ss.y[i];
		}
	}
}
