package za.ac.salt.pipt.common.gui;

import javax.swing.JPanel;

import za.ac.salt.pipt.common.dataExchange.PiptData;


/** The abstract ChoiceItemPanel describes and provides basic functionality for a panel used for setting an item included in a (XML schema) choice element. It provides a listener for updating the panel (in case the parent element containing the choice changes), the actual method for updating, a method for activating or deactivating the panel GUI elements, a method for determining whether the item has been chosen, a method for choosing the item and a method for setting all the item elements to null.<br>
From an implementation point of view, this class uses two different data models: First, there is the "real" model, which is the one which would be used for producing the XML output. Second, there is the "GUI" model, which has non-null elements only. If a new (non-null) value is assigned to a real element it is also assigned to the corresponding GUI model. However, if null is assigned to the real element, the GUI element isn't changed. Choosing a choice item implies that the respective real elements take the values of the GUI elements.<br>
IMPORTANT: This class and its implementations assume that an element in the choice is null (i.e. doesn't exist) if and only if the choice item isn't the chosen one. */
public abstract class ChoiceItemPanel extends JPanel
{
    /** the parent element for this panel */
    protected PiptData parent;


    /** Initializes the panel. A listener is added to ensure that the panel content is updated when changes are made to the parent element.
     * @param parent the parent element */
    protected ChoiceItemPanel(PiptData parent)
    {
	// Set the internal parent element variable
	this.parent = parent;
    }


    /** Updates the panel. To this end, first the GUI data model is updated. Then the panel GUI is changed accordingly. The latter is done only if the GUI can be updated already. */
    public void update()
    {
    }


    /** Returns an id for the choice item described by this panel. This must be the same for each instance of the respective class. A reasonable choice for the id might be the concatenation of the child element names.
     * @return an id for the choice item described by this panel */
    protected abstract String getChoiceItemId();


    /** Sets the state of the GUI components, i.e. renders them active or inactive, depending on whether this choice item described by this panel has been chosen. */
    protected abstract void setActive();


    /** Chooses the choice item described by this panel. This means that the GUI data model values are assigned to the corresponding real data model values. IMPORTANT: The real data model elements of the other choice item panels must be set to null and an update should be enforced. However, this isn't carried out by this method, so that the calling method has to perform these tasks. Note further that assigning the values one by one almost necessarily implies that the data model is invalid in between. Accordingly, when implementing this method, you must ensure that no validation is performed. */
    public abstract void select();


    /** Returns whether the choice item described by this panel has been chosen.
     * @return true if the choice item described by this panel has been chosen */
    public abstract boolean isChosen();


    /** Sets all the real data model element values to null. IMPORTANT: Assigning null to the elements one by one almost necessarily implies that the data model is invalid in between. Accordingly, when implementing this method, you must ensure that no validation is performed. An update should be enforced after unselecting, but this task must be carried out by the calling method. */
    public abstract void unselect();


    /** Returns the name of the choice item associated with this panel. This is intended to be used as an informal identifier in a GUI only, and hence shouldn't be expected to meet any formal requirements. */
    public abstract String getChoiceItemName();
}
