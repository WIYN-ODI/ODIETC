package za.ac.salt.pipt.common.dataExchange;



/** An instance of the PiptDataNode class contains both a PiptData and a (corresponding) node name. */
public class PiptDataNode
{
    /** the PiptData object */
    private PiptData piptData;

    /** the name of the PiptData element */
    private String name;


    /** Assigns the given values to the PiptData object and the DOM node.
     * @param piptData the PiptData object
     * @param name the name of the PiptData element */
    public PiptDataNode(PiptData piptData, String name)
    {
	this.piptData = piptData;
 	this.name = name;
    }


    /** Returns the PiptData object.
     * @return the PiptData object */
    public PiptData getPiptData()
    {
	return piptData;
    }


    /** Returns the element name
     * @return the element name */
    public String getName()
    {
	return name;
    }
}
