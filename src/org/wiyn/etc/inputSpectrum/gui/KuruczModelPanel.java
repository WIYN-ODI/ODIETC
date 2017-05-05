package org.wiyn.etc.inputSpectrum.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.KuruczModel;
import org.wiyn.etc.inputSpectra.NormalizedKuruczModel;
import org.wiyn.etc.inputSpectra.TargetSpectrum;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberComboBox;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;

/**
 * This class provides a panel for including (or excluding) a Kurucz model
 * spectrum in the target spectrum calculation and for setting all its
 * properties. The Kurucz model spectrum is normalized to match a V magnitude.
 */
public class KuruczModelPanel extends SpectrumPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the list of (logarithmic) gravity values */
	public static final String[] LOG_GRAVITY_VALUES = { "0.0", "0.5", "1.0",
			"1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0" };

	/** the list of (logarithmic) mtallicity values */
	public static final String[] LOG_METALLICITY_VALUES = { "0", "-1", "-2" };

	PiptNumberSpinner temperatureSpinner;
	PiptNumberComboBox gravityComboBox;
	PiptNumberComboBox metallicityComboBox;

	KuruczModel kuruczModel = null;

	/**
	 * Creates the panel for the Kurucz model.
	 * 
	 * @param normalizedKuruczModel
	 *            the V magnitude normalized Kurucz model spectrum associated
	 *            with this panel
	 * @param targetSpectrum
	 *            the target spectrum
	 */
	public KuruczModelPanel(NormalizedKuruczModel normalizedKuruczModel,
			TargetSpectrum targetSpectrum) {
		super(normalizedKuruczModel, targetSpectrum);
		this.kuruczModel = (KuruczModel) normalizedKuruczModel;
	}

	/**
	 * Returns the panel for setting the properties of the Kurucz model. The
	 * Kurucz model spectrum must be supplied. If the supplied spectrum happens
	 * to be no Kurucz model spectrum, the stack trace is output and an empty
	 * panel is returned.
	 * 
	 * @param spectrum
	 *            the spectrum (must be of the type KuruczModel)
	 * @return the panel for setting the properties of the given Kurucz model
	 *         spectrum
	 */
	public JPanel propertiesPanel(final GenericSpectrum spectrum) {
		// Create the panel with a flow layout (there will be one row only,
		// anyway).
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());

		// Is the supplied spectrum really that of a Kurucz model?
		if (!(spectrum instanceof KuruczModel)) {
			(new Exception("The supplied spectrum is no Kurucz model spectrum."))
					.printStackTrace();
			return pane;
		}

		// As we now know that we have a Kurucz model spectrum, we may perform a
		// respective cast.
		final KuruczModel kuruczModel = (KuruczModel) spectrum;

		// Add the spinner for the temperature (and a respective label).
		pane
				.add(new PiptLabel("Temperature (K):",
						"The temperature in Kelvin"));

		temperatureSpinner = new PiptNumberSpinner(kuruczModel, "temperature",
				3500, 50000, 250);
		pane.add(temperatureSpinner);

		// Add the combo box for the gravity values (and a respective label) */
		pane.add(new PiptLabel("log(G):",
				"The log of the gravity (in solar units)"));
		gravityComboBox = new PiptNumberComboBox(kuruczModel, "logGravity",
				LOG_GRAVITY_VALUES, PiptNumberComboBox.ASCENDING_ORDER);
		pane.add(gravityComboBox);

		// Add the combo box for the metallicity values (and a respective
		// label).
		pane.add(new PiptLabel("log(Z):",
				"The log of the metallicity (in solar units)"));
		metallicityComboBox = new PiptNumberComboBox(kuruczModel,
				"logMetallicity", LOG_METALLICITY_VALUES,
				PiptNumberComboBox.DESCENDING_ORDER);
		pane.add(metallicityComboBox);

		pane.add(new kuruczModelQuickSelect(this));
		// Return the panel.
		return pane;
	}

	public void remoteSetKuruczConfig(double teff, double logg, double feh) {
		this.kuruczModel.safeSetTemperature(teff);
		this.kuruczModel.safeSetLogGravity(logg);
		this.kuruczModel.safeSetLogMetallicity(feh);
	}
}

class kuruczModelQuickSelect extends JComboBox {

	/**
     * 
     */
    private static final long serialVersionUID = 7426457514389266469L;
	KuruczModelPanel myParent = null;

	public kuruczModelQuickSelect(KuruczModelPanel theModel) {
		super(kuruczConfig.getIndexStrings());
		myParent = theModel;
		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String item = (String) getSelectedItem();
				System.err.println(item);
				myParent.remoteSetKuruczConfig(kuruczConfig.getTeff(item),
						kuruczConfig.getLogg(item), kuruczConfig.getFeh(item));
			}

		});
	}
}

class kuruczConfig {
	String id;
	double teff;
	double logg;
	double feh;

	private kuruczConfig(String id, double teff, double logg, double feh) {
		super();
		this.id = id;
		this.teff = teff;
		this.logg = logg;
		this.feh = feh;
	}

	public static String[] getIndexStrings() {

		String[] results = new String[kuruczConfig.kuruczPresets.length];
		for (int ii = 0; ii < kuruczPresets.length; ii++)
			results[ii] = new String(kuruczPresets[ii].getId());
		return results;
	}

	public String getId() {
		return id;
	}

	private static int getIndexByName(String Name) {
		int result = -1;
		for (int ii = 0; ii < kuruczPresets.length; ii++) {
			if (Name.equals(kuruczPresets[ii].getId())) {
				result = ii;
				break;
			}
		}
		return result;
	}

	public static double getTeff(String id) {
		return kuruczPresets[getIndexByName(id)].teff;
	}

	public static double getLogg(String id) {
		return kuruczPresets[getIndexByName(id)].logg;
	}

	public static double getFeh(String id) {
		return kuruczPresets[getIndexByName(id)].feh;
	}

	private final static kuruczConfig[] kuruczPresets = {
			new kuruczConfig("O3V", 52500, +4.14, 0),
			new kuruczConfig("O5V", 44500, +4.04, 0),
			new kuruczConfig("O6V", 41000, +3.99, 0),
			new kuruczConfig("O8V", 35800, +3.94, 0),
			new kuruczConfig("B0V", 30000, +3.9, 0),
			new kuruczConfig("B3V", 18700, +3.94, 0),
			new kuruczConfig("B5V", 15400, +4.04, 0),
			new kuruczConfig("B8V", 11900, +4.04, 0),
			new kuruczConfig("A0V", 9520, +4.14, 0),
			new kuruczConfig("A5V", 8200, +4.29, 0),
			new kuruczConfig("F0V", 7200, +4.34, 0),
			new kuruczConfig("F5V", 6440, +4.34, 0),
			new kuruczConfig("G0V", 6030, +4.39, 0),
			new kuruczConfig("G5V", 5770, +4.49, 0),
			new kuruczConfig("K0V", 5250, +4.49, 0),
			new kuruczConfig("K5V", 4350, +4.54, 0),
			new kuruczConfig("M0V", 3850, +4.59, 0),
			new kuruczConfig("M2V", 3580, +4.64, 0),
			new kuruczConfig("M5V", 3240, +4.94, 0),
			new kuruczConfig("B0III", 29000, +3.34, 0),
			new kuruczConfig("B5III", 15000, +3.49, 0),
			new kuruczConfig("G0III", 5850, +2.94, 0),
			new kuruczConfig("G5III", 5150, +2.54, 0),
			new kuruczConfig("K0III", 4750, +2.14, 0),
			new kuruczConfig("K5III", 3950, +1.74, 0),
			new kuruczConfig("M0III", 3800, +1.34, 0),
			new kuruczConfig("O5I", 40300, +3.34, 0),
			new kuruczConfig("O6I", 39000, +3.24, 0),
			new kuruczConfig("O8I", 34200, +3.24, 0),
			new kuruczConfig("BOI", 26000, +2.84, 0),
			new kuruczConfig("B5I", 13600, +2.44, 0),
			new kuruczConfig("AOI", 9730, +2.14, 0),
			new kuruczConfig("A5I", 8510, +2.04, 0),
			new kuruczConfig("F0I", 7700, +1.74, 0),
			new kuruczConfig("F5I", 6900, +1.44, 0),
			new kuruczConfig("G0I", 5550, +1.34, 0),
			new kuruczConfig("G5I", 4850, +1.14, 0),
			new kuruczConfig("K0I", 4420, +0.94, 0),
			new kuruczConfig("K5I", 3850, +0.34, 0),
			new kuruczConfig("M0I", 3650, +0.14, 0),
			new kuruczConfig("M2I", 3450, -0.06, 0)

	};
}