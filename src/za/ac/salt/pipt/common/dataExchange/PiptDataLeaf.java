package za.ac.salt.pipt.common.dataExchange;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import za.ac.salt.pipt.common.Phase;


/** This class provides a PiptData implementation for a leaf, i.e. for some element which has no children. It contains a value and a name property. If an instance of this class (or rather an element corresponding to it) has a parent which accesses it (or, again, rather the corresponding element) by means of getXYZ() getter method, the name property must be the XYZ part of the getter method. As an example, assume that the instance represents an ActOnAlert child of a Proposal element, and further assume that the class representing the Proposal provides a method getActOnAlert() for accessing the ActOnAlert element. The the name property must have the value "ActOnAlert". This convention should be followed in order to allow reflection. */
public class PiptDataLeaf implements PiptData
{
    /** the name of the leaf */
    private String name;

    /** the value of the leaf */
    private Object value;

    /** an empty list (of child nodes) */
    private final ArrayList<PiptDataElement> children = new ArrayList<PiptDataElement>(0);


    /** Sets the name and value of the leaf. See above what the name is supposed to be.
     * @param name the name of the leaf */
    public PiptDataLeaf(String name, Object value)
    {
	this.name = name;
	this.value = value;
    }


    /** Returns the value of the leaf.
     * @return the value of the leaf */
    public Object getValue()
    {
	return value;
    }


    /** Returns the name of this leaf. See above what the name is supposed to be.
     * @return the name of this leaf */
    public String getName()
    {
	return name;
    }


    /** Returns the name of the leaf as the string representation.
     * @return the name of the leaf */
    public String toString()
    {
	return name;
    }


    /** Does nothing as this class doesn't support listeners.
     * @param listener the property change listenert to be added */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
    }


    /** Does nothing as this class doesn't support listeners.
     * @param listener the property change listener to be removed */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
    }


    /** Returns an empty list, as there are no children for a leaf.
     * @return an empty list */
    public ArrayList<PiptDataElement> getChildren()
    {
	return children;
    }


    /** Returns true.
     * @param childElement the name of the child element
     * @param phase the phase for vwhich the proposal is valid
     * @return true */
    public boolean isRequired(String childElement, Phase phase)
    {
	return true;
    }


    /** Returns true.
     * @param phase the phase for vwhich the proposal is valid
     * @return true */
    public boolean isSubTreeComplete(Phase phase)
    {
	return true;
    }
}
