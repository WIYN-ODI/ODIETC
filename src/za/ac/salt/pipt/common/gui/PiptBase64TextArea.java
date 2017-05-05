package za.ac.salt.pipt.common.gui;

import javax.swing.JComponent;

import org.apache.commons.codec.binary.Base64;

import za.ac.salt.pipt.common.ErrorLog;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataAccess;


/** One of the ideas behind PIPT-related data is that it may be shared by applications. Hence the GUIs involved must keep track of changes, even if these are caused by a completely different GUI. A reasonable way to achieve this is by adding property change listeners and hoping (not in vain) that the PIPT duely fires property change events when changed. In addition, each text area associated with a PIPT quantity must update that quantity when its value changes. Hence a respective action and focus listeners are called for as well.<br />
Now while these could be added by hand for each and every text area, this would become boring after a while. After all, the code is pretty the same for almost all combo boxes: The action and focus listener pass the current value to the associated PIPT quantity, and the property change listener updates the text area according to the new value of the associated quantity.<br />
Hence this class alleviates the developer from the burding of having to produce this code as much as possible.<br />
Before accepting some input, it is checked whether after unformatting it has the correct form. In addition, InvalidValueExceptions are duely caught and processed by the text area.<br />
In case you need listeners different from those provided by this class, you may override the methods <code>getActionListener()</code>, <code>getFocusListener() and <code>getPropertyChangeListener()</code>. If you don't need a listener at all, ensure that these methods return <code>null</code>.<br /> */
public class PiptBase64TextArea extends PiptGenericTextArea
{
    /** Initializes the text area with the default number of rows and columns.
     * @param dataObject the data object associated with this text area
     * @param field the field associated with this text area */
    public PiptBase64TextArea(PiptData dataObject, String field)
    {
	super(dataObject, field);
    }


    /** Initializes the text area.
     * @param dataObject the data object associated with this text area
     * @param field the field associated with this text area
     * @param rows the number of rows of the text area
     * @param columns the number of columns of the text area */
    public PiptBase64TextArea(PiptData dataObject, String field, int rows, int columns)
    {
	super(dataObject, field, rows, columns);
    }


    /** Base64 encode the given text, put it into an array of bytes and assign this to the respective data field. The setter method for the last step must be provided by the implementing class.
     * @param decodedString the text to be encoded */
    protected void setDataObjectValue(String decodedString)
    {
	Base64 base64Codec = new Base64();
	byte[] base64Array = base64Codec.encodeBase64(decodedString.getBytes());
	setBase64Value(base64Array);
    }


    /** Gets the array of bytes containing the Base64 encoded text, decode the text and returns the result. The getter method for the first step must be provided by the implementing class.
     * @return the decoded string */
    protected String getDataObjectValue()
    {
	byte[] base64Array = getBase64Value();
	Base64 base64Codec = new Base64();
	return new String(base64Codec.decodeBase64(base64Array));
    }


    /** Assigns the given array of bytes to the data field associated with this text area. An empty array is replaced by null.
     * @param base64Value the array of bytes to be assigned */
    protected void setBase64Value(byte[] base64Value)
    {
	if (base64Value.length == 0) {
	    base64Value = null;
	}
	PiptDataAccess.setValue(dataObject, field, base64Value);
    }


    /** Returns the array of bytes from the data field asociated with this text area. A null value is replaced by an empty array.
     * @return the array of bytes from the data field asociated with this text area */
    protected byte[] getBase64Value()
    {
	byte[] value = (byte[]) PiptDataAccess.getValue(dataObject, field, new Class[] {}, new Object[] {}, byte[].class);
	if (value == null) {
	    value = new byte[0];
	}
	return value;
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
