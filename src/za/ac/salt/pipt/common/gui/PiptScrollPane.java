package za.ac.salt.pipt.common.gui;

import java.awt.Component;

import javax.swing.JScrollPane;


/** This class provides the scroll pane used by the PIPT tools. It has a larger scroll increment than the standard JScrollPane class. */
public class PiptScrollPane extends JScrollPane
{
    /** the default scroll increment for this class */
    public static final int DEFAULT_SCROLL_INCREMENT = 40;


    /** Creates a scroll pane whose scroll iuncrement is set to DEFAULT_SCROLL_INCREMENT. */
    public PiptScrollPane()
    {
	super();
	getHorizontalScrollBar().setUnitIncrement(DEFAULT_SCROLL_INCREMENT);
	getVerticalScrollBar().setUnitIncrement(DEFAULT_SCROLL_INCREMENT);
    }


    /** Creates a scroll pane containing the given component in its viewport. The scroll increment is set to DEFAULT_SCROLL_INCREMENT.
     * @param view the component contained in the viewport */
    public PiptScrollPane(Component view)
    {
	super(view);
	getHorizontalScrollBar().setUnitIncrement(DEFAULT_SCROLL_INCREMENT);
	getVerticalScrollBar().setUnitIncrement(DEFAULT_SCROLL_INCREMENT);
    }
}
