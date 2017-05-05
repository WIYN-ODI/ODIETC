package za.ac.salt.pipt.common.dataExchange;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;



/** This class offers methods for setting and getting a field value of a PiptData object. */
public class PiptDataAccess
{
    /** the hashtable of the primitive types and their clas counterparts */
    private static Hashtable<Class, Class> PRIMITIVE_TYPES = new Hashtable<Class, Class>();

    /** Initialize the hashtable of the primitive types and their clas counterparts. */
    static {
	PRIMITIVE_TYPES.put(Boolean.TYPE, Boolean.class);
	PRIMITIVE_TYPES.put(Byte.TYPE, Byte.class);
	PRIMITIVE_TYPES.put(Character.TYPE, Character.class);
	PRIMITIVE_TYPES.put(Short.TYPE, Short.class);
	PRIMITIVE_TYPES.put(Integer.TYPE, Integer.class);
	PRIMITIVE_TYPES.put(Long.TYPE, Long.class);
	PRIMITIVE_TYPES.put(Float.TYPE, Float.class);
	PRIMITIVE_TYPES.put(Double.TYPE, Double.class);
    }


    /** Sets the specified field of the given PiptData object to the provided value, using the setter method with error checking (i.e. the safeSetField() rather than setField() method). To this end, reflection is used to find this method for the given field and it is invoked. If the new value is provided as a string, it is turned into the required type before being passed to the setter method. If no appropriate setter method can be found, the program is terminated with a respective error message. (If you are a user and get such an error message, please complain to your nearest software developer immediately, urging him to fix the problem.)
     * @param piptDataObject the PiptData object dealt with by this method
     * @param field the name of the field to be set
     * @param value the value to be assigned to the field (must be a string or an array)
     * @throws InvalidValueException if the given value is neither a string nor an array or if the invoked setter method throws such an exception */
    public static void setValue(PiptData piptDataObject, String field, Object value)
    {
	// Obtain the name of the desired setter method. (Remember that the
	// field must be capitalized in the name.)
	if (piptDataObject == null) return;
	String setterMethodName = "safeSet" + capitalize(field);

	// Loop over all setter methods, trying to find a matching one. This
	// method must have the correct name and must accept exactly one
	// argument (but we don't care of what type).
	Method setterMethod = null;
	Method[] availableMethods = piptDataObject.getClass().getMethods();
	for (Method method: availableMethods) {
	    if (method.getName().equals(setterMethodName) && method.getParameterTypes().length == 1) {
		setterMethod = method;
		break;
	    }
	}

	// If no setter method could be found, output the stack trace and an
	// error message and terminate the program.
	if (setterMethod == null) {
	    new Exception("No safe setter method " + setterMethodName + "() could be found for the supported argument types.").printStackTrace();
	    System.exit(-1);
	}

	// We have to convert the given value into the required type for the
	// setter method. If any problem is encountered, the stack trace and an
	// error message are output and the program is terminated.
	Object setterArgument = null;
	try {
		
	    // If the parameter type of the setter method equals the type of
	    // the given value, we may just assign the value as the setter
	    // method argument. Otherwise, we either create a wrapper type
	    // instance (in case of a primitive type value) or assume that
	    // there is aconstructor for the setter method class which accepts
	    // the value as its parameter. In case the value is null, we assign
	    // null to the setter argument as well.
	    Class setterMethodType = setterMethod.getParameterTypes()[0];
	    if (value == null) {
		setterArgument = null;
	    }
	    else if (setterMethodType.equals(value.getClass())) {
		setterArgument = value;
	    }
	    else if (setterMethodType.isPrimitive()) {
		Constructor constructor = PRIMITIVE_TYPES.get(setterMethodType).getConstructor(value.getClass());
		setterArgument = constructor.newInstance(value);
	    }
	    else {
		Constructor constructor = setterMethodType.getConstructor(value.getClass());
		setterArgument = constructor.newInstance(value);
	    }
	}
	catch (Exception exception) {
	    exception.printStackTrace();
	    System.exit(-1);
	}

	// Invoke the setter method. If something goes wrong, output the stack
	// trace and an error message and terminate the program. (Unless the
	// setter method has thrown an InvalidValueException, which is simply
	// passed on.)
	try {
	    setterMethod.invoke(piptDataObject, new Object[] {setterArgument});
	}
	catch (Exception e) {
	    // If the exception has been caused by an InvalidValueException,
	    // we just pass on the latter.
	    Throwable causingException = e.getCause();
	    if (causingException != null && causingException.getClass().equals(InvalidValueException.class)) {
		throw new InvalidValueException(causingException.getMessage());
	    }

	    // Otherwise we output the stack trace and terminate the program.
	    e.printStackTrace();
	    System.exit(-1);
	}
    }


    /** Returns the value of the specified field of the given PiptData object. To this end, reflection is used to find the getter method for the given field and this method is invoked. The result is returned as a string. Methods starting with get and is are taken into consideration; if there is such a method expecting a boolean argument, it is preferred and is called with true. If no appropriate getter method can be found, the program is terminated with a respective error message. (If you are a user and get such an error message, please complain to your nearest software developer immediately, urging him to fix the problem.)
     * @param piptDataObject the PiptData object dealt with by this method
     * @param field the name of the field whose value is returned
     * @return the value of the field as a string */
    public static String getValue(PiptData piptDataObject, String field)
    {
	if (piptDataObject == null)return ("NaN");
	Object value = getValue(piptDataObject, field, Object.class);
	return value != null ? value.toString() : null;
    }


    /** Returns the value of the specified field of the given PiptData object, assuming the specified argument types and arguments. To this end, reflection is used to find the getter method for the given field and this method is invoked. The result is returned as a string. Methods starting with get and is are taken into consideration. If no appropriate getter method can be found, the program is terminated with a respective error message. (If you are a user and get such an error message, please complain to your nearest software developer immediately, urging him to fix the problem.)
     * @param piptDataObject the PiptData object dealt with by this method
     * @param field the name of the field whose value is returned
     * @param argumentTypes the argument types for the getter method
     * @param arguments the arguments for the getter method
     * @return the value of the field */
    public static String getValue(PiptData piptDataObject, String field, Class[] argumentTypes, Object[] arguments)
    {
	Object value = getValue(piptDataObject, field, argumentTypes, arguments, Object.class);
	return value != null ? value.toString() : null;
    }


    /** Returns the value of the specified field of the given PiptData object. To this end, reflection is used to find the getter method for the given field and this method is invoked. Methods starting with get and is are taken into consideration; if there is such a method expecting a boolean argument, it is preferred and is called with true. If no appropriate getter method can be found, the program is terminated with a respective error message. (If you are a user and get such an error message, please complain to your nearest software developer immediately, urging him to fix the problem.)
     * @param piptDataObject the PiptData object dealt with by this method
     * @param field the name of the field whose value is returned
     * @param returnType the return type of the getter method
     * @return the value of the field */
    public static Object getValue(PiptData piptDataObject, String field, Class returnType)
    {
	Object value = null;
	try {
	    value = getValue(piptDataObject, field, new Class[] {Boolean.TYPE}, new Object[] {Boolean.TRUE}, returnType);
	}
	catch (Exception e) {
	    try {
		value = getValue(piptDataObject, field, new Class[] {}, new Object[] {}, returnType);
	    }
	    catch (Exception innerException) {
		innerException.printStackTrace();
		System.err.println("FATAL ERROR: " + innerException.getMessage());
		System.exit(-1);
	    }
	}

	// Check whether the result really is an instance of the given return
	// type.
	if (value != null && !(returnType.isInstance(value))) {
	    throw new InvalidValueException("The getter method has returned an object of the type " + value.getClass() + ",  which is no instance of the requested type " + returnType);
	}

	// Return the result.
	return value;
    }


    /** Returns the value of the specified field of the given PiptData object, assuming the specified argument types and arguments. To this end, reflection is used to find the getter method for the given field and this method is invoked. Methods starting with get and is are taken into consideration. If no appropriate getter method can be found, the program is terminated with a respective error message. (If you are a user and get such an error message, please complain to your nearest software developer immediately, urging him to fix the problem.) The getter method must return an object which is an instance of the given return type.
     * @param piptDataObject the PiptData object dealt with by this method
     * @param field the name of the field whose value is returned
     * @param argumentTypes the argument types for the getter method
     * @param arguments the arguments for the getter method
     * @param returnType the return type of the getter method
     * @return the value of the field */
    public static Object getValue(PiptData piptDataObject, String field, Class[] argumentTypes, Object[] arguments, Class returnType)
    {
	// Obtain the name of the desired getter method. (Remember that the
	// field must be capitalized in the name.)
	String getterMethodName = "get" + capitalize(field);
	String isMethodName = "is" + capitalize(field);

	// Obtain the getter method. If the method doesn't exist, output the
	// stack trace and an error message and terminate the program.
	Method getterMethod = null;
	try {
	    getterMethod = piptDataObject.getClass().getMethod(getterMethodName, argumentTypes);
	}
	catch (Exception e) {
	    try {
		getterMethodName = isMethodName;
		getterMethod = piptDataObject.getClass().getMethod(getterMethodName, argumentTypes);
	    }
	    catch (Exception innerException) {
		throw new InvalidValueException("The getter method for the field " + field + " doesn't exist or cannot be accessed.");
	    }
	}

	// Invoke the getter method. If something goes wrong, output the stack
	// trace and an error message and terminate the program.
	Object value = "";
	try {
	    value = getterMethod.invoke(piptDataObject, arguments);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.err.println("FATAL INTERNAL ERROR: The getter method " + getterMethodName + "() couldn't be invoked.");
	    System.exit(-1);
	}
	 
	// If the result is null, return null.
	if (value == null) {
	    return null;
	}

	// Check whether the result really is an instance of the given return
	// type.
	if (!(returnType.isInstance(value))) {
	    throw new InvalidValueException("The getter method has returned an object which is no instance of the requested type " + returnType);
	}

	// Return the result.
	return value;
    }


    /** Turns the first letter of the given string into a capital letter and returns the result.
     * @param word the string whose first letter is turned into upper case
     * @return the given string with the first letter in upper case */
    public static String capitalize(String word)
    {
	// If we are dealing with an empty string, there is nothing to do.
	if (word.equals("")) {
	    return word;
	}

	// Get the first letter of the string and capitalize it.
	String firstLetter = word.substring(0,1).toUpperCase();

	// If the given string consists of one letter only, return the
	// capitalized letter.
	if (word.length() == 1) {
	    return firstLetter;
	}

	// Get the rest of the string (i.e. everything from the second letter),
	// append it to the first letter and return the result.
	return firstLetter + word.substring(1);
    }
}
