package org.wiyn.etc.inputSpectrum.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.FluxNormalizationDescriptor;
import org.wiyn.etc.inputSpectra.NormalizedKuruczModel;
import org.wiyn.etc.inputSpectra.NormalizedSpectrum;

import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;

public class FluxNormalizationPanel extends JPanel implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 9008253346361309403L;
    FluxNormalizationDescriptor myMagSystem;
    NormalizedSpectrum mySpectrum;

    PiptNumberSpinner refMagSpinner = null;
    JComboBox RefSystemBox = null;

    public FluxNormalizationPanel(FluxNormalizationDescriptor MagSystem,
	    NormalizedSpectrum spectrum) {

	myMagSystem = MagSystem;
	mySpectrum = spectrum;

	refMagSpinner = new PiptNumberSpinner ((PiptData) spectrum,
		"Magnitude",
		NormalizedSpectrum.DEFAULT_MINIMUM_ALLOWED_V_MAGNITUDE,
		NormalizedSpectrum.DEFAULT_MAXIMUM_ALLOWED_V_MAGNITUDE, 0.1);

	//RefSystemBox = new JComboBox (FluxNormalizationDescriptor.FluxModes);
	RefSystemBox = new JComboBox (spectrum.getFluxModes());
	RefSystemBox.setSelectedIndex (0);
	RefSystemBox.addActionListener (this);

	setLayout (new FlowLayout (FlowLayout.LEFT));
	this.add (RefSystemBox);
	this.add (refMagSpinner);

    }

    public static void main (String[] param) {

	FluxNormalizationDescriptor d = new FluxNormalizationDescriptor (
		FluxNormalizationDescriptor.FluxModes[0]);

	FluxNormalizationPanel p = new FluxNormalizationPanel (d,
		new NormalizedKuruczModel (3000, 0, 0, 20));

	JFrame j = new JFrame ("FluxNormalizationPanel Test App");
	j.getContentPane ().add (p);
	j.pack ();
	j.setVisible (true);

    }

    public void actionPerformed (ActionEvent e) {
	// TODO Auto-generated method stub

	if (e.getSource () == this.RefSystemBox) {
	    if (this.myMagSystem != null) {
		System.err.println ("Change Reference Mag system to:"
			+ (FluxNormalizationDescriptor) ((JComboBox) e
				.getSource ()).getSelectedItem ());

		this.mySpectrum
			.setMagSystem ((FluxNormalizationDescriptor) ((JComboBox) e
				.getSource ()).getSelectedItem ());
	    }

	}
    }
}
