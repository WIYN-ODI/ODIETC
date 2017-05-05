package za.ac.salt.pipt.common.gui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.PiptData;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each combo box associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action listener is called for as well.<br />
Now while these could be added by hand for each and every combo box, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action listener passes the current value to the associated PIPT quantity, and the property change listener updates the combo box state according to the new value of the associated quantity.<br />
This class and its extensions alleviate the developer from the burding of having to produce this code as much as possible by providing and adding all the required listeners for the case of strings.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code> and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br />
Before accepting some input, it is checked whether after unformatting it has the correct form. In addition, InvalidValueExceptions are duely caught and processed by the combo box.<br />
The combo box is editable by default. */
public class PiptStringComboBox extends PiptGenericComboBox
{
    /** Creates a JComboBox for the quantity given by the specified field of the supplied data object. The combo box contains the given list of values and, if isn't part of that list already, the current value of the associated quantity. If the action and property change listeners provided by the getActionListener() and getPropertyChangeListener() methods are non-null, they are added to the combo box and the data object, respectively. When adding the current value to the list of combo box values, the specified order (ascending, descending or no order at all) is maintained. The current value of the associated quantity is chosen as the selected item of the combo box.
     * @param dataObject the data object associated with this combo box
     * @param field the field (of the data object) associated with this combo box
     * @param comboBoxValues the list of combo box values (possibly without the current value of the associated field)
     * @param order the order of the list of combo box values (must be either of the values ASCENDING_ORDER, DESCENDING_ORDER or NO_ORDER) */
    public PiptStringComboBox(PiptData dataObject, String field, String[] comboBoxValues, int order)
    {
	// Initialize the combo box.
	super(dataObject, field, comboBoxValues, order);
    }


    /** Returns true, as by default all strings should be allowed.
     * @return true if the current (unformatted) selected item constitutes a number */
    protected boolean hasValidForm()
    {
	return true;
    }


    /** Returns true if the two given strings are equal to each other.
     * @param firstValue the first string to be compared
     * @param secondValue the second string to be compared
     * @return true if the two strings are equal */
    public boolean areEqual(String firstValue, String secondValue)
    {
	return firstValue.equals(secondValue);
    }


    /** Sorts the given list as a list of numbers. The second parameter states whether the resulting list should be of ascending order.
     * @param list the list to be sorted
     * @param useAscendingOrder states whether the sorted list should be of ascending order */
    protected void sort(List list, boolean useAscendingOrder)
    {
	Collections.sort(list, new StringRepresentationComparator(useAscendingOrder));
    }


    /** Produces an error message informing the user that the given value isn't valid.
     * @param parent the parent GUI component for the error message
     * @param value the value, which is invalid */
    protected void showInvalidFormMessage(JComponent parent, Object value)
    {
	ErrorLog.invalidValueMessage(parent, value);
    }


    /** This class provides the compare method for strings. */
    private class StringRepresentationComparator implements Comparator
    {
	/** states whether an ascending order is to be assumed */
	private boolean orderedAscendingly;

	/** Sets the internal variable for the order to the given value.
	 * @param orderedAscendingly states whether an ascending (true) or descending order (false) is to be assumed */
	public StringRepresentationComparator(boolean orderedAscendingly)
	{
	    this.orderedAscendingly = orderedAscendingly;
	}

	/** Compares the string representation of the two given objects and returns the result. The result depends on the value of the acendingOrder variable set by the constructor: If it is true, -1, 0 or 1, depending on whether the first string (representation) is less than, equal to or greater than that given by the second string (representation). If it is false, on the other hand, 1, 0 or -1 are returned, depending again on whether the first string (representation) is less than, equal to or greater than the second string (representation).
	 * @param firstValue the first object
	 * @param secondValue the second object
	 * @return the result of comparing the string representations of the two objects */
	public int compare(Object firstValue, Object secondValue)
	{
	    // Obtain the comparison result for an ascending order.
	    int result = firstValue.toString().compareTo(secondValue.toString());

	    // If the numbers are to be ordered descendingly, we have to
	    // adapt the result accordingly.
	    if (!orderedAscendingly) {
		result *= -1;
	    }

	    // Return the result.
	    return result;
	}
    }
}
