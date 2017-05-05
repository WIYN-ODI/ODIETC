package org.wiyn.etc.configuration;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import za.ac.salt.pipt.common.dataExchange.AllowingPropertyChanges;


/** This is the superclass of all the classes describing RSS related properties. It provides the methods for adding and removing property change listeners and for firing property change events. */
public class GenericProperties implements AllowingPropertyChanges
{
    /** supports handling property changes */
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


    /** Fires a property change event with the given property name, old value and new value.
     * @param propertyName the property name
     * @param oldValue the old value
     * @param newValue the new value */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
	propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }


    /** Adds the given listener to the list of property change listeners.
     * @param listener the property change listener to be added */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
	propertyChangeSupport.addPropertyChangeListener(listener);
    }


    /** Removes the given listener from the list of property change listeners.
     * @param listener the property change listener to be removed */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
	propertyChangeSupport.removePropertyChangeListener(listener);
    }
}
