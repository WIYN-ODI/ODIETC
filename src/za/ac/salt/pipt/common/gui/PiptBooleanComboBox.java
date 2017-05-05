package za.ac.salt.pipt.common.gui;

import java.util.List;

import javax.swing.JComponent;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.PiptData;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each combo box associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action listener is called for as well.<br />
Now while these could be added by hand for each and every combo box, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action listener passes the current value to the associated PIPT quantity, and the property change listener updates the combo box state according to the new value of the associated quantity.<br />
Hence the PiptBooleanComboBox class alleviates the developer from the burding of having to produce this code. Its constructor expects (among others) the object containing the associated quantity and the name of the object's field for the quantity. It then happily provides default listeners for the quantity.<br />
Of course, in order to achieve this task the constructor has to make some assumptions of how to access the desired quantity:<br />
<strong>The object passed is a PiptData object and contains the error checking setter and the getter method for the named quantity.</strong><br />
An example might be in order. Let us consider some object <code>blackbody</code> of the type <code>Blackbody</code>. If we pass <code>blackbody</code> together with the string <code>"temperature"</code>, the constructor tacitly assumes that the class <code>Blackbody</code> contains the methods <code>safeSetTemperature()</code> and <code>getTemperature()</code>, where the setter and getter method expect and return the same simple type, respectively. If the supplied object doesn't conform to these requirements, the program is terminated with a respective error message. (If you are a user and get such an error, please contact your nearest developer as soon as possible.) Obviously, it must be possible to add the property change listener to the given object, but we ensure this by demanding the object to be of the respective type.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code> and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br />
Before accepting some input, it is checked whether after unformatting it constitutes a number. In addition, InvalidValueExceptions are duely caught and processed by the combo box.<br />
The combo box is editable by default. */
public class PiptBooleanComboBox extends PiptGenericComboBox
{
    /** Creates a JComboBox for the quantity given by the specified field of the supplied data object. The combo box contains the given list of values and, if isn't part of that list already, the current value of the associated quantity. If the action and property change listeners provided by the getActionListener() and getPropertyChangeListener() methods are non-null, they are added to the combo box and the data object, respectively. When adding the current value to the list of combo box values, the specified order (ascending, descending or no order at all) is maintained. The current value of the associated quantity is chosen as the selected item of the combo box.
     * @param dataObject the data object associated with this combo box
     * @param field the field (of the data object) associated with this combo box
     * @param comboBoxValues the list of combo box values (possibly without the current value of the associated field) (is ignored)
     * @param order the order of the list of combo box values (must be either of the values ASCENDING_ORDER, DESCENDING_ORDER or NO_ORDER) (is ignored) */
    public PiptBooleanComboBox(PiptData dataObject, String field, String[] comboBoxValues, int order)
    {
	// Initialize the combo box.
	super(dataObject, field, new String[] {"true", "false"}, PiptGenericComboBox.DESCENDING_ORDER);
    }


    /** Returns true if the current selected item is, if unformatted, a number.
     * @return true if the current (unformatted) selected item constitutes a number */
    protected boolean hasValidForm()
    {
	// If the value is PiptGenericComboBox.NOT_SET, it is of valid form by
	// definition.
	String value = (String) getSelectedItem();
	if (value.equals(PiptGenericComboBox.NOT_SET)) {
	    return true;
	}

	// If the unformatted value cannot be parsed as a boolean, it isn't
	// valid.
	try {
	    Boolean.valueOf(unformat(value));
	}
	catch (NumberFormatException nfe) {
	    return false;
	}

	// Otherwise it is indeed a boolean.
	return true;
    }


    /** Returns true if the two given strings correspond to the same boolean values.
     * @param firstBoolean the first number to be compared
     * @param secondBoolean the second number to be compared
     * @return true if the booleans corresponding to the given strings are equal */
    protected boolean areEqual(String firstBoolean, String secondBoolean)
    {
	if (firstBoolean.equals(PiptGenericComboBox.NOT_SET)
	    || secondBoolean.equals(PiptGenericComboBox.NOT_SET)) {
	    return firstBoolean.equals(secondBoolean);
	}
	return Boolean.valueOf(firstBoolean).equals(Boolean.valueOf(secondBoolean));
    }


    /** Does nothing, as there is no need for sorting.
     * @param list the list to be sorted
     * @param useAscendingOrder states whether the sorted list should be of ascending order */
    protected void sort(List list, boolean useAscendingOrder)
    {
	// do nothing
    }


    /** Produces an error message informing the user that the given value doesn't constitute a number.
     * @param parent the parent GUI component for the error message
     * @param value the value, which is no number */
    protected void showInvalidFormMessage(JComponent parent, Object value)
    {
	ErrorLog.noBooleanMessage(parent, value);
    }
}
