package org.wiyn.etc.inputSpectra;

import org.wiyn.etc.configuration.LunarProperties;
import org.wiyn.etc.configuration.SolarProperties;
import org.wiyn.etc.configuration.SpectrumGenerationData;
import org.wiyn.etc.configuration.TelescopeProperties;

import za.ac.salt.pipt.common.GridSpectrum;

/**
 * This class computes a sky spectrum (ergs/s/A/arcsec^2). Note that the unit of
 * length is angstroms. The sky spectrum is the sum of 3 component spectra:<br>
 * <br>
 * airglow: depends on<br>
 * t: time of observation<br>
 * Z: target's zenith distance<br>
 * <br>
 * zodiacal light:<br>
 * l, b: target's ecliptic coordinates<br>
 * moonlight:<br>
 * <br>
 * Zm: lunar zenith distance<br>
 * beta: lunar phase angle (0 = new)<br>
 * rho: target/moon separation<br>
 * <br>
 * See SALT PI Tool Spectrum Simulator Requirements document SALT-3172AS0005 for
 * specific details.
 */

public class SkySpectrum extends GridSpectrum {
	/** the solar properties */
	SolarProperties solarProperties;

	/** the lunar properties */
	LunarProperties lunarProperties;

	/** the telescope properties */
	TelescopeProperties telescopeProperties;

	/**
	 * Creates the sky spectrum for the given spectrum generation data. The
	 * listeners for updating the sky spectrum are added.
	 * 
	 * @param spectrumGenerationData
	 *            the spectrum generation data for which the sky spectrum is
	 *            created
	 */
	public SkySpectrum(SpectrumGenerationData spectrumGenerationData) {
		// Retrieve the various required quantities.
		solarProperties = spectrumGenerationData.getSolarProperties();
		lunarProperties = spectrumGenerationData.getLunarProperties();
		telescopeProperties = spectrumGenerationData.getTelescopeProperties();

		/**
		 * NB: the argument "l" is not really the target's ecliptic longitude,
		 * but its solar elongation. It is l - l(sun).
		 */

		this.setDiffuse(true); // Sky spectrum is diffuse emission
	}

	/** Creates the grid for the sky spectrum. */
	public void update() {
		// Create the grid (without the correct values yet).
		reset(DEFAULT_LB_STARTING_VALUE, DEFAULT_LB_RESOLUTION,
				DEFAULT_LB_RANGE);

		// Record the fact that no update is required any longer.
		setUpdateNeeded(false);

		// Retrieve the required quantities.
		double t = solarProperties.getObservationYear();
		double Z = telescopeProperties.getAirmass();
		double l = solarProperties.getSolarElongation();
		double b = solarProperties.getEclipticLatitude();
		double Zm = lunarProperties.getMoonZenithDistance();
		double beta = lunarProperties.getLunarPhase();
		double rho = lunarProperties.getLunarElongation();

		// fix the zenith distances
		Z = Math.acos(1 / Z);
		Zm = Math.toRadians(Zm);

		// get the target and moon airmasses using the airglow method
		double X = 1 / Math.sqrt(1 - 0.96 * Math.pow(Math.sin(Z), 2));
		double Xm = 1 / Math.sqrt(1 - 0.96 * Math.pow(Math.sin(Zm), 2));

		/**
		 * First, the airglow.
		 */

		AirGlowSpectrum ags = new AirGlowSpectrum(true);

		// get an atmosphere
		Atmosphere a = new Atmosphere();

		// apply extinction (X-1 because we're coming from zenith, not space)
		a.apply(ags, X - 1);

		// finish the weird airglow extinction
		ags.scale(X);

		// apply the effect of the solar cycle
		double Cs = 0.37; // amplitude of solar modulation of airglow
		double Ps = 9.67; // current period of solar cycle
		double t0 = 2001.5; // time of cycle 23 maximum
		double f = (t - t0) / Ps; // the fractional solar period
		f *= 2 * Math.PI; // in radians
		double g = (1 + Cs * Math.cos(f)) / (1 + Cs);
		ags.scale(g);
		this.add(ags);
		ags = null;

		/**
		 * Second, the zodiacal light.
		 */

		double Cz0 = 56.5; // S10 at ecliptic pole
		double Cz1 = 92; // S10 in ecliptic for l-l_sun > 100 deg
		double Cz2 = 0; // for l-l_sun < 100 deg and b > 45 deg
		if (l < Math.toRadians(100)) {
			if (b < Math.toRadians(b)) {
				Cz2 = 219.2; // looking into the sun
			}
		}
		// we will need this a few times
		double asb = Math.abs(Math.sin(Math.toRadians(b)));
		// and this
		double s45 = Math.sin(Math.toRadians(45));
		double h = Cz0;
		h += Cz1 * (1 - asb);
		h += Cz2 * (Math.toRadians(100) - l / Math.toRadians(40))
				* (s45 - asb / s45) * (1 - asb);
		// start with a solar spectrum
		SolarSpectrum zls = new SolarSpectrum();
		// normalize it to unity at 5500 A
		zls.scale(5500, 1);
		// extinct it by the airmass
		a.apply(zls, 1 / Math.cos(Z));
		// final scaling
		zls.scale(2.92e-20 * h);
		this.add(zls);
		zls = null;

		/**
		 * Third, the moonlight. Oh, baby.
		 */

		GridSpectrum mls = null;
		// are we above or below the horizon?
		if (Zm >= Math.PI / 2) {
			mls = new GridSpectrum(0); // assume null spectrum
		} else {
			// start with f(rho)
			// we will need a normalized extinction curve
			a = new Atmosphere();
			a.scale(5500, 1);
			// get fr(rho)
			GridSpectrum fr = new GridSpectrum();
			for (int i = 0; i < fr.n(); i++) {
				double x = fr.x(i);
				double y = x / 5500;
				fr.setValue(i, y);
			}
			fr.power(-4); // Rayleigh scattering
			fr.div(a);
			double cosRho = Math.cos(Math.toRadians(rho));
			fr.scale(1.06 + cosRho * cosRho);
			fr.scale(Math.pow(10, 5.36));
			// get fm(rho)
			GridSpectrum fm = new GridSpectrum();
			for (int i = 0; i < fm.n(); i++) {
				double x = fm.x(i);
				double y = x / 5500;
				fm.setValue(i, y);
			}
			fm.power(-0.5); // Mie scattering
			fm.div(a);
			a = null;
			fm.scale(Math.pow(10, (6.15 - rho / 40)));
			// combine them
			GridSpectrum frho = new GridSpectrum();
			frho.add(fr);
			frho.add(fm);
			fr = null;
			fm = null;
			GridSpectrum B = new GridSpectrum(1);
			B.scale(frho);
			frho = null;
			// get the illuminance
			// get the visual magnitude Vm
			double Vm = -12.73 + 0.026 * Math.abs(beta) + 4e-9
					* Math.pow(beta, 4);
			// the illuminance outside the atmosphere
			double Istar = Math.pow(10, -0.4 * (Vm + 16.37));
			// the illuminance inside the atmosphere
			GridSpectrum I = new GridSpectrum(1);
			a = new Atmosphere();
			a.apply(I, Xm);
			I.scale(Istar);
			B.scale(I);
			I = null;
			// get the (1-10^-0.4kZ) factor
			GridSpectrum other = new GridSpectrum(1);
			a.apply(other, X);
			a = null;
			other.scale(-1);
			other.add(1);
			B.scale(other);
			other = null;

			// now do the solar part
			// set up the albedo
			GridSpectrum albedo = new GridSpectrum();
			for (int i = 0; i < albedo.n(); i++) {
				double x = albedo.x(i);
				double y = 1 + 2.1e-4 * (x - 5500);
				albedo.setValue(i, y);
			}
			SolarSpectrum ss = new SolarSpectrum();
			ss.scale(5500, 1.12e-19);
			ss.scale(albedo);
			albedo = null;
			mls = new GridSpectrum(1);
			mls.scale(B);
			mls.scale(ss);
			B = null;
			ss = null;
		}
		this.add(mls);
	}

	/**
	 * Clones the given sky spectrum.
	 * 
	 * @param skySpectrum
	 *            the sky spectrum
	 */
	public SkySpectrum(SkySpectrum skySpectrum) {
		super(skySpectrum);
	}
}
