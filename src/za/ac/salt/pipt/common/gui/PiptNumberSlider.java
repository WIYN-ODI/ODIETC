package za.ac.salt.pipt.common.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each slider associated with a PIPT quantity must update that quantity when its value changes. Hence a respective change listener is called for as well.<br />
Now while these could be added by hand for each and every slider, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The state change listener passes the current value to the associated PIPT quantity, and the property change listener updates the slider state according to the new value of the associated quantity.<br />
Hence the PiptNumberSlider class alleviates the developer from the burding of having to produce this code. Its constructor expects (among others) the object containing the associated quantity and the name of the object's field for the quantity. It then happily provides default listeners for the quantity.<br />
Of course, in order to achieve this task the constructor has to make some assumptions of how to access the desired quantity:<br />
<strong>The object passed is a PiptData object and contains the error checking setter and the getter method for the named quantity.</strong><br />
An example might be in order. Let us consider some object <code>blackbody</code> of the type <code>Blackbody</code>. If we pass <code>blackbody</code> together with the string <code>"temperature"</code>, the constructor tacitly assumes that the class <code>Blackbody</code> contains the methods <code>safeSetTemperature()</code> and <code>getTemperature()</code>, where the setter and getter method expect and return the same simple type, respectively. If the supplied object doesn't conform to these requirements, the program is terminated with a respective error message. (If you are a user and get such an error, please contact your nearest developer as soon as possible.) Obviously, it must be possible to add the property change listener to the given object, but we ensure this by demanding the object to be of the respective type.<br />
InputValidationExceptions arising from setting the associated quantity are duely caught and processed by the slider.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code> and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br /> */
public class PiptNumberSlider extends JSlider
{
    /** the data object associated with this slider */
    protected PiptData dataObject;

    /** the name of the field associated with this slider */
    protected String field;

    /** states whether the boundaries are being set */
    protected boolean boundariesBeingSet = false;


    /** Creates the slider, which is assumed to be associated with the specified field of the given data object. The default value range of the slider must be given. If the value of the associated quantity lies outside this range, the range is extended to include the value. The value is chosen as the value shown by the slider.
     * @param dataObject the data object associated with this slider
     * @param field the name of the data object field associated with this slider
     * @param minimumValue the default minimum value allowed for this slider
     * @param maximumValue the default maximum value allowed for this slider
     * @param majorTickSpacing the major tick spacing for this slider
     * @param minorTickSpacing the minor tick spacing for this slider */
    public PiptNumberSlider(PiptData dataObject, String field, int minimumValue, int maximumValue, int majorTickSpacing, int minorTickSpacing)
    {
	// Assign the given data object and field name to the respective
	// internal variables.
	this.dataObject = dataObject;
	this.field = field;

	// Set the range and tick spacings of the slider.
	setMinimum(minimumValue);
	setMaximum(maximumValue);
	setMajorTickSpacing(majorTickSpacing);
	setMinorTickSpacing(minorTickSpacing);

	// Ensure that the ticks and labels are shown.
	setPaintTicks(true);
	setPaintLabels(true);

	// Customize the slider, setting the shown value.
	customize();

	// Add the state change and property change listeners.
	addChangeListener();
	addPropertyChangeListener();
    }


    /** Gets the (state) change listener for this slider and, if it is non-null, adds it. */
    protected void addChangeListener()
    {
	ChangeListener changeListener = getChangeListener();
	if (changeListener != null) {
	    addChangeListener(changeListener);
	}
    }


    /** Gets the property change listener for this slider and, if it is non-null, adds it to the data object associated with the combo box. */
    protected void addPropertyChangeListener()
    {
	PropertyChangeListener propertyChangeListener = getPropertyChangeListener();
	if (propertyChangeListener != null) {
	    dataObject.addPropertyChangeListener(propertyChangeListener);
	}
    }


    /** Returns a (state) change listener which changes the value of the associated quantity to that of the slider. If an InvalidValueException is thrown during setting the associated quantity, the user is informed and the slider is reverted to the value of the associated quantity. */
    protected ChangeListener getChangeListener()
    {
	return new ChangeListener() {
		public void stateChanged(ChangeEvent event)
		{
		    defaultChangeListenerCode(event);
		}
	    };
    }


    /** Returns a property change event which sets the value of the slider to that of the associated quantity. If necessary, the allowed range of the slider is extended to include the new value. Override this method if you neeed some other listener. The code is executed only if the data field associated with this slider has changed. */
    protected PropertyChangeListener getPropertyChangeListener()
    {
	return new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event)
		{
		    defaultPropertyChangeListenerCode(event);
		}
	    };
    }


    /** Sets the value of the relevant data object field. If during this process, an InvalidValueException is thrown, inform the user and revert to the current value of the associated quantity. The code is executed only if the setBoundary() method isn't being executed, as we have to avoid processing changes which are simply due to setting the boundaries.
     * @param event the event responsible for the call to the change listener */
    protected void defaultChangeListenerCode(ChangeEvent event)
    {
	// If the boundaries are being set, we mustn't proceed.
	if (boundariesBeingSet) {
	    return;
	}

	// Otherwise we set the data field value.
	try {
	    setDataObjectValue(unformat(getValue()));
	}
	catch (InvalidValueException ive) {
	    ErrorLog.rejectedValueMessage((JComponent) event.getSource(), "" + getValue(), ive);
	    setValue(format(getDataObjectValue()));
	}
    }


    /** Sets the value of the slider to that of the associated quantity.
     * @param event the property change event responsible for the call to the property change listener */
    protected void defaultPropertyChangeListenerCode(PropertyChangeEvent event)
    {
	if (event.getPropertyName().equals(field)) {
	    customize();
	}
    }


    /** Assigns the given value to the data field associated with this slider.
     * @param value the value to be assigned to the associated data field */
    protected void setDataObjectValue(String value)
    {
	PiptDataAccess.setValue(dataObject, field, value);
    }


    /** Returns the value of the data field associated with this slider. A null value is replaced by an empty string.
     * @return the value of the associated data field */
    protected String getDataObjectValue()
    {
	String value = PiptDataAccess.getValue(dataObject, field);
	if (value == null) {
	    value = "";
	}
	return value;
    }


    /** Sets the value shown by the slider to that of the associated quantity. If necessary, the allowed range is extended to include this value. This method is executed only if the boundaries aren't being set. */
    protected void customize()
    {
	// Additional customizing doesn't make sense while the boundaries are
	// being set.
	if (boundariesBeingSet) {
	    return;
	}

	// We are about to set the boundaries.
	boundariesBeingSet = true;

	// If the value is an empty string, we replace it by the minimum allowed
	// value.
	String value = getDataObjectValue();
	if (value.equals("")) {
	    value = "" + getMinimum();
	}

	// If the value of the quantity associated with this slider lies
	// outside the current range of allowed values, extend the range to
	// include it.
	int currentValue = format(value);
	if (currentValue < getMinimum()) {
	    setMinimum(currentValue);
	}
	if (currentValue > getMaximum()) {
	    setMaximum(currentValue);
	}

	// Show the value of the associated quantity.
	setValue(currentValue);
	repaint();

	// We are finished with setting the boundaries.
	boundariesBeingSet = false;
    }


    /** Turns the given string into an integer. If for a subclass the value shown by the slider has a format different from that used in the XML, this is the place where to perform the mapping from the XML to the slider format.
     * @param unformattedValue the string to be converted into an integer
     * @return the integer corresponding to the given string */
    protected int format(String unformattedValue)
    {
	return (new Integer(unformattedValue)).intValue();
    }


    /** Turns the given integer into a string. If for a subclass the value shown by the slider has a format different from that used in the XML, this is the place where to perform the mapping from the slider to the XML format.
     * @param formattedValue the value to be converted into a string
     * @return the string corresponding to the given integer */
    protected String unformat(int formattedValue)
    {
	return "" + formattedValue;
    }
}
