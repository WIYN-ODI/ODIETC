package za.ac.salt.pipt.common.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each label associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action and focus listeners are called for as well.<br />
Now while these could be added by hand for each and every text field, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action and focus listener pass the current value to the associated PIPT quantity, and the property change listener updates the text field according to the new value of the associated quantity.<br />
Hence this class alleviates the developer from the burding of having to produce this code as much as possible.<br />
Before accepting some input, it is checked whether after unformatting it has the correct form. In addition, InvalidValueExceptions are duely caught and processed by the text field.<br />
In case you need a listeners different from those provided by this class, you may override the method <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that this method returns <code>null</code>.<br /> */
public class PiptNonEditableLabel extends JLabel
{
    /** the object associated with this text field */
    protected PiptData dataObject;

    /** the name of the field associated with this text field */
    protected String field;


    /** Initializes the label, which is assumed to be associated with the given  data object.
     * @param dataObject the object associated with this text field
     * @param field the data field associated with this text field */
    public PiptNonEditableLabel(PiptData dataObject, String field)
    {
	// Assign the given value to the internal data object variable.
	this.dataObject = dataObject;

	// Assign the given value to the internal data field variable.
	this.field = field;

	// Sets the text.
	setText(format(getDataObjectValue()));

	// Add the listener.
	addPropertyChangeListener();
    }


    /** Gets the propertychange listener for this text field and, if it is non-null, add it to the object associated with the text field */
    private void addPropertyChangeListener()
    {
	PropertyChangeListener propertyChangeListener = getPropertyChangeListener();
	if (propertyChangeListener != null) {
	    dataObject.addPropertyChangeListener(propertyChangeListener);
	}
    }


    /** Returns a property change event which sets the value of the text field to that of the associated quantity. If necessary, the allowed range of the text field is extended to include the new value. Override this method if you neeed some other listener. The code is executed only if the data field associated with this text field has changed. */
    private PropertyChangeListener getPropertyChangeListener()
    {
	return new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event)
		{
 		    if (event.getPropertyName().equals(field)) {
			setText(format(getDataObjectValue()));
 		    }
		}
	    };
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


    /** Assigns the given value to the data field associated with this text field.
     * @param value the value to be assigned to the associated data field */
    protected void setDataObjectValue(String value)
    {
	PiptDataAccess.setValue(dataObject, field, value);
    }


    /** Returns the value of the data field associated with this text field. If that value is null, the unselected value is returned instead.
     * @return the value of the associated data field */
    protected String getDataObjectValue()
    {
	return PiptDataAccess.getValue(dataObject, field);
    }


    /** Returns true, as there is no need for checking validity.
     * @return true */
    protected boolean hasValidForm()
    {
	return true;
    }


    /** Produces an error message informing the user that the given value doesn't have the correct form. An implementing method should use the ErrorLog class for this.
     * @param parent the parent GUI component for the error message
     * @param value the value, which doesn't have the correct form */
    protected void showInvalidFormMessage(JComponent parent, Object value)
    {
	ErrorLog.rejectedValueMessage(parent, value, new InvalidValueException("The value has been rejected."));
    }
}

