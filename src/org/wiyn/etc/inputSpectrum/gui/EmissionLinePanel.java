package org.wiyn.etc.inputSpectrum.gui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.EmissionLine;
import org.wiyn.etc.inputSpectra.TargetSpectrum;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;
import za.ac.salt.pipt.common.gui.PiptNumberTextField;



/** This class provides a panel for including (or excluding) an emission line in the target spectrum calculation and for setting its properties. */
public class EmissionLinePanel extends SpectrumPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Creates the panel for the given emission line spectrum.
     * @param emissionLine the emission line spectrum associated with this panel
     * @param targetSpectrum the target spectrum */
    public EmissionLinePanel(EmissionLine emissionLine, TargetSpectrum targetSpectrum)
    {
	super(emissionLine, targetSpectrum);
    }


    /** Returns the panel for setting the properties of the emission line. The emission line spectrum must be supplied. If the supplied spectrum happens to be no emission line spectrum,  the stack trace is output and an empty panel is returned.
     * @param spectrum the spectrum (must be of the type EmissionLine)
     * @return the panel for setting the properties of the given Kurucz model spectrum */
    public JPanel propertiesPanel(final GenericSpectrum spectrum)
    {
	// Create the panel with a flow layout (there will be one row only,
	// anyway).
	JPanel pane = new JPanel();
	pane.setLayout(new FlowLayout());

	// Is the supplied spectrum really that of an emission line?
	if (!(spectrum instanceof EmissionLine)) {
	    (new Exception("The supplied spectrum is no emission line spectrum.")).printStackTrace();
	    return pane;
	}

	// As we now know that we have an emission line spectrum, we may
	// perform a respective cast.
	final EmissionLine emissionLine = (EmissionLine) spectrum;

	// Add the spinner for the central wavelength (and a respective label).
	pane.add(new PiptLabel("Wavelength (A):", "Central wavelength in Angstrom"));
	PiptNumberSpinner wavelengthSpinner = new PiptNumberSpinner(emissionLine, "centralWavelength", 3140, 10100, 1);
	pane.add(wavelengthSpinner);

	// Add the spinner for the FWHM (and a respective label).
	pane.add(new PiptLabel("FWHM (A)", "The Full Width at Half Maximum of the emission line (in Angstrom)"));
	PiptNumberSpinner fwhmSpinner = new PiptNumberSpinner(emissionLine, "FWHM", 0, 100, 0.1);
	pane.add(fwhmSpinner);

	// Add the text field for the flux in the line (and a respective label).
	pane.add(new PiptLabel("Flux:", "The flux in the line, in erg/cm^2/s/A"));
	PiptNumberTextField fluxTextField = new PiptNumberTextField(emissionLine, "totalFlux");
	pane.add(fluxTextField);

	// Return the panel.
	return pane;
    }
}

	
