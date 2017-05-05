package za.ac.salt.pipt.common.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;


/** In case an element may appear more than once (i.e., more technically, in case an element has a maxOccurs attribute with a value greater than 1), the JAXB data model represents this element by means of a List object. This class provides the GUI for dealing with such a list. It provides buttons for inserting and removing list items and ensures that the GUI for each item is shown. It also checks that the minimum and maximum numbers of list items are satisfied. For convenience, all the GUI is put in a scroll pane. */
public abstract class PiptListPanel extends JPanel
{
    /** the element containing the element list associated with this panel */
    protected PiptData parent;

    /** the element list associated with this panel */
    private java.util.List elementList;

    /** the minimum number of occurences of the element, i.e. the minimum number of items in the element list */
    private int minOccurs;

    /** the maximum number of occurences of the element, i.e. the maximum number of items in the element list */
    private int maxOccurs;

    /** the list of the panels for describing the elements in the element list */
    private java.util.List elementPanels;

    /** the list of buttons for inserting an element */
    private java.util.List addElementButtons;

    /** the list of buttons for removing an element */
    private java.util.List removeElementButtons;

    /** the scroll pane for the content */
    private PiptScrollPane contentScrollPane;

    /** states whether the GUI components should be active */
    private boolean renderActive = true;


    /** Creates the GUI for dealing with the element list. The list as well as the minimum and maximum number of occurences must be specified. All the GUI is put into a scroll pane, the size of which must be specified.
     * @param parent the parent element containing the element list associated with this panel
     * @param elementList the element list associated with this panel
     * @param minOccurs the minimum occurence of the element
     * @param maxOccurs the maximum occurence of the element
     * @param scrollPaneSize the scroll pane size */
    public PiptListPanel(PiptData parent, java.util.List elementList, int minOccurs, int maxOccurs, Dimension scrollPaneSize)
    {
	// Set the internal variables.
	this.parent = parent;
	this.elementList = elementList;
	this.minOccurs = minOccurs;
	this.maxOccurs = maxOccurs;

	// While this shouldn't happen, we should first check whether perhaps
	// the given list is longer than allowed.
	if (elementList.size() > maxOccurs) {
	    throw new InvalidValueException("The list contains more elements than allowed.");
	}

	// We need a scroll pane for the content.
	contentScrollPane = new PiptScrollPane();
	contentScrollPane.setPreferredSize(scrollPaneSize);
	add(contentScrollPane);

	// If the list has less than the minimum number of elements, we add the
	// required number of elements.
	for (int i = elementList.size(); i < minOccurs; i++) {
	    addElement(i, false);
	}

	// We may now create all the GUI stuff for the content pane.
	update();

	// In case the parent element undergoes any change, this panel must be
	// updated.
	parent.addPropertyChangeListener(new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event)
		{
		    update();
		}
	    });
    }


    /** Updates the panel and the list of element panels so that they are consistent with the element list. */
    private void update()
    {
	// Create the content pane with a grid bag layout.
	JPanel contentPane = new JPanel();
	contentPane.setLayout(new GridBagLayout());
	GridBagConstraints constraints = new GridBagConstraints();

	// Start anew with the GUI component lists.
	elementPanels = new ArrayList();
	addElementButtons = new ArrayList();
	removeElementButtons = new ArrayList();

	// Add all the GUI stuff to the content pane. The GUI components are
	// added to the respective lists as well.
	constraints.anchor = GridBagConstraints.WEST;
	constraints.insets = new Insets(5, 5, 5, 5);
	constraints.gridx = 1;
	constraints.gridy = 0;
	AddElementButton addElementButton = new AddElementButton(0);
	addElementButtons.add(addElementButton);
	contentPane.add(addElementButton, constraints);
	for (int i = 0; i < elementList.size(); i++) {
	    constraints.gridx = 0;
	    constraints.gridy = 2 * i + 1;
	    RemoveElementButton removeElementButton = new RemoveElementButton(i); 
	    removeElementButtons.add(removeElementButton);
 	    contentPane.add(removeElementButton, constraints);
	    constraints.gridx = 1;
	    PiptListItemPanel elementPanel = createElementPanel(i);
	    elementPanels.add(elementPanel);
	    contentPane.add(elementPanel, constraints);
	    constraints.gridy++;
	    addElementButton = new AddElementButton(i + 1);
	    addElementButtons.add(addElementButton);
	    contentPane.add(addElementButton, constraints);
	}

	// Render the GUI components active or inactive.
	setActive();

	// Put the GUI into the scroll panel.
 	contentScrollPane.setViewportView(contentPane);
    }


    /** Adds a new element at the given index to the element list and ensures that the panel for setting its value is created as well. An update is made afterwards.
     * @param index the index where to add the element (which must lie between 0 and the size of the element list) */
    private void addElement(int index)
    {
	addElement(index, true);
    }


    /** Adds a new element at the given index to the element list and ensures that the panel for setting its value is created as well. The user has to specify whether an update should be made after adding the element.
     * @param index the index where to add the element (which must lie between 0 and the size of the element list)
     * @param updateRequested states whether an update should be carried out after adding the element */
    private void addElement(int index, boolean updateRequested)
    {
	// If the index lies outside the allowed range, we inform the user and
	// do nothing. (But this shouldn't happen!)
	if (index < 0 || index > elementList.size()) {
	    ErrorLog.indexOutOfRangeMessage(this, index, 0, maxOccurs);
	}

	// Create the new element.
	Object newElement = createElement(index);

	// Add the created element to the respective list.
	addElementToList(index, newElement);

	// The addElementToList() ought to throw a property change event and
	// hence the property change listener should ensure that an update is
	// made. But as this isn't under full control of this class, we adopt
	// a pessimistic point of view and update (again), unless the user has
	// requested otherwise.
	if (updateRequested) {
	    update();
	}
    }


    /** Removes the element at the given index from the element list and ensures that the panel for describing it is shown no longer.
     * @param index of the element which is to be removed */
    private void removeElement(int index)
    {
	// If the index lies outside the allowed range, we inform the user and
	// do nothing. (But this shouldn't happen!)
	if (index < 0 || index >= elementList.size()) {
	    ErrorLog.indexOutOfRangeMessage(this, index, 0, elementList.size()-1);
	}

	// Remove the element.
	removeElementFromList(index);

	// The removeElementFromList() ought to throw a property change event
	// and hence the property change listener should ensure that an update
	// is made. But as this isn't under full control of this class, we adopt
	// a pessimistic point of view and update (again), unless the user has
	// requested otherwise.
	update();
    }


    /** Renders the GUI components active or inactive, preserving the (previous) state. This method calls the setActive() method with the respective internal variable. */
    private void setActive()
    {
	setActive(renderActive);
    }


    /** Renders the GUI components active or inactive, depending on the value of the respective parameter. Buttons that ought to be disabled because of occurence constraints remain so. The state (active or inactive) is recorded and is used if the setActive() is called with no parameter. */
    public void setActive(boolean renderActive)
    {
	// Record the requested state (active or inactive).
	this.renderActive = renderActive;

	// Renders all the GUI components active or inactive, according to the
	// given value.
	for (int i = 0; i < elementPanels.size(); i++) {
	    ((PiptListItemPanel) elementPanels.get(i)).setActive(renderActive);
	}
	for (int i = 0; i < addElementButtons.size(); i++) {
	    ((AddElementButton) addElementButtons.get(i)).setEnabled(renderActive);
	}
	for (int i = 0; i < removeElementButtons.size(); i++) {
	    ((RemoveElementButton) removeElementButtons.get(i)).setEnabled(renderActive);
	}
	     
	// If (but only if) the list already contains the maximum number of
	// elements, we mustn't allow the addition of any further element.
	if (elementList.size() >= maxOccurs) {
	    for (int i = 0; i < addElementButtons.size(); i++) {
	        ((AddElementButton) addElementButtons.get(i)).setEnabled(false);
	    }
	}

	// If (but only if) the list contains exactly the minimum allowed number
	// of elements (or less), we mustn't allow the removal of an element.
	if (elementList.size() <= minOccurs) {
	    for (int i = 0; i < removeElementButtons.size(); i++) {
	        ((RemoveElementButton) removeElementButtons.get(i)).setEnabled(false);
	    }
	}
    }


    /** Adds the given element at the given index of the element list. This method must be overridden by the subclass. Remember that a property change ought to be fired. Hence if we are considering a list of <Wavelength> elements, say, the overriding method probably should contain the line<br><br>
<code>parent.addWavelength(element, index);</code><br><br>
where the <code>parent</code> must be cast to the correct type.
     * @param index the index where to add the element
     * @param element the element to be added */
    protected abstract void addElementToList(int index, Object element);


    /** Removes the element at the given index from the element list. This method must be overridden by the subclass. Remember that a property change ought to be fired. Hence if we are considering a list of <Wavelength> elements, say, the overriding method probably should contain the line<br><br>
<code>parent.removeWavelength(index);</code><br><br>
where the <code>parent</code> must be cast to the correct type.
     * @param index the index of the element to be removed */
    protected abstract void removeElementFromList(int index);


    /** Returns a new element with the given index in the element list. Default values should be provided for the element content in order to guarantee that it is valid according to the PIPT schema. If this isn't done, the PiptListItemPanel describing the element must carry out the task.<br>
In general, the provided index value is irrelevant. It is provided just in case that the type of the new element might depend on its position in the element list.
     * @param index the index of the new element in the element list
     * @return the new element for the element list */
    protected abstract Object createElement(int index);


    /** Returns the panel for changing the element with the given index in the element list.
     * @param index the index of the element described by the returned panel */
    protected abstract PiptListItemPanel createElementPanel(int index);


    /** This class provides a button for inserting a new element into the element list. */
    private class AddElementButton extends JButton
    {
	/** Creates the button for adding a new element at the given index into the element list.
	 * @param index the index where to add the new element */
	public AddElementButton(final int index)
	{
	    // Create the button.
	    super("Insert here");

	    // Add the listener for inserting a new element.
	    addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event)
		    {
			addElement(index);
		    }
		});
	}
    }


    /** This class provides a button for removing an element from the element list. */
    private class RemoveElementButton extends JButton
    {
	/** Creates the button for removing the element at the given index from the element list.
	 * @param index the index of the element to be removed */
	public RemoveElementButton(final int index)
	{
	    // Create the button.
	    super("Remove");

	    // Add the listener for removing the element.
	    addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event)
		    {
			removeElement(index);
		    }
		});
	}
    }
}
