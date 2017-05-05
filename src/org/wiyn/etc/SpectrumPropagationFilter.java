package org.wiyn.etc;

import org.wiyn.etc.configuration.SourceExtent;
import org.wiyn.etc.configuration.SpectrumGenerationData;

import za.ac.salt.pipt.common.Filter;

/**
 * This class provides a filter which describes the changes in the spectrum
 * occuring during the propagation through the atmosphere, the reflection off
 * the mirrors.
 */
public class SpectrumPropagationFilter extends Filter {
	/**
	 * Creates the filter for the given RSS setup and telescope properties.
	 * 
	 * @param rss
	 *            the RSS setup
	 * @param spectrumGenerationData
	 *            the data for generating the spectra
	 * @param sourceExtent
	 *            the (type of) source extent
	 */
	public SpectrumPropagationFilter(
			SpectrumGenerationData spectrumGenerationData,
			SourceExtent sourceExtent) {
		// Start with no extinction.
		super(1.0);

		// Include the extinction in the atmosphere and the reflection off the
		// telescope mirrors.
		scale(spectrumGenerationData.getFilter());

		
	}
}
