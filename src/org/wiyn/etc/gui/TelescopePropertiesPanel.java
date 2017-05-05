package org.wiyn.etc.gui;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.wiyn.etc.configuration.TelescopeProperties;

import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberComboBox;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;

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
/** This class provides the panel for setting the telescope properties. */
public class TelescopePropertiesPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** the telescope properties */
    private TelescopeProperties telescopeProperties;

    /** the label for the seeing FWHM */
    private JLabel fwhmLabel;

    /**
     * Creates the panel for the given telescope properties.
     * 
     * @param telescopeProperties
     *            the telescope properties
     */
    public TelescopePropertiesPanel(
	    final TelescopeProperties telescopeProperties) {

	GridLayout myLayout = new GridLayout (3, 2);
	myLayout.setHgap (5);
	myLayout.setVgap (0);
	myLayout.setColumns (2);
	myLayout.setRows (3);
	setLayout (myLayout);

	// Sets the internal variable for the telescope properties.
	this.telescopeProperties = telescopeProperties;

	{

	    add (new PiptLabel ("Airmass :", "How many airmasses?"));
	    PiptNumberSpinner zenithDistanceSpinner = new PiptNumberSpinner (
		    telescopeProperties, "airmass", 1.0, 2.5, 0.1);

	    add (zenithDistanceSpinner);

	}

	{

	    add (new PiptLabel ("Zenith seeing [\"] :",
		    "Seeing affects sky noise"));

	    JComponent seeingComboBox = new PiptNumberComboBox (
		    telescopeProperties, "seeing",
		    TelescopeProperties.defaultSeeingValues,
		    PiptNumberComboBox.ASCENDING_ORDER);
	    add (seeingComboBox);

	}

	// Add the spinner for the seeing (and a respective label)
	{

	    add (new PiptLabel ("Airmass degraded seeing:", ""));

	    fwhmLabel = new PiptLabel ();

	    fwhmLabel
		    .setToolTipText ("Includes degradation to the SALT zenith distance with the telescope PSF added in quadrature");
	    add (fwhmLabel);
	}

	telescopeProperties
		.addPropertyChangeListener (new PropertyChangeListener () {
		    public void propertyChange (PropertyChangeEvent event) {
			updateFWHMLabel ();
		    }
		});
	updateFWHMLabel ();

	setPreferredSize (getMinimumSize ());
	setMaximumSize (getMinimumSize ());
	this.setAlignmentY (TOP_ALIGNMENT);
    }

    DecimalFormat decimalFormat = new DecimalFormat ();
    {
	decimalFormat.setMinimumFractionDigits (2);
	decimalFormat.setMaximumFractionDigits (2);
    }

    /**
     * Updates the label for the seeing FWHM by using the current values of the
     * telescope properties. We enforce exactly two fraction digits.
     */
    private void updateFWHMLabel () {
	// Obtain the correct format.

	// Update the FWHM label.
	String formattedFWHM = decimalFormat.format (telescopeProperties
		.getFWHM ());
	fwhmLabel.setText (formattedFWHM);
    }

    public static void main (String[] args) {
	JComponent p = new TelescopePropertiesPanel (new TelescopeProperties ());
	JFrame j = new JFrame ();
	j.getContentPane ().add (p);
	j.pack ();
	j.setVisible (true);
    }
}
