package org.wiyn.etc.inputSpectrum.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.wiyn.etc.inputSpectra.NormalizedKuruczModel;
import org.wiyn.etc.inputSpectra.NormalizedSpectrum;
import org.wiyn.etc.inputSpectra.TargetSpectrum;

import za.ac.salt.pipt.common.GenericSpectrum;

/**
 * The panel for entering the relevant data for some spectrum can be divided in
 * three parts. First, there is the checkbox for deciding whether to include the
 * spectrum in the target spectrum calculation. Second, there is a spinner for
 * setting the V magnitude. And third, there are various input fields for
 * supplying the properties of the spectrum in question. This class provides the
 * generic framework for these panels. In particular, it implements the checkbox
 * and its functionality, as well as the spinner for setting the V magnitude.
 */
public abstract class SpectrumPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -3482447443141673721L;

    /** the default V magnitude for non-V magnitude normalized spectra */
    public static final double DEFAULT_V_MAGNITUDE = 20;

    /**
     * Creates the panel for the given spectrum.
     * 
     * @param spectrum
     *            the spectrum associated with this panel
     * @param targetSpectrum
     *            the target spectrum
     */

    Border myBorder = null;

    public SpectrumPanel(final GenericSpectrum spectrum,
	    final TargetSpectrum targetSpectrum) {
	// We use a flow layout for this panel, as everything should fit into a
	// single row. The content is left-justified.
	setLayout (new FlowLayout (FlowLayout.LEFT));

	// Add a border with the name of the spectrum (at the default position).
	myBorder = BorderFactory.createEmptyBorder ();
	Border titledBorder = BorderFactory.createTitledBorder (null, spectrum
		.name ());
	setBorder (titledBorder);

	// Add the checkbox for including (or excluding) the spectrum in the
	// calculation of the target spectrum.
	JCheckBox inclusionCheckBox = new JCheckBox ("Use?");
	inclusionCheckBox.addActionListener (new ActionListener () {
	    public void actionPerformed (ActionEvent event) {
		JCheckBox checkBox = (JCheckBox) event.getSource ();
		if (checkBox.isSelected ()) {
		    targetSpectrum.add (spectrum);
		    setSelected (true);
		} else {
		    targetSpectrum.remove (spectrum);
		    setSelected (false);
		}
	    }
	});
	inclusionCheckBox
		.setToolTipText ("Check this to include this spectrum");
	add (inclusionCheckBox);

	// Add the spinner for the V magnitude (and a respective label). If the
	// spectrum associated with this panel isn't normalized to match a V
	// magnitude, there is no need for the spinner, and hence we choose some
	// default values and render the spinner uneditable. Note that we cannot
	// use the PiptNumberSpinner class in this case, as the getVMagnitude()
	// method is missing.
	boolean spectrumIsVNormalized = spectrum instanceof NormalizedSpectrum ? true
		: false;

	if (spectrumIsVNormalized) {
	    FluxNormalizationPanel MagPanel = new FluxNormalizationPanel (
		    ((NormalizedSpectrum) spectrum).getMagSystem (),
		    (NormalizedSpectrum) spectrum);
	   
	    add (MagPanel);
	}
	// Add the panel for setting the various properties of the spectrum.
	// This must be provided by the subclass.
	JPanel p = propertiesPanel (spectrum);
	if (p != null)
	    add (propertiesPanel (spectrum));
	setSelected (false);
    }

    private void setSelected (boolean isSelected) {
	if (isSelected) {
	    setColor (selectedColor);
	} else {
	    setColor (deselectedColor);
	}

    }

    final static Color selectedColor = new Color (0.9f, 1.0f, 0.9f);
    final static Color deselectedColor = new Color (0.9f, 0.9f, 0.8f);

    private void setColor (Color c) {
	this.setBackground (c);
	Component[] childs = this.getComponents ();
	for (int ii = 0; ii < childs.length; ii++) {
	    childs[ii].setBackground (c);
	}
    }

    /**
     * Returns the panel used for setting the various spectrum properties. This
     * method must be implemented by the subclass.
     * 
     * @param spectrum
     *            the spectrum the properties are set in the panel
     * @return the panel used for setting the various spectrum properties
     */
    public abstract JPanel propertiesPanel (GenericSpectrum spectrum);

    /** Simple test method. Delete. */
    public static void main (String[] argv) {
	JFrame frame = new JFrame ("Test");
	JPanel contentPane = new JPanel ();
	NormalizedKuruczModel nbb = new NormalizedKuruczModel (12000, 3.33,
		2, 11);
	TargetSpectrum ts = new TargetSpectrum ();
	contentPane.add (new KuruczModelPanel (nbb, ts));
	frame.setContentPane (contentPane);
	frame.pack ();
	frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	frame.setVisible (true);
    }
}
