package org.wiyn.etc.inputSpectrum.gui;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.KC96GalaxyTemplate;
import org.wiyn.etc.inputSpectra.TargetSpectrum;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;
import za.ac.salt.pipt.common.gui.PiptStringComboBox;

public class KC96Panel extends SpectrumPanel {

    KC96GalaxyTemplate kc96 = null;
    PiptStringComboBox templateBox;
    private Component redshiftBox;

    // static {System.err.println (TemplateKeys);}
    public KC96Panel(GenericSpectrum spectrum, TargetSpectrum targetSpectrum) {
	super (spectrum, targetSpectrum);
	this.kc96 = (KC96GalaxyTemplate) spectrum;
    }

    public JPanel propertiesPanel (final GenericSpectrum spectrum) {
	// Create the panel with a flow layout (there will be one row only,
	// anyway).
	JPanel pane = new JPanel ();
	pane.setLayout (new FlowLayout ());

	// Is the supplied spectrum really that of a Kurucz model?
	if (!(spectrum instanceof KC96GalaxyTemplate)) {
	    (new Exception (
		    "The supplied spectrum is no KC96 galaxy tempalte spectrum."))
		    .printStackTrace ();
	    return pane;
	}

	// As we now know that we have a Kurucz model spectrum, we may perform a
	// respective cast.
	final KC96GalaxyTemplate kc96Model = (KC96GalaxyTemplate) spectrum;

	// Add the spinner for the temperature (and a respective label).
	pane.add (new PiptLabel ("Galaxy type:", ""));

	this.templateBox = new PiptStringComboBox (kc96Model, "galaxyType",
		kc96Model.getTemplateKeys (), PiptStringComboBox.ASCENDING_ORDER);
	templateBox.setEditable (false);

	pane.add (templateBox);

	// Add the combo box for the gravity values (and a respective label) */
	pane.add (new PiptLabel ("z:", "The redshift of the object"));
	redshiftBox = new PiptNumberSpinner (kc96Model, "redshift", 0, 10, 0.1);

	pane.add (redshiftBox);

	return pane;
    }

}
