package org.wiyn.etc.inputSpectrum.gui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JPanel;

import org.wiyn.etc.configuration.SpectrumGenerationData;
import org.wiyn.etc.inputSpectra.TargetSpectrum;

/** This class provides the panel for generating a target and a sky spectrum. */
public class SpectrumGenerationPane extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** the target spectrum */
    static TargetSpectrum targetSpectrum;

    /**
     * Creates the panel containing all the panels required for the spectrum
     * generation.
     * 
     * @param spectrumGenerationData
     *                the data required for the spectrum generation
     */
    public SpectrumGenerationPane(
	    final SpectrumGenerationData spectrumGenerationData) {
	// We use a border layout.
	setLayout (new BorderLayout ());

	// Extract the various objects from the spectrum generation data.
	SpectrumGenerationPane.targetSpectrum = spectrumGenerationData
		.getTargetSpectrum ();
	

	// Set the internal target spectrum variable.
	//SpectrumGenerationPane.targetSpectrum = targetSpectrum;

	// Create a Box for the panel content.
	Box box = Box.createVerticalBox ();

	box.add (new InputSpectrumSelection (targetSpectrum));
	
	add (box, BorderLayout.CENTER);

	// Add the button for displaying the spectra and magnitudes.
	
    }
}
