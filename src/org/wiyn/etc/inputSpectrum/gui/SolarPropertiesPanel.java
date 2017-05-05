package org.wiyn.etc.inputSpectrum.gui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.wiyn.etc.configuration.SolarProperties;

import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;

/** This class provides the panel for setting the solar properties. */
public class SolarPropertiesPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates the panel for the given solar properties.
     * 
     * @param solarProperties
     *                the solar properties
     */
    public SolarPropertiesPanel ()  {
	this (new SolarProperties(2008.5, 180, -90));
    }
    
    public SolarPropertiesPanel(final SolarProperties solarProperties) {
	// Create the panel. As there will be only one row anyway, we choose a
	// flow layout. The content is left-justified.
	super ();
	setLayout (new FlowLayout (FlowLayout.LEFT));

	// Add a titled border.
	Border etchedBorder = BorderFactory.createEtchedBorder ();
	Border titledBorder = BorderFactory.createTitledBorder (etchedBorder,
		"Solar Items");
	setBorder (titledBorder);

	// Add the spinner for the year of observation (and a respective label).
	add (new PiptLabel ("Obs. Year:",
		"Time of year determines the solar cycle for airglow"));
	PiptNumberSpinner observationSpinner = new PiptNumberSpinner (
		solarProperties, "observationYear", 2004, 2010, 0.5);
	add (observationSpinner);

	// Add the spinner for the solar elongation (and a respective label).
	add (new PiptLabel ("Solar Elongation:",
		"How far is it from the target?"));
	PiptNumberSpinner elongationSpinner = new PiptNumberSpinner (
		solarProperties, "solarElongation", 0, 180, 1);
	add (elongationSpinner);

	// Add the spinner for the ecliptic latitude (and a respective label).
	add (new PiptLabel ("Ecliptic Latitude:", "How much zodiacal light?"));
	PiptNumberSpinner latitudeSpinner = new PiptNumberSpinner (
		solarProperties, "eclipticLatitude", -90, 90, 1);
	add (latitudeSpinner);
    }
}
