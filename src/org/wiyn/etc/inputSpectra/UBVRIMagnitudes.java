package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.JohnsonFilter;


/** This class provides the magnitudes for the U, B, V, R and I band. */
public class UBVRIMagnitudes
{
    /** the Johnson U filter */
	private static final JohnsonFilter JOHNSON_FILTER_U = new JohnsonFilter(JohnsonFilter.U);

    /** the Johnson B filter */
	private static final JohnsonFilter JOHNSON_FILTER_B = new JohnsonFilter(JohnsonFilter.B);

    /** the Johnson V filter */
	private static final JohnsonFilter JOHNSON_FILTER_V = new JohnsonFilter(JohnsonFilter.V);

    /** the Johnson R filter */
	private static final JohnsonFilter JOHNSON_FILTER_R = new JohnsonFilter(JohnsonFilter.R);

    /** the Johnson I filter */
	private static final JohnsonFilter JOHNSON_FILTER_I = new JohnsonFilter(JohnsonFilter.I);


    /** Returns the U band magnitude of the given spectrum.
     * @param spectrum the spectrum */
    public static double getUMagnitude(GenericSpectrum spectrum)
    {
	return JOHNSON_FILTER_U.getMagnitude(spectrum);
    }

    /** Returns the B band magnitude of the given spectrum.
     * @param spectrum the spectrum */
    public static double getBMagnitude(GenericSpectrum spectrum)
    {
	return JOHNSON_FILTER_B.getMagnitude(spectrum);
    }

    /** Returns the V band magnitude of the given spectrum.
     * @param spectrum the spectrum */
    public static double getVMagnitude(GenericSpectrum spectrum)
    {
	return JOHNSON_FILTER_V.getMagnitude(spectrum);
    }

    /** Returns the R band magnitude of the given spectrum.
     * @param spectrum the spectrum */
    public static double getRMagnitude(GenericSpectrum spectrum)
    {
	return JOHNSON_FILTER_R.getMagnitude(spectrum);
    }

    /** Returns the I band magnitude of the given spectrum.
     * @param spectrum the spectrum */
    public static double getIMagnitude(GenericSpectrum spectrum)
    {
	return JOHNSON_FILTER_I.getMagnitude(spectrum);
    }
}
