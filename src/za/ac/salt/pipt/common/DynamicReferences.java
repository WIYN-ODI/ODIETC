package za.ac.salt.pipt.common;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.dataExchange.PiptData;


/** This class handles all the dynamic references between PiptData objects. It allows references to be created and removed. A dynamic reference is a reference which automatically updates its value if the referenced object changes. */
public class DynamicReferences
{
    private static DynamicReferenceHandler referenceHandler = new DynamicReferenceHandler();


    /** Creates a "dynamic reference" from a PiptData object to some other PiptData object. This means that the value of the specified field of the former is always given by the string representation of the specified field of the latter. Correspondingly, the user should be given no opportunity to change the field of the reference object. The reference can be removed by means of the removeDynamicReference() method.
     * @param referenceObject the object serving as a reference to some other object
     * @param referenceField the name of the field containing the reference (must be a String field)
     * @param referencedObject the referenced object
     * @param referencedField the name of the referenced field
     * @throws InvalidValueException if the dynamic reference cannot be created */
    public static void createDynamicReference(PiptData referenceObject, String referenceField, PiptData referencedObject, String referencedField)
    {
	Reference reference = new Reference(referenceObject, referenceField, referencedObject, referencedField);
	referenceHandler.createReference(reference);
    }


    /** Removes the given dynamic reference. If the reference occurs more than once, it is removed once only. If it doesn't occur at all, nothing is done.
     * @param referenceObject the object serving as a reference to some other object
     * @param referenceField the name of the field containing the reference (must be a String field)
     * @param referencedObject the referenced object
     * @param referencedField the name of the referenced field */
    public static void removeDynamicReference(PiptData referenceObject, String referenceField, PiptData referencedObject, String referencedField)
    {
	Reference reference = new Reference(referenceObject, referenceField, referencedObject, referencedField);
	referenceHandler.removeReference(reference);
    }
}


class DynamicReferenceHandler
{
    /** the list of references */
    private ArrayList<Reference> references = new ArrayList<Reference>();

    /** the map of references and corresponding listeners */
    private Hashtable<Reference, PropertyChangeListener> listeners = new Hashtable<Reference, PropertyChangeListener>();


    /** Creates the given reference, if doesn't exist yet. In this case, a listener for updating the reference object is added to the referenced object. */
    public void createReference(final Reference reference)
    {
	// If the reference exists already, there's nothing to do.
	if (references.contains(reference)) {
	    return;
	}

	// Create the reference.
	references.add(reference);

	// Retrieve the setter method for the reference object field and the
	// getter method for the referenced field.
	final PiptData referenceObject = reference.getReferenceObject();
	final String referenceField = reference.getReferenceField();
	final PiptData referencedObject = reference.getReferencedObject();
	final String referencedField = reference.getReferencedField();
	Method setterMethod = null;
	Method getterMethod = null;
	try {
	    setterMethod = referenceObject.getClass().getMethod("safeSet" + referenceField, String.class);
	    getterMethod = referencedObject.getClass().getMethod("get" + referencedField);
	}
	catch (NoSuchMethodException nsme) {
	    throw new InvalidValueException("A method required for the dynamic reference couldn't be created because of the following reason: " + nsme.getMessage());
	}
	final Method referenceSetterMethod = setterMethod;
	final Method referencedGetterMethod = getterMethod;
	
	// Create and add the listener.
	PropertyChangeListener listener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event)
		{
		    if (event.getPropertyName().equals(referencedField)) {
			try {
			    String referenceValue = (String) referencedGetterMethod.invoke(referencedObject);
			    referenceSetterMethod.invoke(referenceObject, referenceValue);
			}
			catch (Exception exception) {
			    throw new InvalidValueException("The reference couldn't be set because of the following reason: " + exception.getMessage());
			}
		    }
		}
	    };
	referencedObject.addPropertyChangeListener(listener);
	listeners.put(reference, listener);
    }


    /** Removes the given reference. The respective listener is removed from the referenced object. */
    public void removeReference(Reference reference)
    {
	// If the given reference doesn't exist, we don't have to do anything.
	if (!references.contains(reference)) {
	    return;
	}

	// Remove the respective listener from the referenced object and clean
	// up the maps used for book-keeping.
	reference.getReferencedObject().removePropertyChangeListener(listeners.get(reference));
	references.remove(reference);
	listeners.remove(reference);
    }
}


/** This class provides a container for all the information required for a reference from a PiptData to some other PiptData object. */
class Reference
{
    /** the reference object */
    private PiptData referenceObject;

    /** the name of the field serving as the reference */
    private String referenceField;

    /** the name of the referenced object */
    private PiptData referencedObject;

    /** the name of the referenced field */
    private String referencedField;

    /** the listener for keeping the reference up to date */
   // private PropertyChangeListener listener;

    /** the number of dynamic references of this kind */
   // private int numberOfReferences;


    /** Initializes the initial variables with the given values. It ensured that the field names start with an upper case letter.
     * @param referenceObject the reference object
     * @param referenceField thename of the field serving as the reference
     * @param referencedObject the referenced object
     * @param referencedField the referenced field */
    public Reference(PiptData referenceObject, String referenceField, PiptData referencedObject, String referencedField)
    {
	this.referenceObject = referenceObject;
	this.referenceField = referenceField.substring(0, 1).toUpperCase() + referenceField.substring(1);
	this.referencedObject = referencedObject;
	this.referencedField = referencedField.substring(0, 1).toUpperCase() + referencedField.substring(1);;
    }


    /** Returns the reference object.
     * @return the reference object */
    public PiptData getReferenceObject()
    {
	return referenceObject;
    }


    /** Returns the field serving as the reference.
     * @return the field serving as the reference */
    public String getReferenceField()
    {
	return referenceField;
    }


    /** Returns the referenced object.
     * @return the referenced object */
    public PiptData getReferencedObject()
    {
	return referencedObject;
    }


    /** Returns the referenced field.
     * @return the referenced field */
    public String getReferencedField()
    {
	return referencedField;
    }


    /** States whether this reference is equal to the given one. This is assumed to be the case if all fields are equal.
     * @param reference the reference which is compared
     * @return true if this reference and the given reference are equal */
    public boolean equals(Object reference)
    {
	Reference comparedReference = (Reference) reference;
	if (comparedReference.referenceObject.equals(this.referenceObject)
	    && comparedReference.referenceField.equals(this.referenceField)
	    && comparedReference.referencedObject.equals(this.referencedObject)
	     && comparedReference.referencedField.equals(this.referencedField)) {
	    return true;
	}
	else {
	    return false;
	}
    }
}

