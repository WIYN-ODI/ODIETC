package org.wiyn.etc.inputSpectrum.gui;

import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.ABReference;
import org.wiyn.etc.inputSpectra.EmissionLine;
import org.wiyn.etc.inputSpectra.KC96GalaxyTemplate;
import org.wiyn.etc.inputSpectra.NormalizedKC96GalaxyTemplate;
import org.wiyn.etc.inputSpectra.NormalizedKuruczModel;
import org.wiyn.etc.inputSpectra.TargetSpectrum;
import org.wiyn.etc.inputSpectra.VNormalizedBlackbody;
import org.wiyn.etc.inputSpectra.VNormalizedPowerLaw;
import org.wiyn.etc.inputSpectra.VNormalizedUserSuppliedSpectrum;

public class InputSpectrumSelection extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = -403390850813006616L;

	/** the default (i.e. initial) value for the V magnitude */
	public static final double DEFAULT_V_MAGNITUDE = 20;

	/** the default (i.e. initial) value for the blackbody temperature */
	public static final double DEFAULT_BLACKBODY_TEMPERATURE = 5000;

	/** the default (i.e. initial) value for the power law index */
	public static final double DEFAULT_POWER_LAW_INDEX = -2;

	/** the default (i.e. initial) value for the temperature of the Kurucz model */
	public static final double DEFAULT_KURUCZ_MODEL_TEMPERATURE = 3500;

	/**
	 * the default (i.e. initial) value for the logarithm of the gravity of the
	 * Kurucz mnodel
	 */
	public static final double DEFAULT_KURUCZ_MODEL_LOG_GRAVITY = 0;

	/**
	 * the default (i.e. initial) value for the logarithm of the metallicity of
	 * the Kurucz model
	 */
	public static final double DEFAULT_KURUCZ_MODEL_LOG_METALLICITY = 0;

	public static final String DEFAULT_KC96_TEMPLATE = "Bulge";

	/**
	 * the default (i.e. initial) value for the URL of the data for the
	 * user-supplied spectrum
	 */
	public static final URL DEFAULT_USER_SUPPLIED_SPECTRUM_URL = null;

	/**
	 * the default (i.e. initial) value for the wavelength of the center of an
	 * emission line
	 */
	public static final double DEFAULT_EMISSION_LINE_WAVELENGTH = 3950;

	/** the default (i.e. initial) value for the FWHM of an emission line */
	public static final double DEFAULT_EMISSION_LINE_FWHM = 20;

	/** the default (i.e. initial) value for the total flux in an emission line */
	public static final double DEFAULT_EMISSION_LINE_FLUX = 1e-16;

	public InputSpectrumSelection() {
		this(SpectrumGenerationPane.targetSpectrum);
	}

	public InputSpectrumSelection(TargetSpectrum targetSpectrum) {
		// Obtain all the spectra and properties required for the spectrum
		// generation with default values.
		VNormalizedBlackbody normalizedBlackbody = new VNormalizedBlackbody(
				DEFAULT_BLACKBODY_TEMPERATURE, DEFAULT_V_MAGNITUDE);

		VNormalizedPowerLaw normalizedPowerLaw = new VNormalizedPowerLaw(
				DEFAULT_POWER_LAW_INDEX, DEFAULT_V_MAGNITUDE);

		KC96GalaxyTemplate k96 = new NormalizedKC96GalaxyTemplate();

		NormalizedKuruczModel normalizedKuruczModel = new NormalizedKuruczModel(
				DEFAULT_KURUCZ_MODEL_TEMPERATURE,
				DEFAULT_KURUCZ_MODEL_LOG_GRAVITY,
				DEFAULT_KURUCZ_MODEL_LOG_METALLICITY, DEFAULT_V_MAGNITUDE);

		VNormalizedUserSuppliedSpectrum normalizedUserSuppliedSpectrum = new VNormalizedUserSuppliedSpectrum(
				DEFAULT_USER_SUPPLIED_SPECTRUM_URL, DEFAULT_V_MAGNITUDE);

		EmissionLine emissionLine = new EmissionLine(
				DEFAULT_EMISSION_LINE_WAVELENGTH, DEFAULT_EMISSION_LINE_FWHM,
				DEFAULT_EMISSION_LINE_FLUX);

		ABReference ABReferenceSpectrum = new ABReference();

		Box sources = new Box(BoxLayout.Y_AXIS);

		// Add the panels for the various spectra.
		sources.add("Kurucz Model", new KuruczModelPanel(normalizedKuruczModel,
				targetSpectrum));
		sources.add("Blackbody", new BlackbodyPanel(normalizedBlackbody,
				targetSpectrum));
		sources.add("Power Law", new PowerLawPanel(normalizedPowerLaw,
				targetSpectrum));
		sources.add("Emisison Line", new EmissionLinePanel(emissionLine,
				targetSpectrum));
		sources.add("User Supplied", new UserSuppliedSpectrumPanel(
				normalizedUserSuppliedSpectrum, targetSpectrum));
/*		sources.add("AB Calibration Spectrum", new FlatFnPanel(
				ABReferenceSpectrum, targetSpectrum));*/
		sources.add("KC96 Galaxy Templates", new KC96Panel(k96,
				targetSpectrum));
		this.add(sources);

	}

	public static void main(String args[]) {

		JFrame f = new JFrame();
		InputSpectrumSelection s = new InputSpectrumSelection();
		f.getContentPane().add(s);
		f.pack();
		f.setVisible(true);
	}

}
