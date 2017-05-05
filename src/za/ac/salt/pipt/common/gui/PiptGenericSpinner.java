package za.ac.salt.pipt.common.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each spinner associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action listener is called for as well.<br />
Now while these could be added by hand for each and every spinner, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The state change listener passes the current value to the associated PIPT quantity, and the property change listener updates the spinner state according to the new value of the associated quantity.<br />
Hence the PiptGenericSpinner class alleviates the developer from the burding of having to produce this code.<br />
InputValidationExceptions arising from setting the associated quantity are duely caught and processed by the spinner.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code> and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br /> */
public abstract class PiptGenericSpinner extends JSpinner
{
    /** the object associated with this spinner */
    protected PiptData dataObject;

    /** the name of the field associated with this spinner */
    protected String field;

    /** the spinner model */
    protected SpinnerModel model;

    /** states whether the boundaries are being set */
    protected boolean boundariesBeingSet = false;


    /** Initializes the spinner, which is assumed to be associated with the specified data object. The value of corresponding to that of the associated quantity is chosen as the the value shown by the spinner.
     * @param model the spinner model
     * @param dataObject the data object associated with this spinner
     * @param field the field associated with this spinner */
    public PiptGenericSpinner(SpinnerModel model, PiptData dataObject, String field)
    {
	// Assign the given data object to the respective internal variable.
	this.dataObject = dataObject;

	// Assign the fiven data field to the respective internal variable.
	this.field = field;

	// Set the spinner model.
	this.model = model;
	setModel(model);

	// Add the state change and property change listeners.
	addChangeListener();
	addPropertyChangeListener();
    }


    /** Gets the (state) change listener for this spinner and, if it is non-null, adds it. */
    protected void addChangeListener()
    {
	ChangeListener changeListener = getChangeListener();
	if (changeListener != null) {
	    addChangeListener(changeListener);
	}
    }


    /** Gets the property change listener for this spinner and, if it is non-null, adds it to the data object associated with the combo box. */
    protected void addPropertyChangeListener()
    {
	PropertyChangeListener propertyChangeListener = getPropertyChangeListener();
	if (propertyChangeListener != null && dataObject != null) {
	    dataObject.addPropertyChangeListener(propertyChangeListener);
	}
    }


    /** Returns a (state) change listener which changes the value of the associated quantity to that of the spinner. If an InvalidValueException is thrown during setting the associated quantity, the user is informed and the spinner (model) is reverted to the value of the associated quantity. */
    protected ChangeListener getChangeListener()
    {
	return new ChangeListener() {
		public void stateChanged(ChangeEvent event)
		{
		    defaultChangeListenerCode(event);
		}
	    };
    }


    /** Changes the value of the associated quantity to that of the spinner. If an InvalidValueException is thrown during setting the associated quantity, the user is informed and the spinner (model) is reverted to the value of the associated quantity. The code is executed only if the setBoundary() method isn't being executed, as we have to avoid processing changes which are simply due to setting the boundaries.
     * @param event the event due to changing the spinner state */
    protected void defaultChangeListenerCode(ChangeEvent event)
    {
	// If the boundaries are being set, we mustn't proceed.
	if (boundariesBeingSet) {
	    return;
	}

	// Sets the value of the relevant data object field. If during this
	// process, an InvalidValueException is thrown, inform the user and
	// revert to the current value of the associated quantity.
	try {
	    setDataObjectValue(getValue().toString());
	}
	catch (InvalidValueException ive) {
	    ErrorLog.rejectedValueMessage((JComponent) event.getSource(), getValue(), ive);
	    setValue(getDataObjectValue());
	}
    }


    /** Returns a property change event which sets the value of the spinner to that of the associated quantity. If necessary, the allowed range of the spinner is extended to include the new value. Override this method if you neeed some other listener. The code is executed only if the data field associated with this spinner has changed. */
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


    /** Assigns the given value to the data field associated with this text field. An empty string is replaced by null.
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


    /** Customizes the spinner. This method should be executed only if the boundaries aren't being set. */
    protected abstract void customize();
}
