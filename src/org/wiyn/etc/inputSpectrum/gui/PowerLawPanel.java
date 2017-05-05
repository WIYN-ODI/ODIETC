package org.wiyn.etc.inputSpectrum.gui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.PowerLaw;
import org.wiyn.etc.inputSpectra.TargetSpectrum;
import org.wiyn.etc.inputSpectra.VNormalizedPowerLaw;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;


/** This class provides a panel for including (or excluding) a power law spectrum in the target spectrum calculation and for setting its index. The power law spectrum is normalized to match a V magnitude. */
public class PowerLawPanel extends SpectrumPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Creates the panel for the given power law spectrum.
     * @param normalizedPowerLaw the V magnitude normalized power law spectrum associated with this panel
     * @param targetSpectrum the target spectrum */
    public PowerLawPanel(VNormalizedPowerLaw normalizedPowerLaw, TargetSpectrum targetSpectrum)
    {
	super(normalizedPowerLaw, targetSpectrum);
    }


    /** Returns the panel for setting the index of the power law. The power law spectrum must be supplied. If the supplied spectrum happens to be no power law spectrum, the stack trace is output and an empty panel is returned.
     * @param spectrum the spectrum (must be of the type PowerLaw)
     * @return the panel for setting the index of the given power law spectrum */
    public JPanel propertiesPanel(final GenericSpectrum spectrum)
    {
	// Create the panel with a flow layout (there will be one row only,
	// anyway).
	JPanel pane = new JPanel();
	pane.setLayout(new FlowLayout());

	// Is the supplied spectrum really that of a power law?
	if (!(spectrum instanceof PowerLaw)) {
	    (new Exception("The supplied spectrum is no power law spectrum.")).printStackTrace();
	    return pane;
	}

	// As we now know that we have a power law spectrum, we may perform a
	// respective cast.
	final PowerLaw powerLaw = (PowerLaw) spectrum;

	// Add the spinner for the index (and a respective label).
        pane.add(new PiptLabel("Index:", "The flux is F(lambda) = lambda**k"));
	PiptNumberSpinner indexSpinner = new PiptNumberSpinner(powerLaw, "index", -10, 10, 0.1);
	pane.add(indexSpinner);

	// Return the panel.
	return pane;
    }
}
