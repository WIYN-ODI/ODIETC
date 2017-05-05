package za.ac.salt.pipt.common.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTextField;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each text field associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action and focus listeners are called for as well.<br />
Now while these could be added by hand for each and every text field, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action and focus listener pass the current value to the associated PIPT quantity, and the property change listener updates the text field according to the new value of the associated quantity.<br />
Hence this class alleviates the developer from the burding of having to produce this code as much as possible.<br />
Before accepting some input, it is checked whether after unformatting it has the correct form. In addition, InvalidValueExceptions are duely caught and processed by the text field.<br />
In case you need listeners different from those provided by this class, you may override the methods <code>getActionListener()</code>, <code>getFocusListener() and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br /> */
public abstract class PiptGenericTextField extends JTextField
{
    /** the default number of columns in a PIPT text field */
    public static final int DEFAULT_NUMBER_OF_COLUMNS = 6;

    /** the object associated with this text field */
    protected PiptData dataObject;

    /** the name of the field associated with this text field */
    protected String field;


    /** Initializes the text field, which is assumed to be associated with the given  data object.
     * @param dataObject the object associated with this text field
     * @param field the data field associated with this text field
     * @param numberOfColumns the number of columns for the text field */
    public PiptGenericTextField(PiptData dataObject, String field, int numberOfColumns)
    {
	// Assign the given value to the internal data object variable.
	this.dataObject = dataObject;

	// Assign the given value to the internal data field variable.
	this.field = field;

	// Sets the number of columns.
	setColumns(numberOfColumns);

	// Sets the text.
	setText(format(getDataObjectValue()));

	// Add the listeners.
	addActionListener();
	addFocusListener();
	addPropertyChangeListener();
    }


    /** Gets the action listener for this text field and, if it is non-null, add it. */
    private void addActionListener()
    {
	ActionListener actionListener = getActionListener();
	if (actionListener != null) {
	    addActionListener(actionListener);
	}
    }


    /** Gets the focus listener for this text field and, if it is non-null, add it. */
    private void addFocusListener()
    {
	FocusListener focusListener = getFocusListener();
	if (focusListener != null) {
	    addFocusListener(focusListener);
	}
    }


    /** Gets the propertychange listener for this text field and, if it is non-null, add it to the object associated with the text field */
    private void addPropertyChangeListener()
    {
	PropertyChangeListener propertyChangeListener = getPropertyChangeListener();
	if (propertyChangeListener != null) {
	    dataObject.addPropertyChangeListener(propertyChangeListener);
	}
    }


    /** Returns an action listener which changes the value of the associated quantity to that of the text field. */
    private ActionListener getActionListener()
    {
	return new ActionListener() {
		public void actionPerformed(ActionEvent event)
		{
		    defaultActionAndFocusListenerAction(event);
		}
	    };
    }


    /** Returns a focus listener which changes the value of the associated quantity to that of the text field if the focus is lost, and does nothing if the focus is gained. */
    private FocusListener getFocusListener()
    {
	return new FocusListener() {
		public void focusLost(FocusEvent event)
		{
		    defaultActionAndFocusListenerAction(event);
		}

		public void focusGained(FocusEvent event)
		{
		    // do nothing
		}
	    };
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


    /** Assigns the content of the text field to the associated quantity. If the content isn't of the correct form or if it is found to be invalid (so that an InvalidValueException is thrown), the user is informed and the content is reverted to the current value of the associated quantity. */
    protected final void defaultActionAndFocusListenerAction(EventObject event)
    {
	// Get the (formatted) value corresponding to that of the associated
	// data object.
	String currentFormattedQuantityValue = format(getDataObjectValue());

	// Check that the field content has the correct form. If this turns out
	// not to be the case, inform the user and revert to the current value
	// of the associated quantity.
	JComponent component = (JComponent) event.getSource();
	if (!hasValidForm()) {
	    showInvalidFormMessage(component, getText());
	    setText(currentFormattedQuantityValue);
	}

	// Set the associated data object. If this fails with an
	// InvalidValueException, inform the user and revert to the value
	// corresponding to the associated data object.
	try {
	    setDataObjectValue(unformat(getText()));
	}
	catch (InvalidValueException ive) {
	    ErrorLog.rejectedValueMessage(component, getText(), ive);
	    setText(currentFormattedQuantityValue);
	}
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


    /** Assigns the given value to the data field associated with this text field. An empty string is replaced by a null value.
     * @param value the value to be assigned to the associated data field */
    protected void setDataObjectValue(String value)
    {
	if (value.equals("")) {
	    value = null;
	}
	PiptDataAccess.setValue(dataObject, field, value);
    }


    /** Returns the value of the data field associated with this text field. If that value is null, the unselected value is returned instead. A null value is replaced by an empty string.
     * @return the value of the associated data field */
    protected String getDataObjectValue()
    {
	String value = PiptDataAccess.getValue(dataObject, field);
	if (value == null) {
	    value = "";
	}
	return value;
    }


    /** Returns true if the current value of the text field has the correct form.
     * @return true if the current value of the text field has the correct form */
    protected abstract boolean hasValidForm();


    /** Produces an error message informing the user that the given value doesn't have the correct form. An implementing method should use the ErrorLog class for this.
     * @param parent the parent GUI component for the error message
     * @param value the value, which doesn't have the correct form */
    protected abstract void showInvalidFormMessage(JComponent parent, Object value);
}

