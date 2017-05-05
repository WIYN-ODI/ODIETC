package org.wiyn.etc.odi.gui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import layout.TableLayout;

import org.wiyn.etc.configuration.ExposureConfig;
import org.wiyn.etc.configuration.PhotometryExposureResult;
import org.wiyn.etc.configuration.SpectrumGenerationData;
import org.wiyn.etc.gui.TelescopePropertiesPanel;
import org.wiyn.etc.inputSpectrum.gui.LunarPropertiesPanel;
import org.wiyn.etc.odi.Exposure;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * This class provides the panel for entering all the data describing the
 * detector and exposure.
 */
public class ExposurePanel extends JPanel {
	/**
     * 
     */

	// private static int BoxSizes = 400;
	private static final long serialVersionUID = 1L;

	private ExposureConfig myExposureConfig;
	/** the data relevant for the spectrum generation */
	private SpectrumGenerationData spectrumGenerationData;

	/** the panel displaying the signal-to-noise ratio and the pixel saturation */
	private ExposureResultDisplayPanel signalPanel;

	/** Apertures in units of FWHM */
	protected final static double[] Apertures = { 0.5, 0.75, 1, 1.25, 1.5,
			1.75, 2 };

	/**
	 * Creates the panel with all the GUI stuff.
	 * 
	 * @param rss
	 *            the RSS setup associated with this panel
	 * @param spectrumGenerationData
	 *            the data relevant for the spectrum generation
	 */

	public ExposurePanel(SpectrumGenerationData spectrumGenerationData) {
		// We use a border layout.

		// this.setPreferredSize(new java.awt.Dimension(602, 418));
		myExposureConfig = new ExposureConfig();
		// new ExposureResult();
		this.spectrumGenerationData = spectrumGenerationData;

		// Create all the GUI components.
		// GridLayout thisLayout = new GridLayout (2, 2);
		// thisLayout.setHgap (2);
		// thisLayout.setVgap (0);
		// thisLayout.setColumns (2);
		// thisLayout.setRows (2);
		// this.setLayout (thisLayout);

		TableLayout thisLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL, TableLayout.FILL },
				{ TableLayout.FILL, TableLayout.FILL } });
		setLayout(thisLayout);

		{
			JPanel ConfigPanel = new ODIExposureSetupPanel(
					this.myExposureConfig);
			// ConfigPanel.setMaximumSize (new Dimension (BoxSizes, 400));
			this.add(ConfigPanel, "0,0,c,c");
		}

		{
			JComponent conditionBox = Box.createVerticalBox();

			Border titledBorder = BorderFactory.createTitledBorder(null,
					"Observing Conditions");
			// conditionBox.setMaximumSize (new Dimension (BoxSizes, 400));
			conditionBox.setBorder(titledBorder);

			LunarPropertiesPanel theMoon = new LunarPropertiesPanel(
					spectrumGenerationData.getLunarProperties());
			TelescopePropertiesPanel theTelescope = new TelescopePropertiesPanel(
					spectrumGenerationData.getTelescopeProperties());

			conditionBox.add(theMoon);
			conditionBox.add(theTelescope);

			this.add(conditionBox, "1,0,c,c");
		}

		// Box resultsBox = Box.createHorizontalBox ();
		{
			signalPanel = new ExposureResultDisplayPanel();
			// signalPanel.setMaximumSize (new Dimension (BoxSizes, 400));
			this.add(signalPanel, "0,1,c,c");
		}
		// Put all the GUI components into a nice box.
		// Box setupAndResultBox = Box.createVerticalBox ();
		// setupAndResultBox.add (setupBox);
		// setupAndResultBox.add (resultsBox);
		// add (setupAndResultBox);

		// Add the button for displaying the filter loss function to the box for
		// this panel.
		JButton displayButton = new JButton(
				"<html><b><h1>Expose</h1></b></html>");
		displayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				expose();
			}
		});
		// add (new JLabel (), BorderLayout.SOUTH);

		add(displayButton, "1,1,c,c");

	}

	/**
	 * Carries out the exposure, updating various values and, in case of a
	 * spectroscopic configuration, plots the signal-to-noise ratio and the
	 * photon spectrum.
	 */

	// public static void main (String[] args) {
	//
	// JFrame j = new JFrame ();
	//
	// j.getContentPane ().add (p);
	// j.pack ();
	// j.setVisible (true);
	// }
	private void expose() {

		Vector<PhotometryExposureResult> Results = new Vector<PhotometryExposureResult>();
		for (int ii = 0; ii < Apertures.length; ii++) {
			PhotometryExposureResult Result = new PhotometryExposureResult();
			Result.Aperture = Apertures[ii];
			Results.add(Result);
		}
		try {
			// Carrying out the exposure may take a few seconds. We indicate
			// this by changing the cursor.
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			// If no spectrum has been selected, there can be no meaningful
			// exposure.
			if (spectrumGenerationData.getTargetSpectrum().getNumberOfSpectra() == 0) {
				throw new InvalidValueException(
						"No spectrum has been selected.");
			}
			@SuppressWarnings("unused")
			Exposure myExposure = new Exposure(spectrumGenerationData,
					myExposureConfig, spectrumGenerationData
							.getTelescopeProperties().getFWHM(), Results);

			// Update the SNR and pixel saturation labels.
			signalPanel.update(Results);

		} catch (InvalidValueException ive) {
			signalPanel.setUndefined();
			JOptionPane
					.showMessageDialog(
							ExposurePanel.this,
							"<html>No exposure could be carried out because of the following reason:<br><br><em>"
									+ ive.getMessage() + "</html>");
		} finally {
			// Irrespective of everything went according to plan, we want to
			// have back the normal cursor.
			setCursor(Cursor.getDefaultCursor());
		}

	}
}