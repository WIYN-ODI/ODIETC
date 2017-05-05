package za.ac.salt.pipt.common.dataExchange;

import java.util.ArrayList;

import za.ac.salt.pipt.common.Phase;


/** This interface describes PIPT data classes. These must be able to deal with property changes and they must provide setter methods with error checking which are called safeSetField() rather than setField() for some given data field. In addition, various methods for accessing element information are provided. */
public interface PiptData extends PiptSafelyAccessible, AllowingPropertyChanges
{
    /** Returns the list of child element names and corresponding getter methods. */
    public ArrayList<PiptDataElement> getChildren();


    /** States whether the specified child element is required.
     * @param childElement the name of the child element
     * @return true if the child element is required */
    public boolean isRequired(String childElement, Phase phase);


    /** States whether all required descendants of this element actually exist.
     * @return true if all required descendants of this element exist */
    public boolean isSubTreeComplete(Phase phase);
}
