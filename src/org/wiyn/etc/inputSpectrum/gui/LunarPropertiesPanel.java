package org.wiyn.etc.inputSpectrum.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.wiyn.etc.configuration.LunarProperties;

import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberSpinner;

/** This class provides the panel for setting the lunar properties. */
public class LunarPropertiesPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** denotes the choice of a dark moon condition */
    private static final String DARK_MOON_CHOICE = "Dark";

    /** denotes the choice of a grey moon condition */
    private static final String GREY_MOON_CHOICE = "Grey";

    /** denotes the choice of a bright moon condition */
    private static final String BRIGHT_MOON_CHOICE = "Bright";

    /** denotes the choice of a manually set moon condition */
    private static final String MANUAL_MOON_CHOICE = "Manual";

    /** the list of moon conditions offered to the user */
    private static final String[] MOON_CHOICES_LIST = { DARK_MOON_CHOICE,
	    GREY_MOON_CHOICE, BRIGHT_MOON_CHOICE, MANUAL_MOON_CHOICE };

    /** the hashtable for the quick select choices */
    private static final Hashtable<String, double[]> MOON_CHOICES;

    /** the combo box for making a quick selection of moon conditions */
    private JComboBox quickSelectComboBox;

    /** the spinner for the zenith distance of the moon */
    private PiptNumberSpinner zenithDistanceSpinner;

    /** the spinner for the lunar phase */
    private PiptNumberSpinner phaseSpinner;

    /** the spinner for the lunar elongation */
    private PiptNumberSpinner elongationSpinner;

    /** the lunar properties associated with this panel */
    private LunarProperties lunarProperties;

    double AAA;

    // Initialize the hashtable for the moon condition choices. The keys are
    // the strings appearing in the quick select combo box; the corresponding
    // values are double arrays of the form {zenith distance of the moon,
    // lunar phase, lunar elongation}. An empty array should be chosen, if there
    // are no canonical values. (Otherwise the code might break in the
    // makeQuickSelectChoice() method.)
    static {
	MOON_CHOICES = new Hashtable<String, double[]> (4);
	MOON_CHOICES.put (DARK_MOON_CHOICE, new double[] { 180, 180, 180 });
	MOON_CHOICES.put (GREY_MOON_CHOICE, new double[] { 75, 90, 90 });
	MOON_CHOICES.put (BRIGHT_MOON_CHOICE, new double[] { 20, 10, 75 });
	// MOON_CHOICES.put (MANUAL_MOON_CHOICE, new double[] {});
    }

    /**
     * Creates the panel for the given lunar properties.
     * 
     * @param lunarProperties
     *            the lunar properties
     */
    public LunarPropertiesPanel(final LunarProperties lunarProperties) {
	// Create the panel. As there will be only one row anyway, we choose a
	// flow layout. The content is left-justified.
	super ();
	// Set the internal variable for the lunar properties.
	this.lunarProperties = lunarProperties;

	setLayout (new BoxLayout (this, BoxLayout.X_AXIS));

	// Adds a combo box for making a quick select of the lunar properties
	// (and a respective label). The selected value is that for a manual
	// setting.
	this.add (new PiptLabel ("Moon:", "Choose a default moon condition"));
	quickSelectComboBox = new JComboBox (MOON_CHOICES_LIST);
	quickSelectComboBox.addActionListener (new ActionListener () {
	    public void actionPerformed (ActionEvent event) {
		makeQuickSelectChoice ();
	    }
	});

	lunarProperties
		.addPropertyChangeListener (new QuickSelectPropertyChangeListener ());
	this.add (Box.createHorizontalStrut (20));
	this.add (quickSelectComboBox);
	this.add (Box.createHorizontalStrut (20));
	// Add the spinner for the zenith distance of the moon (and a respective
	// label).
	// add(new PiptLabel("Moon ZD:", "Is the moon up?"));
	zenithDistanceSpinner = new PiptNumberSpinner (lunarProperties,
		"moonZenithDistance", 0, 180, 1);
	// add(zenithDistanceSpinner);

	// Add the spinner for the lunar phase (and a respective label).
	// add(new PiptLabel("Lunar Phase:", "How bright is the moon?"));
	phaseSpinner = new PiptNumberSpinner (lunarProperties, "lunarPhase", 0,
		180, 1);
	// add(phaseSpinner);

	// Add the spinner for the lunar elongation (and a respective label).
	// add(new PiptLabel("Lunar Elongation:", "How far is it from the
	// target?"));
	elongationSpinner = new PiptNumberSpinner (lunarProperties,
		"lunarElongation", 0, 180, 1);
	// add(elongationSpinner);

	// Set the selected item of the quick select combo box.
	setQuickSelectComboBox ();
    }

    /**
     * Checks whether the lunar properties match any of the preset moon
     * conditions. If this is found to be the case, the corresponding quick
     * select combo box item is selected. Otherwise the manual choice is adopted
     * as the selected item.
     */
    private void setQuickSelectComboBox () {
	// Check for all preset moon conditions whether they match the
	// current values of the lunar properties. If a match is found,
	// select the corresponding item of the quick select combo box.
	Enumeration<String> moonConditions = MOON_CHOICES.keys ();
	String selectedItem = null;
	while (moonConditions.hasMoreElements ()) {
	    String moonCondition = (String) moonConditions.nextElement ();
	    double[] properties = (double[]) MOON_CHOICES.get (moonCondition);
	    ;

	    // We are interested in an element of MOON_CHOICES only if its
	    // value actually contains values for all the lunar properties.
	    if (properties.length >= 3) {
		if (lunarProperties.getMoonZenithDistance () == properties[0]
			&& lunarProperties.getLunarPhase () == properties[1]
			&& lunarProperties.getLunarElongation () == properties[2]) {
		    selectedItem = moonCondition;
		}
	    }
	}

	// If the variable for the selected item is still null, the lunar
	// properties don't match any of the preset moon conditions.
	if (selectedItem == null) {
	    selectedItem = MANUAL_MOON_CHOICE;
	}

	// Set the selected item of the quick select combo box.
	quickSelectComboBox.setSelectedItem (selectedItem);
    }

    /**
     * Makes the choice of moon conditions requested by the user. If either of
     * the preset conditions (bright, grey or dark) has been chosen, the
     * respective values are assigned to the lunar properties and the
     * corresponding spinners. If, on the other hand, the manual choice has been
     * requested, nothing needs to be done.
     */
    private void makeQuickSelectChoice () {
	// Figure out the choice and take respective action.
	Object choice = quickSelectComboBox.getSelectedItem ();
	Object value = MOON_CHOICES.get (choice);

	// If the value is a double array with at least three entries, it ought
	// to contain the lunar properties.
	if (value instanceof double[] && ((double[]) value).length >= 3) {
	    double[] properties = (double[]) value;
	    lunarProperties.setMoonZenithDistance (properties[0]);
	    lunarProperties.setLunarPhase (properties[1]);
	    lunarProperties.setLunarElongation (properties[2]);
	    zenithDistanceSpinner.setValue (new Double (properties[0]));
	    phaseSpinner.setValue (new Double (properties[1]));
	    elongationSpinner.setValue (new Double (properties[2]));
	}

	// Changes in the spinner models imply that the selected item of the
	// combo box is set to the manual choice. Hence we have to ensure that
	// it is reset to the correct value.
	quickSelectComboBox.setSelectedItem (choice);
    }

    /**
     * This class constitutes the property change listener for the quick select
     * box for choosing moon conditions. It ensures that the value shown in the
     * combo box is consistent with the lunar properties.
     */
    private class QuickSelectPropertyChangeListener implements
	    PropertyChangeListener {
	/**
	 * Checks whether the current lunar properties match either of the quick
	 * select choices. If this is indeed the case, the respective choice is
	 * chosen as the selected item of the quick select combo box. Otherwise,
	 * the manual choice is taken.
	 * 
	 * @param event
	 *            the property change event
	 */
	public void propertyChange (PropertyChangeEvent event) {
	    setQuickSelectComboBox ();
	}
    }
}
