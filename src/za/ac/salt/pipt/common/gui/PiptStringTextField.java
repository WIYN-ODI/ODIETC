package za.ac.salt.pipt.common.gui;

import javax.swing.JComponent;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.PiptData;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each text field associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action and focus listeners are called for as well.<br />
Now while these could be added by hand for each and every text field, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action and focus listener pass the current value to the associated PIPT quantity, and the property change listener updates the text field according to the new value of the associated quantity.<br />
Hence this class alleviates the developer from the burding of having to produce this code as much as possible.<br />
Before accepting some input, it is checked whether after unformatting it has the correct form. In addition, InvalidValueExceptions are duely caught and processed by the text field.<br />
In case you need listeners different from those provided by this class, you may override the methods <code>getActionListener()</code>, <code>getFocusListener() and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br /> */
public class PiptStringTextField extends PiptGenericTextField
{
    /** Creates the text field, which is assumed to be associated with the given field of the specified data object. The default number of columns is used for the text field. */
    public PiptStringTextField(PiptData dataObject, String field)
    {
	this(dataObject, field, DEFAULT_NUMBER_OF_COLUMNS);
    }


    /** Creates the text field, which is assumed to be associated with the given field of the specified data object. The number of columns for the text field must be specified.
     * @param dataObject the object associated with this text field
     * @param field the name of the field associated with this text field
     * @param numberOfColumns the number of columns of the text field */
    public PiptStringTextField(PiptData dataObject, String field, int numberOfColumns)
    {
	// Initialize the text field.
	super(dataObject, field, numberOfColumns);
    }


    /** Returns true, as by default, a string cannot be of the wrong form.
     * @return true */
    protected boolean hasValidForm()
    {
	return true;
    }


    /** Produces an error message informing the user that the given value doesn't constitute a number.
     * @param parent the parent GUI component for the error message
     * @param value the value, which doesn't constitute a number */
    protected void showInvalidFormMessage(JComponent parent, Object value)
    {
	ErrorLog.invalidValueMessage(parent, value);
    }
}
