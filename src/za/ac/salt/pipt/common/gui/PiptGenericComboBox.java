package za.ac.salt.pipt.common.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each combo box associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action listener is called for as well.<br />
Now while these could be added by hand for each and every combo box, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action listener passes the current value to the associated PIPT quantity, and the property change listener updates the combo box state according to the new value of the associated quantity.<br />
This class and its extensions alleviate the developer from the burding of having to produce this code as much as possible by providing and adding all the required listeners.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code> and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br />
Before accepting some input, it is checked whether after unformatting it has the correct form. In addition, InvalidValueExceptions are duely caught and processed by the combo box.<br />
The combo box is editable by default. */
public abstract class PiptGenericComboBox extends JComboBox
{
    /** represents a null value */
    public static final String NOT_SET = "not set";

    /** denotes the fact that an array isn't ordered. */
    public static final int NO_ORDER = 0;

    /** denotes an ascending order */
    public static final int ASCENDING_ORDER = 1;

    /** denotes a descending order */
    public static final int DESCENDING_ORDER = 2;

    /** the object associated with this combo box */
    protected PiptData dataObject;

    /** the field of the data object associated with this combo box */
    protected String field;

    /** the elements of the combo box (possibly without the current value of the quantity associated with it) */
    protected String[] comboBoxValues;

    /** the order of the list elements (must be either of the values NO_ORDER, ASCENDING_ORDER or DESCENDING_ORDER) */
    protected int order;

    /** the action listener for this combo box */
    protected ActionListener actionListener;

    /** the property change listener for this combo box */
    protected PropertyChangeListener propertyChangeListener;

    /** states whether the customize() method is being processed */
    protected boolean isBeingCustomized = false;


    /** Initializes a generic JComboBox with which the supplied data object is associated. The combo box contains the given list of values and, if isn't part of that list already, the current value of the associated quantity. If the action and property change listeners provided by the getActionListener() and getPropertyChangeListener() methods are non-null, they are added to the combo box and the data object, respectively. When adding the current value to the list of combo box values, the specified order (ascending, descending or no order at all) is maintained. The current value of the associated quantity is chosen as the selected item of the combo box.
     * @param dataObject the data object associated with this combo box
     * @param field the data field associated with this combo box
     * @param comboBoxValues the list of combo box values (possibly without the current value of the associated field)
     * @param order the order of the list of combo box values (must be either of the values ASCENDING_ORDER, DESCENDING_ORDER or NO_ORDER) */
    protected PiptGenericComboBox(PiptData dataObject, String field, String[] comboBoxValues, int order)
    {
	// Set the internal variables. The combo box values need to contain the
	// NOT_SET value.
	this.dataObject = dataObject;
	this.field = field;
	this.comboBoxValues = new String[comboBoxValues.length ];
	for (int i = 0; i < comboBoxValues.length; i++) {
	    this.comboBoxValues[i] = comboBoxValues[i];
	}
	//this.comboBoxValues[comboBoxValues.length] = NOT_SET;
	this.order = order;

	// Create the combo box items.
	customize();

	// The combo box should be editable by default.
	setEditable(true);

	// Obtain the action and property change listeners for this combo box
	// and (if they are non-null) add them.
	actionListener = getActionListener();
	propertyChangeListener = getPropertyChangeListener();
	addActionListener();
	addPropertyChangeListener();
    }


    /** Adds the action listener for this combo box, if it is non-null. */
    private void addActionListener()
    {
	if (actionListener != null) {
	    addActionListener(actionListener);
	}
    }


    /** Removes the propertty change listener for this combo box, if it is non-null. */
    private void removeActionListener()
    {
	if (actionListener != null) {
	    removeActionListener(actionListener);
	}
    }


    /** Adds the property change listener for this combo box to the associated object, if it is non-null. */
    protected void addPropertyChangeListener()
    {
	if (propertyChangeListener != null) {
	    dataObject.addPropertyChangeListener(propertyChangeListener);
	}
    }


    /** Removes the property change listener for this combo box from the associated object, if it is non-null. */
    private void removePropertyChangeListener()
    {
	if (propertyChangeListener != null) {
	    dataObject.removePropertyChangeListener(propertyChangeListener);
	}
    }

    /** Returns an action listener which sets the value of associated data object to the value corresponding to the selected combo box item. If the selected item is invalid, inform the user by means of a message dialog and revert the selected item to that corresponding to the current value of the associated data object. */
    protected ActionListener getActionListener()
    {
	return new ActionListener() {
		public void actionPerformed(ActionEvent event)
		{
		    defaultActionListenerCode(event);
		}
	    };
    }


    /** Provides the functionality for the default action listener returned by the getActionListener() method. */
    public void defaultActionListenerCode(ActionEvent event)
    {
	// the current (formatted) value of the associated quantity
	String currentFormattedQuantityValue = format(getDataObjectValue());
	
	// Retrieve the combo box responsible for the event and its selected
	// item.
	PiptGenericComboBox comboBox = (PiptGenericComboBox) event.getSource();
	String selectedItem = (String) comboBox.getSelectedItem();
	
	// Check whether the selected item (if unformatted) has the correct
	// form. If this is not the case, inform the user, revert to the
	// (formatted) current value of the associated data object and return.
	if (!hasValidForm()) {
	    showInvalidFormMessage(comboBox, selectedItem);
	    setSelectedItem(currentFormattedQuantityValue);
	    return;
	}
	
	// Sets the value of the relevant data object. If during this process,
	// an InvalidValueException is thrown, inform the user and revert to the
	// current value of the associated object.
	try {
	    setDataObjectValue(unformat(selectedItem));
	}
	catch (InvalidValueException ive) {
	    ErrorLog.rejectedValueMessage(comboBox, getSelectedItem(), ive);
	    setSelectedItem(currentFormattedQuantityValue);
	}
    }


    /** Returns a property change listener which formats the value of the relevant data object field and chooses it as the selected item of the combo box. If necessary, the field value is added to the combo box values first. The code is executed only if the data field associated with this combo box has changed.
     * @return a property change listener for choosing the selected combo box item */
    protected PropertyChangeListener getPropertyChangeListener()
    {
	return new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event)
		{
		    if (event.getPropertyName().equals(field)) {
			customize();
		    }
		}
	    };
    }


    /** Creates the list of combo box items. First, the current value of the quantity associated with this combo box is obtained and added to the list of combo box numbers. That list then is used to create the list of combo box items (using the format() method). The item corresponding to the current value is chosen as the selected item.<br />
The code of this method is somewhat tricky and deserves some explanation: As it might well be that we have to add a new item, we construct the items anew. This means, however, that the value of the field associated with the combo box changes, so that the property change listener is invoked, which in turn calls this (i.e. the customize()) method again. Thus without taking precaution the program will be doomed for desaster.<br />
We employ two measures to get around this problem. First, we record whether the method is already being processed. If this is found to be the case, the current call is terminated. But, alas, there is further peril to be heeded: The natural attempt to create the combo box items anew would be to first remove the old and then create the new items. However, a null pointer exception would occur, if things were done this way round. Hence we first add the new and then remove the old items.<br />
Second, we temporarily remove the action and property change listeners prior to carrying out the customization. They are added back again at the moment. Hence, for a short time the combo box is insensitive to any changes. From a practical point of view, this shouldn't matter, though. Still, to minimize the risk, we disable the combo box for that time. (Hence the only thing that might happen is that it doesn't record a property change.)
Finally, it must be kept in mind that all the fiddling around with the combo box items is bound to change the value of the field associated with the combo box. Hence we ensure it is restored afterwards to its original value. */
    protected void customize()
    {
	// If another call to this method is being processed, nothing must be
	// done.
	if (isBeingCustomized) {
	    return;
	}

	// Disable the combo box, as the action listener will be removed in a
	// moment. In order to put back the combo box into its current state
	// (enabled or disabled), we have to record that state first.
	boolean currentlyEnabled = isEnabled();
 	setEnabled(false);

	// As it seems, the exceution of this method leads to calls to the
	// action and property change listeners. As we don't wan't this, we
	// temporarily remove the listeners. They will be added back again at
	// the end of this method.
	removeActionListener();
	removePropertyChangeListener();

	// Record the fact that the combo box is being customized.
	isBeingCustomized = true;

	// We'll need the current value of the associated data object field
	// throughout this method.
	String currentValue = getDataObjectValue();

	// Add the current value to the list of values, if necessary.
	addValue(currentValue);

	// We'll remove the current items of the combo box in a moment, and for
	// that task we have to know how many there are.
	int numberOfOldItems = getItemCount();

	// Add all the values to the combo box, using the format given by the
	// format() method.
	for (int i = 0; i < this.comboBoxValues.length; i++) {
	    addItem(format(this.comboBoxValues[i]));
	}

	// We have to get rid of the old values, as otherwise items would
	// appear twice in the combo box. (Note that the deletion of the first
	// item implies that the indices of the remaining items are decreased by
	// one. Hence we always have to remove the item with index 0.)
	for (int i = 0; i < numberOfOldItems; i++) {
 	    removeItemAt(0);
	}

	// Get the index of the current value in the list of combo box values.
	// If it can be retrieved successfully, select the corresponding item.
 	int currentValueIndex = findValue(currentValue);
	if (currentValueIndex != -1) {
	    setSelectedIndex(currentValueIndex);
	}

	// Add the action and property change listeners back again.
	addActionListener();
	addPropertyChangeListener();

	// Revert the combo box to its former state, as there is an action
	// listener again.
	setEnabled(currentlyEnabled);

	// Record the fact that the customization has finished.
	isBeingCustomized = false;
    }


    /** Adds the given value to the array of combo box values. The order of the array is maintained. If the value is contained in the array already (to a reasonable approximation), it isn't added. If something goes wrong, the current value is added to the back. In case the given array of combo box values is claimed to be unordered, the value is added to the back.
     * @param value the value rto be added to the array of combo box values */
    private void addValue(String value)
    {
	// We need a list rather than array for adding the new value.
	java.util.List list = null;

	// Insert the new value (if necessary). If something goes wrong, the
	// new value is added to the back.
	try {
	    // Check whether we really have to add the given value.
	    for (int i = 0; i < comboBoxValues.length; i++) {
		if (areEqual(value, comboBoxValues[i])) {
		    return;
		}
	    }
	    
	    // Unlikely though it may, we cannot rule out that an empty array
	    // has been provided. We better get rid of that case before
	    // proceeding.
	    if (comboBoxValues.length == 0) {
		comboBoxValues = new String[] {value};
		return;
	    }

	    // Construct a list of the original values and the new value.
	    list = new ArrayList();
	    for (int i = 0; i < comboBoxValues.length; i++) {
		list.add(comboBoxValues[i]);
	    }
	    list.add(value);

	    // Order the list (if required).
	    if (order == ASCENDING_ORDER) {
		sort(list, true);
	    }
	    else if (order == DESCENDING_ORDER) {
		sort(list, false);
	    }
	    else {
		// do nothing
	    }
	}
	catch (Exception e) {
	    // Create the list corresponding to the original numbers.
	    list = new ArrayList();
	    for (int i = 0; i < comboBoxValues.length; i++) {
		list.add(comboBoxValues[i]);
	    }

	    // Add the new number at the back.
	    list.add(value);
	}

	// Obtain the new array of numbers and return.
	String[] numbers = new String[list.size()];
	for (int i = 0; i < list.size(); i++) {
	    numbers[i] = (String) list.get(i);
	}
	comboBoxValues = numbers;

    }


    /** Returns the given unformatted string "as is". If you need some other formatting, override this method.
     * @param unformattedString the unformatted string
     * @return the unformatted string */
    protected String format(String unformattedString)
    {
	return unformattedString;
    }


    /** Undoes the formatting of the format() method. In our case this means that the given string is returned unaltered. However, if you override the format() method, you have to override this method as well.
     * @param formattedString the string to be "unformatted"
     * @return the unformatted string (which ought to be a number) */
    protected String unformat(String formattedString)
    {
	return formattedString;
    }


    /** Returns the index of the given value in the array of combo box values. If the current value isn't included in the array or something else goes wrong, -1 is returned instead.
     * @param value the value to be found in the array of combo box values
     * @return the index of the current value in the array of combo box values or -1 if it isn't part of the array */
    private int findValue(String value)
    {
	int index = -1;
	try {
	    for (int i = 0; i < comboBoxValues.length; i++) {
		if (areEqual(comboBoxValues[i], value)) {
		    return i;
		}
	    }
	}
	catch (Exception e) {
	    return -1;
	}

	// As it seems, the current value is not contained in the array.
	return -1;
    }


    /** Assigns the given value to the data field associated with this text field. The value NOT_SET is replaced by null.
     * @param value the value to be assigned to the associated data field */
    protected void setDataObjectValue(String value)
    {
	if (value.equals(NOT_SET)) {
	    value = null;
	}
	PiptDataAccess.setValue(dataObject, field, value);
    }


    /** Returns the value of the data field associated with this text field. If that value is null, the unselected value is returned instead. A null value is replaced by the NOT_SET value.
     * @return the value of the associated data field */
    protected String getDataObjectValue()
    {
	String value = PiptDataAccess.getValue(dataObject, field);
	if (value == null) {
	    value = NOT_SET;
	}
	return value;
    }


    /** Checks whether the current selected item is of the correct form. */
    protected abstract boolean hasValidForm();


    /** Checks whether the two given values should be regarded as equal from the point of view of the combo box.
     * @param firstValue the first value to be compared
     * @param secondValue the second value to be compared */
    protected abstract boolean areEqual(String firstValue, String secondValue);


    /** Sorts the given list. The second parameter states whether the list should have ascending (<code>true</code>) or descending (<code>false</code>) order.
     * @param list the list to be sorted
     * @param useAscendingOrder states whether the list should have an ascending order after sorting */
    protected abstract void sort(java.util.List list, boolean useAscendingOrder);


    /** Produces an error message informing the user that the given value doesn't have the correct form. An implementing method should use the ErrorLog class for this.
     * @param parent the parent GUI component for the error message
     * @param value the value, which doesn't have the correct form */
    protected abstract void showInvalidFormMessage(JComponent parent, Object value);
}
