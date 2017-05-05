package za.ac.salt.pipt.common.gui;

import javax.swing.JPanel;


/** In case an element may occur more than once (i.e., more technically, if its minOccurs attribute has a value greater than 1), it is represented by a list in the JAXB data model. This class provides the framework for a panel used for setting one of the items of this list. */
public abstract class PiptListItemPanel extends JPanel
{
    /** Renders the GUI components of this panel active or inactive, depending on the supplied parameter value
     * @param renderActive states whether to render the GUI components active */
    public abstract void setActive(boolean renderActive);
}
