package za.ac.salt.pipt.common.gui;

import javax.swing.SpinnerNumberModel;

import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each spinner associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action listener is called for as well.<br />
Now while these could be added by hand for each and every spinner, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The state change listener passes the current value to the associated PIPT quantity, and the property change listener updates the spinner state according to the new value of the associated quantity.<br />
Hence the PiptNumberSpinner class alleviates the developer from the burding of having to produce this code. Its constructor expects (among others) the object containing the associated quantity and the name of the object's field for the quantity. It then happily provides default listeners for the quantity.<br />
Of course, in order to achieve this task the constructor has to make some assumptions of how to access the desired quantity:<br />
<strong>The object passed is a PiptData object and contains the eror checking setter and the getter method for the named quantity.</strong><br />
An example might be in order. Let us consider some object <code>blackbody</code> of the type <code>Blackbody</code>. If we pass <code>blackbody</code> together with the string <code>"temperature"</code>, the constructor tacitly assumes that the class <code>Blackbody</code> contains the methods <code>safeSetTemperature()</code> and <code>getTemperature()</code>, where the setter and getter method expect and return the same simple type, respectively. If the supplied object doesn't conform to these requirements, the program is terminated with a respective error message. (If you are a user and get such an error, please contact your nearest developer as soon as possible.) Obviously, it must be possible to add the property change listener to the given object, but we ensure this by demanding the object to be of the respective type.<br />
InputValidationExceptions arising from setting the associated quantity are duely caught and processed by the spinner.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code> and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br /> */
public class PiptNumberSpinner extends PiptGenericSpinner
{
    /** Creates the spinner, which is assumed to be associated with the specified field of the given data object. The default value range of the spinner must be given. If the value of the associated quantity lies outside this range, the range is extended to include the value. The value is chosen as the value shown by the spinner.
     * @param dataObject the data object associated with this spinner
     * @param field the name of the data object field associated with this spinner
     * @param minimumValue the default minimum value allowed for this spinner
     * @param maximumValue the default maximum value allowed for this spinner
     * @param stepSize the step size of the spinner */
    
    public PiptNumberSpinner () {
	this (null, "test", 0,10,1);
	
    }
    public PiptNumberSpinner(PiptData dataObject, String field, double minimumValue, double maximumValue, double stepSize)
    {
	super(new SpinnerNumberModel(0.5 * (minimumValue + maximumValue), minimumValue, maximumValue, stepSize), dataObject, field);

	// Customize the spinner, setting the range and the shown value.
	customize();

	// Add the state change and property change listeners.
	addChangeListener();
	addPropertyChangeListener();
    }


    /** Sets the value shown by the spinner to that of the associated quantity. If necessary, the allowed range is extended to include this value. This method is executed only if the boundaries aren't being set. */
    protected void customize()
    {
	// Additional customizing doesn't make sense while the boundaries are
	// being set.
	if (boundariesBeingSet) {
	    return;
	}

	// We are about to set the boundaries.
	boundariesBeingSet = true;

	// If the value of the quantity associated with this spinner is an
	// empty string, replace it by the minimum allowed value.
	SpinnerNumberModel numberModel = (SpinnerNumberModel) model;
	String value = PiptDataAccess.getValue(dataObject, field);
	if (value.equals("")) {
	    value = "" + numberModel.getMinimum();
	}

	// If the value of the quantity associated with this spinner lies
	// outside the current range of allowed values, extend the range to
	// include it.
	Double currentValue = new Double(value);
	if (currentValue.compareTo((Double) numberModel.getMinimum()) == -1) {
	    numberModel.setMinimum(currentValue);
	}
	if (currentValue.compareTo((Double) numberModel.getMaximum()) == 1) {
	    numberModel.setMaximum(currentValue);
	}

	// Show the value of the associated quantity.
	numberModel.setValue(currentValue);

	// We are finished with setting the boundaries.
	boundariesBeingSet = false;
    }
}
