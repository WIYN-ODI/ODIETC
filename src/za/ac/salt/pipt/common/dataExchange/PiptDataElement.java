package za.ac.salt.pipt.common.dataExchange;

import java.lang.reflect.Method;


/** A container for a PiptData element name and the corresponding getter method. */
public class PiptDataElement
{
    /** the element name */
    private String name;

    /** the getter method for accessing the element */
    private Method getterMethod;


    /** Does nothing. This default constructor is required by JAXB. */
    public PiptDataElement()
    {
	// do nothing
    }


    /** Sets the element name and corresponding getter method.
     * @param name the element name
     * @param getterMethod the corresponding getter method */
    public PiptDataElement(String name, Method getterMethod)
    {
	this.name = name;
	this.getterMethod = getterMethod;
    }


    /** Returns the element name.
     * @return the element name */
    public String getName()
    {
	return name;
    }


    /** Returns the getter method.
     * @return the getter method */
    public Method getGetterMethod()
    {
	return getterMethod;
    }
}
