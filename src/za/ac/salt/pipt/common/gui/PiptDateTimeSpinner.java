package za.ac.salt.pipt.common.gui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each spinner associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action listener is called for as well.<br />
Now while these could be added by hand for each and every spinner, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The state change listener passes the current value to the associated PIPT quantity, and the property change listener updates the spinner state according to the new value of the associated quantity.<br />
Hence the PiptDateTimeSpinner class alleviates the developer from the burding of having to produce this code. Its constructor expects (among others) the object containing the associated quantity and the name of the object's field for the quantity. It then happily provides default listeners for the quantity.<br />
Of course, in order to achieve this task the constructor has to make some assumptions of how to access the desired quantity:<br />
<strong>The object passed is a PiptData object and contains the eror checking setter and the getter method for the named quantity.</strong><br />
An example might be in order. Let us consider some object <code>blackbody</code> of the type <code>Blackbody</code>. If we pass <code>blackbody</code> together with the string <code>"temperature"</code>, the constructor tacitly assumes that the class <code>Blackbody</code> contains the methods <code>safeSetTemperature()</code> and <code>getTemperature()</code>, where the setter and getter method expect and return the same simple type, respectively. If the supplied object doesn't conform to these requirements, the program is terminated with a respective error message. (If you are a user and get such an error, please contact your nearest developer as soon as possible.) Obviously, it must be possible to add the property change listener to the given object, but we ensure this by demanding the object to be of the respective type.<br />
InputValidationExceptions arising from setting the associated quantity are duely caught and processed by the spinner.<br />
In case you need other listeners, you may override the methods <code>getActionListener()</code> and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br /> */
public class PiptDateTimeSpinner extends PiptGenericSpinner
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4714679993078150016L;


	/** Creates the spinner, which is assumed to be associated with the specified field of the given data object. The default value range of the spinner must be given. If the value of the associated quantity lies outside this range, the range is extended to include the value. The value is chosen as the value shown by the spinner.
     * @param dataObject the data object associated with this spinner
     * @param field the name of the data object field associated with this spinner
     * @param minimumDate the default minimum date allowed for this spinner (may be null, if there is no minimum date)
     * @param maximumDate the default maximum date allowed for this spinner (may be null, if there is no maximum date)
     * @param stepSize the step size of the spinner (must be a valid Calendar constant such as Calendar.MONTH) */
    public PiptDateTimeSpinner(PiptData dataObject, String field, Date minimumDate, Date maximumDate, int stepSize)
    {
	super(new SpinnerDateModel(new Date(), minimumDate, maximumDate, stepSize), dataObject, field);

	// Use a slightly more convenient editor.
	setEditor(new JSpinner.DateEditor(this, "dd MMMM yyyy hh:mm:ssZ"));

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

	// If the value of the quantity associated with this spinner lies
	// outside the current range of allowed values, extend the range to
	// include it.
	Calendar calendar = (Calendar) PiptDataAccess.getValue(dataObject, field, Calendar.class);
	Date currentDate = calendar.getTime();
	SpinnerDateModel dateModel = (SpinnerDateModel) model;
	Date minimumDate = (Date) dateModel.getStart();
	Date maximumDate = (Date) dateModel.getEnd();
	if (minimumDate != null && currentDate.compareTo(minimumDate) == -1) {
	    dateModel.setStart(currentDate);
	}
	if (maximumDate != null && currentDate.compareTo(maximumDate) == 1) {
	    dateModel.setEnd(currentDate);
	}

	// Show the value of the associated quantity.
	dateModel.setValue(currentDate);

	// We are finished with setting the boundaries.
	boundariesBeingSet = false;
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
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	    calendar.setTime((Date) getValue());
	    PiptDataAccess.setValue(dataObject, field, calendar);
	}
	catch (InvalidValueException ive) {
	    ErrorLog.rejectedValueMessage((JComponent) event.getSource(), getValue(), ive);
	    setValue(PiptDataAccess.getValue(dataObject, field, Calendar.class));
	}
    }
}
