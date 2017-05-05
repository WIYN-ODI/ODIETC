package za.ac.salt.pipt.common.gui;

import javax.swing.JComponent;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.PiptData;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each text field associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action and focus listeners are called for as well.<br />
Now while these could be added by hand for each and every text field, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action and focus listener pass the current value to the associated PIPT quantity, and the property change listener updates the text field according to the new value of the associated quantity.<br />
Hence the PiptIntegerTextField class alleviates the developer from the burding of having to produce this code. Its constructor expects (among others) the object containing the associated quantity and the name of the object's field for the quantity. It then happily provides default listeners for the quantity.<br />
Of course, in order to achieve this task the constructor has to make some assumptions of how to access the desired quantity:<br />
<strong>The object passed is a PiptData object and contains the error checking setter and the getter method for the named quantity.</strong><br />
An example might be in order. Let us consider some object <code>blackbody</code> of the type <code>Blackbody</code>. If we pass <code>blackbody</code> together with the string <code>"temperature"</code>, the constructor tacitly assumes that the class <code>Blackbody</code> contains the methods <code>safeSetTemperature()</code> and <code>getTemperature()</code>, where the setter and getter method expect and return the same simple type, respectively. If the supplied object doesn't conform to these requirements, the program is terminated with a respective error message. (If you are a user and get such an error, please contact your nearest developer as soon as possible.) Obviously, it must be possible to add the property change listener to the given object, but we ensure this by demanding the object to be of the respective type.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code>, <code>getFocusListener() and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br />

In addition, a method for retrieving the field content as a number from an event is given. */
public class PiptIntegerTextField extends PiptNumberTextField
{
    /** the default number of columns in a PIPT text field */
    public static final int DEFAULT_NUMBER_OF_COLUMNS = 6;


    /** Creates the text field, which is assumed to be associated with the given field of the specified data object. The default number of columns is used for the text field. */
    public PiptIntegerTextField(PiptData dataObject, String field)
    {
	this(dataObject, field, DEFAULT_NUMBER_OF_COLUMNS);
    }


    /** Creates the text field, which is assumed to be associated with the given field of the specified data object. The number of columns for the text field must be specified.
     * @param dataObject the object associated with this text field
     * @param field the name of the field associated with this text field
     * @param numberOfColumns the number of columns of the text field */
    public PiptIntegerTextField(PiptData dataObject, String field, int numberOfColumns)
    {
	// Initialize the text field.
	super(dataObject, field, numberOfColumns);
    }


    /** Returns true if the current value of the text field is an integer number.
     * @return true if the current value of the text field is an integer number */
    protected boolean hasValidForm()
    {
	// If the text field contains no value, the value is correct by
	// definition.
	if (getText().equals("")) {
	    return true;
	}

	// If the value of the text field cannot be parsed as an integer number,
	// it is no integer number.
	try {
	    Long.parseLong(getText());
	}
	catch (NumberFormatException nfe) {
	    return false;
	}

	// Otherwise it is indeed a number.
	return true;
    }


    /** Produces an error message informing the user that the given value doesn't constitute an integer number.
     * @param parent the parent GUI component for the error message
     * @param value the value, which doesn't constitute a number */
    protected void showInvalidFormMessage(JComponent parent, Object value)
    {
	ErrorLog.noIntegerMessage(parent, value);
    }
}

