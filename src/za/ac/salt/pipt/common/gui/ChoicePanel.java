package za.ac.salt.pipt.common.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import za.ac.salt.pipt.common.dataExchange.PiptData;


/** This class provides the framework for implementing a panel covering the content of a (XML schema) choice. For each possible item a respective ChoiceItemPanel implementation must be provided. The class adds radio buttons in order to allow their selection. It is ensured that the state of these panels (i.e. whether their GUI elements are active) and the data model content are consistent. If an item is selected, the elements corresponding to the other items must be set to null. This is carried out by a listener using the respective method of the ChoiceItemPanel class. */
public class ChoicePanel extends JPanel
{
    /** the parent element, i.e. the element containing the choice */
    PiptData parent;

    /** the array of choice item panels */
    ChoiceItemPanel[] choiceItemPanels;

    /** the button group for this choice panel */
    private ButtonGroup choiceButtons;


    /** Creates the choice panel for the given array of panels describing the choice items.
     * @param parent the parent element, i.e. the element containing the choice
     * @param choiceItemPanels the panels describing the choice items */
    public ChoicePanel(PiptData parent, ChoiceItemPanel[] choiceItemPanels)
    {
	// Set the internal variables for the parent element and the array of
	// choice item panels.
	this.parent = parent;
	this.choiceItemPanels = choiceItemPanels;

	// Loop over all choice item panels. For each panel, a radio button is
	// created, and both the button and the panel are added to the panel.
	// In order to keep track of changes, a respective property change
	// listener is added to the parent element (i.e. the element containing
	// the choice) for each button. In addition, an action listener for
	// selecting or unselecting a choice item is added throughout.
	choiceButtons = new ButtonGroup();
	setLayout(new GridBagLayout());
	GridBagConstraints constraints = new GridBagConstraints();
	for (int i = 0; i < choiceItemPanels.length; i++) {
	    ChoiceButton choiceButton = new ChoiceButton(choiceItemPanels[i], i);
	    choiceButtons.add(choiceButton);
	    constraints.gridx = 0;
	    constraints.gridy = i;
	    constraints.anchor = GridBagConstraints.NORTHWEST;
	    add(choiceButton, constraints);
	    constraints.gridx = 1;
	    add(choiceItemPanels[i], constraints);
	}
    }


    /** Enables or disables thje buttons of this choice panel.
     * @param enabled states whether to enable or disable the buttons */
    public void setEnabled(boolean enabled)
    {
	Enumeration<AbstractButton> buttons = choiceButtons.getElements();
	while (buttons.hasMoreElements()) {
	    buttons.nextElement().setEnabled(enabled);
	}
    }


    /** This class provides a radio button for selecting a choice item panel. It adds a property change listener to the parent element containing the choice, in order to keep track of its changes and update the button selection state accordingly. In addition, an action listener for selecting the choice item is added. */
    private class ChoiceButton extends JRadioButton
    {
	/** the choice item panel associated with this button */
	private ChoiceItemPanel choiceItemPanel;

	/** the id of the associated choice item panel, i.e. its index in the array of choice item panels */
	private int id;


	/** Sets the id variable and adds the listeners. The id is just the index of the choice item panel associated with this button. The listeners ensure that the selection state of the button remains consistent with the choice item chosen and that selecting a button leads to the selection of the associated choice item (panel).
	 * @param choiceItemPanel the choice item panel associated with this button
	 * @param id the id of the choice item panel*/
	public ChoiceButton(ChoiceItemPanel choiceItemPanel, int id)
	{
	    // Create the radio button with the choice item name as its label.
	    super(choiceItemPanel.getChoiceItemName() + ": ");

	    // Set the internal variables for the id and the choice item panel.
	    this.choiceItemPanel = choiceItemPanel;
	    this.id = id;

	    // Ensure that the initial selection state of the button is correct.
	    if (choiceItemPanel.isChosen()) {
		setSelected(true);
	    }
	    else {
		setSelected(false);
	    }

	    // Add the listener for updating the selection state of this button
	    // to the element containing the choice.
	    parent.addPropertyChangeListener(new PropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent event)
		    {
			if (ChoiceButton.this.choiceItemPanel.isChosen()) {
			    setSelected(true);
			}
			else {
			    setSelected(false);
			}
		    }
		});

	    // Add the listener for choosing the choice item panel associated
	    // with this button.
	    addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event)
		    {
			// Loop over all choice item panels, selecting and
			// unselecting them as we go.
			for (int i = 0; i < choiceItemPanels.length; i++) {
			    // If the counter differs from the id, we have to
			    // unselect the choice item panel for this index.
			    // Otherwise we have to select it.
			    if (i != ChoiceButton.this.id) {
				choiceItemPanels[i].unselect();
			    }
			    else {
				choiceItemPanels[i].select();
			    }
			    choiceItemPanels[i].update();
			}
		    }
		});
	}
    }
}
