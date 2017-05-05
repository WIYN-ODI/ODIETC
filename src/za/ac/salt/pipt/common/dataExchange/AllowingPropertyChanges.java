package za.ac.salt.pipt.common.dataExchange;

import java.beans.PropertyChangeListener;


/** This interface provides the methods for adding and removing property change listeners. */
public interface AllowingPropertyChanges
{
    /** Adds the given listener to the list of property change listeners.
     * @param listener the property change listenert to be added */
    public void addPropertyChangeListener(PropertyChangeListener listener);


    /** Removes the given listener from the list of property change listeners.
     * @param listener the property change listener to be removed */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
