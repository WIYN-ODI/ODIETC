package org.wiyn.etc;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.wiyn.etc.configuration.LunarProperties;
import org.wiyn.etc.configuration.SolarProperties;
import org.wiyn.etc.configuration.SpectrumGenerationData;
import org.wiyn.etc.configuration.TelescopeProperties;
import org.wiyn.etc.gui.SpectrumViewPanel;
import org.wiyn.etc.inputSpectra.SkySpectrum;
import org.wiyn.etc.inputSpectra.TargetSpectrum;
import org.wiyn.etc.inputSpectrum.gui.SpectrumGenerationPane;
import org.wiyn.etc.odi.ODI;
import org.wiyn.etc.odi.gui.ExposurePanel;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.Filter;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * This class provides a panel containing all the GUI parts of the Exposure Time
 * Calculator.
 */
public class WiynEtcPanel extends JPanel implements ActionListener {
	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/** the default (i.e. initial) value for the year of observation */
	public static final double DEFAULT_OBSERVATION_YEAR = 2008.5;

	/** the default (i.e. initial) value for the solar elongation */
	public static final double DEFAULT_SOLAR_ELONGATION = 180;

	/** the default (i.e. initial) value for the ecliptic latitude */
	public static final double DEFAULT_ECLIPTIC_LATITUDE = -90;

	/** the default (i.e. initial) value for the zenith distance of the moon */
	public static final double DEFAULT_MOON_ZENITH_DISTANCE = 180;

	/** the default (i.e. initial) value for the lunar phase */
	public static final double DEFAULT_LUNAR_PHASE = 180;

	/** the default (i.e. initial) value for the lunar elongation */
	public static final double DEFAULT_LUNAR_ELONGATION = 180;

	/** the default (i.e. initial) value for the zenith distance of the target */
	public static final double DEFAULT_AIRMASS = 1;

	/** the default (i.e. initial) value for the effective mirror area */
	// public static final double DEFAULT_EFFECTIVE_AREA = 460000;
	/**
	 * the default (i.e. initial) value for the seeing (in direction of the
	 * zenith)
	 */
	public static final double DEFAULT_SEEING = 0.6;

	/**
	 * Creates the panel containing all the GUI parts of the RSS Simulator Tool
	 * for the given RSS setups.
	 */

	SpectrumGenerationData spectrumGenerationData;
	JTabbedPane ETCMainTabs;
	public SpectrumViewPanel spectrumViewPanel;

	// private WiynEtcPanel myself;

	public WiynEtcPanel() {
		// myself = this;
		// Create a target spectrum.
		TargetSpectrum targetSpectrum = new TargetSpectrum();

		// Create the solar properties.
		SolarProperties solarProperties = new SolarProperties(
				DEFAULT_OBSERVATION_YEAR, DEFAULT_SOLAR_ELONGATION,
				DEFAULT_ECLIPTIC_LATITUDE);

		// Create the lunar properties.
		LunarProperties lunarProperties = new LunarProperties(
				DEFAULT_MOON_ZENITH_DISTANCE, DEFAULT_LUNAR_PHASE,
				DEFAULT_LUNAR_ELONGATION);

		// Create the telescope properties.
		TelescopeProperties telescopeProperties = new TelescopeProperties(
				DEFAULT_AIRMASS, 0, DEFAULT_SEEING);

		// Create the container for all the spectrum generation data.
		spectrumGenerationData = new SpectrumGenerationData(targetSpectrum,
				solarProperties, lunarProperties, telescopeProperties);

		// Get the panes with the tab contents.
		JPanel spectrumGenerationPane = new JPanel();

		// First page: Spectrum generation
		spectrumGenerationPane.add(new SpectrumGenerationPane(
				spectrumGenerationData));

		// If the RSS simulator isn't run in single configuration mode, we have
		// to provide an exposure panel for each configuration. Otherwise the
		// exposure panels may be the same for each configuration.
		ExposurePanel exposurePanel = new ExposurePanel(spectrumGenerationData);

		// We choose the RSS setup for the imaging panel, as this panel is
		// chosen initially (in case of not running in single configuration
		// mode) or all the RSS setups are the same anyway.
		JPanel exposurePane = new JPanel();
		exposurePane.add(exposurePanel);

		JPanel spectraDisplayPane = new JPanel();
		spectrumViewPanel = new SpectrumViewPanel();
		spectraDisplayPane.add(spectrumViewPanel);

		// Create the tabbed pane holding the panes for the spectrum generation,
		// the RSS setup and making an exposure.
		ETCMainTabs = new JTabbedPane();

		ETCMainTabs.addTab("Input Spectrum", spectrumGenerationPane);
		ETCMainTabs.addTab("Make an Exposure", exposurePane);
		ETCMainTabs.addTab("Spectrum Plot", spectraDisplayPane);
		ETCMainTabs.addTab("Logging", WIYNETC.UserLogger.getPanel());
		// Add the tabbed pane to the panel.
		add(ETCMainTabs);

		JButton displayButton = new JButton("Display");

		displayButton.addActionListener(this);

		add(displayButton, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent event) {
		// Plotting takes a few seconds. Hence it is a good idea to
		// choose a corresponding cursor. Irrespective of whether
		// an exception is thrown during plotting, the cursor must
		// be reset to the default value; for that reason we use a
		// try and finally block.
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {
			// Get the target spectrum.

			boolean display = true;
			// If no spectrum has been selected, there is little
			// sense in displaying... (We must not use the "cloned"
			// targetSpectrum variable here, as its spectra vector
			// is null!)

			if (spectrumGenerationData == null
					|| spectrumGenerationData.getTargetSpectrum()
							.getNumberOfSpectra() == 0) {

				display = false;
			}

			double[] yRange = { 0, 1.0 };
			if (display) {

				// Get the sky spectrum.

				SkySpectrum skySpectrum = new SkySpectrum(
						spectrumGenerationData);
				skySpectrum.update();

				TargetSpectrum targetSpectrum = new TargetSpectrum(
						spectrumGenerationData.getTargetSpectrum());

				// yRange[1] = Math.max (targetSpectrum.ymax (),
				// spectrumGenerationData.getSkySpectrum ().ymax ());

				// Create the plots.
				SpectrumViewPanel.updateObjectSpectrum(targetSpectrum,
						skySpectrum, null);
				// SpectrumViewPanel.updateSkySpectrum (skySpectrum, yRange);
			}
			// Display the Throughput

			Filter WIYNODIFilter = ODI.theODI.getWIYNODI_ThroughputFilter();

			// first instrument only
			SpectrumViewPanel.updateThroughput(WIYNODIFilter, yRange);

		} catch (InvalidValueException ive) {
			ErrorLog.generalErrorMessage(this,
					"Displaying failed because of the following reason:<br><br><em>"
							+ ive.getMessage() + "</em>");
		} finally {
			ETCMainTabs.setSelectedIndex(2);
			setCursor(Cursor.getDefaultCursor());

		}
	}

	/**
	 * Returns the size of the scroll panes for the tabs. It is taken to be 70
	 * per cent of the screen size.
	 * 
	 * @return the size of the scroll panes
	 */
	public static Dimension getScrollPaneSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension scrollPaneSize = new Dimension();
		scrollPaneSize.setSize(0.75 * screenSize.getWidth(),
				0.75 * screenSize.getHeight());
		return scrollPaneSize;
	}
}
