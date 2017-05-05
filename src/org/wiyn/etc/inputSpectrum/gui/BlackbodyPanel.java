package org.wiyn.etc.inputSpectrum.gui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.Blackbody;
import org.wiyn.etc.inputSpectra.TargetSpectrum;
import org.wiyn.etc.inputSpectra.VNormalizedBlackbody;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;



/** This class provides a panel for including (or excluding) a blackbody spectrum in the target spectrum calculation and for setting its temperature. The blackbody spectrum is normalized to match a V magnitude. */
public class BlackbodyPanel extends SpectrumPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Creates the panel for the given blackbody spectrum.
     * @param normalizedBlackbody the V magnitude normalized blackbody spectrum associated with this panel
     * @param targetSpectrum the target spectrum */
    public BlackbodyPanel(VNormalizedBlackbody normalizedBlackbody, TargetSpectrum targetSpectrum)
    {
	super(normalizedBlackbody, targetSpectrum);
    } 


    /** Returns the panel for setting the temperature of the blackbody. The blackbody spectrum must be supplied. If the supplied spectrum happens to be no blackbody spectrum,  the stack trace is output and an empty panel is returned.
     * @param spectrum the spectrum (must be of the type Blackbody)
     * @return the panel for setting the temperature of the given blackbody spectrum */
    public JPanel propertiesPanel(final GenericSpectrum spectrum)
    {
	// Create the panel with a flow layout (there will be one row only,
	// anyway).
	JPanel pane = new JPanel();
	pane.setLayout(new FlowLayout());

	// Is the supplied spectrum really that of a blackbody?
	if (!(spectrum instanceof Blackbody)) {
	    (new Exception("The supplied spectrum is no blackbody spectrum.")).printStackTrace();
	    return pane;
	}

	// As we now know that we have a blackbody spectrum, we may perform a
	// respective cast.
	final Blackbody blackbody = (Blackbody) spectrum;

	// Add the spinner for the temperature (and a respective label).
        pane.add(new PiptLabel("Temperature (K):", "Temperature in Kelvin"));
	PiptNumberSpinner temperatureSpinner = new PiptNumberSpinner(blackbody, "temperature", 1000, 10000, 100);
	pane.add(temperatureSpinner);

	// Return the panel.
	return pane;
    }
}
