package org.wiyn.etc.odi;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.wiyn.etc.SpectrumPropagationFilter;
import org.wiyn.etc.WIYNETC;
import org.wiyn.etc.configuration.ExposureConfig;
import org.wiyn.etc.configuration.PhotometryExposureResult;
import org.wiyn.etc.configuration.SourceExtent;
import org.wiyn.etc.configuration.SpectrumGenerationData;
import org.wiyn.etc.configuration.TelescopeProperties;

import za.ac.salt.pipt.common.Filter;
import za.ac.salt.pipt.common.GridSpectrum;

/**
 * This class implements an exposure. The spectrum arrives at the CCD, and we
 * produce the signal-to-noise ratios for target and sky. All the real work is
 * done here.
 * 
 * TODO: This class does not yet consequently draw al information form the ODI
 * class. In the future this class needs to derive all information from an
 * abstract instrument class.
 */

public class Exposure {

	private final static Logger myLogger = Logger.getLogger("etc.odi.Exposure");
	static {
		myLogger.addAppender(WIYNETC.UserLogger);

	}
	private double FOCAL_LENGTH_COLLIMATOR = 1;

	/** the focal length of the camera (in mm) */
	private double FOCAL_LENGTH_CAMERA = 1;

	/** the signal-to-noise ratio (for imaging) */
	private double snr;

	static NumberFormat myNF;
	static {
		myNF = new DecimalFormat("0.000E0");

	}
	/**
	 * The photon count in the brightest pixel. We compute it in expose(), and
	 * provide a getter method for other classes to use.
	 */
	private int peakLevel;

	private int skyLevel;
	/** the RSS ODIsetup for the exposure */
	private ExposureConfig myExposureConfig;

	/** the full width at half maximum for the PSF */
	private double fwhm;

	/** the incident target spectrum */
	private GridSpectrum targetSpectrum;

	/** the incident sky spectrum */
	private GridSpectrum skySpectrum;

	/**
	 * the (net) exposure timefor a single readout.
	 */
	private double exposureTime;

	/** the binning factor in x-direction */
	private long xbin;

	private long numberOfReadouts;

	private double RON = 0;

	/**
	 * Dark Current in e-/s/pixel
	 * 
	 */
	private double DarkCurrent = 0;

	/**
	 * Pixel scale in arcese/pixel of binned pixels
	 * 
	 */
	private double PixelScale_binned = 0;

	/**
	 * Solid Angle of binned pixel on sky in arcseconds
	 * 
	 */
	private double Omega_binned = 0;

	/**
	 * Clones the given target and sky spectrum, applies the given throughput
	 * filter to the cloned spectra and subsequently quantizes them. Depending
	 * on the value of the Config element, an imaging or spectroscopic exposure
	 * is carried out.
	 * 
	 * @param spectrumGenerationData
	 *            the data required for spectrum generation
	 * @param interferenceFilter
	 *            the interference filter
	 * @param rss
	 *            the RSS setup for the exposure
	 * @param fwhm
	 *            the full width at half maximum
	 */

	public Exposure(SpectrumGenerationData spectrumGenerationData,
			ExposureConfig myExposureConfig, double fwhm,
			Vector<PhotometryExposureResult> Results) {

		// Set the internal variables.
		this.targetSpectrum = new GridSpectrum(
				spectrumGenerationData.getTargetSpectrum());
		this.skySpectrum = new GridSpectrum(
				spectrumGenerationData.getSkySpectrum());
		this.myExposureConfig = myExposureConfig;
		this.fwhm = fwhm;
		this.exposureTime = myExposureConfig.ExposureTime;

		this.numberOfReadouts = myExposureConfig.ExposureRepeat;
		this.RON = ODI.theODI.getReadoutNoise(); // ODI OTAs
		this.DarkCurrent = ODI.theODI.getDarkCurrentValue();
		this.xbin = myExposureConfig.ExposureBinning;
		this.PixelScale_binned = ODI.theODI.getPixelScale() * this.xbin;
		this.Omega_binned = PixelScale_binned * PixelScale_binned;
		// long ybin = xbin;

		// SpectrumPropagationfilter does scale the flux by the telescope area
		// and applies atmospheric extinction. This is a leftover from the SALT
		// version and this scaling could be invoked more directly someday.

		SpectrumPropagationFilter targetPropagationFilter = new SpectrumPropagationFilter(
				spectrumGenerationData, SourceExtent.POINT);

		SpectrumPropagationFilter skyPropagationFilter = new SpectrumPropagationFilter(
				spectrumGenerationData, SourceExtent.DIFFUSE);

		// target and sky spectra are in units ergs/cm^2/Ang/s (/arcsec^2 for
		// sky).
		// After applying the PropagationFilter the unit is ergs/Ang/s
		// (/arcsec^2 for sky)

		myLogger.info("\n\n--  Calculating Fluxes from Object and Sky ------------------------------------------------------------\n");

		myLogger.info("Total Flux in OBJECT spectrum at X=0............................ [ergs/s/cm^2]          : "
				+ myNF.format(targetSpectrum.integrate()));
		myLogger.info("Total Flux SKY spectrum at X=0.................................. [ergs/s/cm^2/arcsec^2] : "
				+ myNF.format(skySpectrum.integrate()));

		targetPropagationFilter.apply(this.targetSpectrum);
		skyPropagationFilter.apply(this.skySpectrum);

		myLogger.info("Total Flux collected by telescope - losses in atmosphere OBJECT. [ergs/s]               : "
				+ myNF.format(targetSpectrum.integrate()));
		myLogger.info("Total Flux collected by telescope - losses in atmosphere SKY.... [ergs/s/arcsec^2]      : "
				+ myNF.format(skySpectrum.integrate()));

		// Get the instrument's current throughput filter. This also includes
		// transmission losses in the telescope.

		Filter odiFilter = ODI.theODI.getWIYNODI_ThroughputFilter();
		targetSpectrum.scale(odiFilter);
		skySpectrum.scale(odiFilter);

		myLogger.info("Total Flux left on the detector OBJECT.......................... [ ergs/s]              : "
				+ myNF.format(targetSpectrum.integrate()));

		myLogger.info("Total Flux left on the detector SKY............................. [ergs/s/arcsec^2]      : "
				+ myNF.format(skySpectrum.integrate()));

		// Quantize the spectra into counts/sec/Ang (/arcsec^2 if diffuse).
		// This is basically using E=hv at each wavelength bin

		this.targetSpectrum.quantize();
		this.skySpectrum.quantize();

		myLogger.info("Total Flux left on the detector in electrons OBJECT............. [e-/s]                 : "
				+ myNF.format(targetSpectrum.integrate()));

		myLogger.info("Total Flux left on the detector in electrons SKY................ [e-/s/arcsec^2]        : "
				+ myNF.format(skySpectrum.integrate()));

		double t = myExposureConfig.ExposureTime;
		double quickSN = targetSpectrum.integrate()
				* t
				/ Math.sqrt(100.
						* 10
						* 10
						+ (skySpectrum.integrate() + targetSpectrum.integrate())
						* t);
		myLogger.info("First S/N estimate: ..............................................................      : "
				+ myNF.format(quickSN));

		pointSourceExposure(Results);

	}

	/**
	 * Sets the binning factors for the x and y direction.
	 * 
	 * @param xbin
	 *            the binning factor for the x direction
	 * @param ybin
	 *            the binning factor for the y direction
	 */
	public void setBinning(int xbin, int ybin) {
		this.xbin = xbin;
		// this.ybin = ybin;
	}

	/**
	 * Sets the full width at half maximum for the point spread function.
	 * 
	 * @param fwhm
	 *            the full width at half maximum
	 */
	public void setFWHM(double fwhm) {
		this.fwhm = fwhm;
	}

	/**
	 * Returns the signal-to-noise ratio (for imaging) or the average
	 * signal-to-noise ratio (for spectroscopy)
	 * 
	 * @return the (in case of a spectroscopic exposure: average)
	 *         signal-to-noise ratio
	 */
	public double getSNR() {

		return snr;

	}

	/**
	 * Returns the RSS focal plane scale.
	 * 
	 * @return the RSS focal plane scale (in arcsec/mm)
	 */
	public double S_f() {
		double S_f = 1 / TelescopeProperties.FOCAL_LENGTH_TELESCOPE; // radians/mm
		S_f *= 3600 * Math.toDegrees(1); // "/mm
		myLogger.debug("Focal PLane scale is " + S_f + " \" per mm");
		return S_f;
	}

	/**
	 * Returns the RSS detector plate scale. The scale is the focal plane scale,
	 * multiplied by the ratio of the focal lengths of the collimator and
	 * camera.
	 * 
	 * @return the RSS detector plate scale (in arcsec/mm)
	 */
	public double S_d() {
		double s = S_f() * (FOCAL_LENGTH_COLLIMATOR / FOCAL_LENGTH_CAMERA);
		return s;
	}

	/**
	 * Carries out an imaging exposure. Upon entry into this procedure the
	 * internally stored Spectrum targetSpectrum is in the unit photons/Ang/s.
	 * The skySpectrum has the unit photons/Ang/s/arcsec^2
	 * 
	 */

	private void pointSourceExposure(Vector<PhotometryExposureResult> Results) {

		// PhotometryExposureResult Result;

		/** pixelscale in " per pixel */

		/** The solid angle on sky covered by one pixel */

		/**
		 * An interesting issue of aperture photometry is that depending on the
		 * S/N ratio on might be better of to choose a very small aperture.
		 * Therefore we try several apertures.
		 */

		// the integrated object electrons counts
		double NTarget = this.targetSpectrum.integrate() * exposureTime; // ergs

		// sky counts per binning element
		double NSky = this.skySpectrum.integrate() * exposureTime; // ergs/(sqrarcsec)
		double NSky_b = NSky * Omega_binned;

		// dark noise per binning element (squared)
		double Noise_d2 = (DarkCurrent * exposureTime
				* myExposureConfig.ExposureBinning * myExposureConfig.ExposureBinning);

		// noise per binning element generated by sky & RON & DC
		double Noise_b = Math.sqrt(NSky_b + RON * RON + Noise_d2);

		myLogger.info("\n\n--  Calculating Flux & Noise properties-----------------------------------\n");

		myLogger.info("Exposure Time per frame ................... [s] : "
				+ myNF.format(exposureTime));

		myLogger.info("Total Flux from Object ................... [e-] : "
				+ myNF.format(NTarget));

		myLogger.info("Sky Flux ......................... [e-/arsec^2] : "
				+ myNF.format(NSky));

		myLogger.info("Binning ............................... [pixel] : "
				+ myNF.format(myExposureConfig.ExposureBinning));

		myLogger.info("Sky photons ...................... [e-/bPixel2] : "
				+ myNF.format(NSky_b));

		myLogger.info("Dark Current Level ............... [e-/bPixel2] : "
				+ myNF.format(Noise_d2));

		myLogger.info("Sky noise & RON & DC per bin ..... [e-/bPixel2] : "
				+ myNF.format(Noise_b) + "\n");

		// Finally, also calculate surface brightness S/N
		myLogger.info("\n-- Now doing Surface Brightness photometry     ----------------------\n");

		double sbflux = NTarget * Omega_binned;

		myLogger.info("Object Surface Brightness ............ [e-/bpixel] : "
				+ myNF.format(sbflux));

		double sbSNR = sbflux / Noise_b;
		myLogger.info("Single exp. Surface Brightness S/N ... [e-/bpixel] : "
				+ myNF.format(sbSNR));

		sbSNR = sbSNR + Math.sqrt(myExposureConfig.ExposureRepeat);
		myLogger.info("Combined Surface Brightness S/N ...... [e-/bpixel] : "
				+ myNF.format(sbSNR));

		for (PhotometryExposureResult Result : Results) {

			Result.SurfaceBrightnessFlux = sbflux;

			Result.TotalFlux = NTarget;
			Result.SkyLevel = NSky_b;
			Result.SkyNoise = Noise_b;

			// Now calculate s/n, and peak value;
			double fwhmPixel = fwhm / PixelScale_binned; // FWHM in Pixelbinns
			double gausssigma = fwhmPixel / 2.354; // Gaussian Sigma in Pixels

			// peak flux if we would plot flux versus radius in " in an ideal
			// model
			double Flux0 = NTarget / (2 * Math.PI * gausssigma * gausssigma);
			Result.PeakLevel = Flux0;

			// Do Aperture Photometry

			// the aperture radius in pixels, driven by FWHM * aperture
			double ApertureRadius = Result.Aperture * fwhmPixel;
			// Calculate the Object Flux within the Aperture

			double StarPSFFlux = IntegrateGauss(Flux0, gausssigma,
					ApertureRadius);

			Result.ApertureFlux = StarPSFFlux;
			myLogger.info("\n-- Doing Aperture [Radius = "
					+ Result.Aperture
					+ " x FWHM = "
					+ ApertureRadius
					+ " pixels] photometry for point sources  ----------------------");
			myLogger.info("Peak level above sky                [e-] : "
					+ myNF.format(Flux0) + "\n");
			myLogger.info("Object Energy in Aperture           [e-] : "
					+ myNF.format(StarPSFFlux) + "\n");

			// Number of Pixels in the PSF
			double PSFPixels = Math.PI * ApertureRadius * ApertureRadius;
			myLogger.info("Number of binned pixels in aperture      : "
					+ myNF.format(PSFPixels) + "\n");

			// We add up all shot noise, sky noise, and readout noise. The error
			// in determining the sky level is ignored at this point.

			double sigma2 = StarPSFFlux + PSFPixels * (Noise_b * Noise_b);
			sigma2 = Math.sqrt(sigma2);
			myLogger.info("Total noise per exposure            [e-] : "
					+ myNF.format(sigma2) + "\n");

			myLogger.info("Number of exposures                 [e-] : "
					+ myExposureConfig.ExposureRepeat + "\n");

			snr = StarPSFFlux / sigma2;

			myLogger.info("Signal/Noise per exposure                : "
					+ myNF.format(snr) + "\n");

			snr = snr * Math.sqrt(myExposureConfig.ExposureRepeat);

			Result.SN = snr;
			myLogger.info("Signal/Noise of combined exposure        : "
					+ myNF.format(snr) + "\n");

		}

	}

	/**
	 * Calculate the area under a gaussian function up to radius rmax in units
	 * of sigma
	 */
	private double IntegrateGauss(double I0, double sigma, double rmax) {

		return (I0 * 2 * Math.PI * sigma * sigma * (1 - Math.exp(-(rmax * rmax)
				/ (2 * sigma * sigma))));

	}

	/**
	 * Returns the photon count in the brightest pixel.
	 * 
	 * @return the photon count in the brightest pixel
	 */
	public int getNmax() {
		return this.peakLevel;
	}

	/**
	 * Returns the percentage of pixel saturation for the peak count/bin.
	 * 
	 * @return the percentage of saturation
	 */
	public double getSaturationPercentage() {

		double saturation = 100 * ((peakLevel + skyLevel) / ODI.theODI
				.getSaturationLevel());

		return saturation;
	}
}