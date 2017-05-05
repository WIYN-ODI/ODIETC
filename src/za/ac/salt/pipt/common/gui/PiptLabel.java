package za.ac.salt.pipt.common.gui;

import java.awt.Color;

import javax.swing.JLabel;


/** This class provides a JLabel object with a tool tip. The label may be rendered active (i.e. the text is black) or inactive (i.e. the text is gray). */
public class PiptLabel extends JLabel
{
    /** the variable recording whether the label is active */
    private boolean active = true;

    /** Creates the label with the given text and sets its tool tip text to the given value.
     * @param text the text of the label
     * @param toolTipText the text of the tool tip for this label */
    
    public PiptLabel () {
	this (null, null);
    }
    
    public PiptLabel(String text, String toolTipText)
    {
	super(text);
	setToolTipText(toolTipText);
    }

    /** Renders the label active (i.e. the text is black) or inactive (i.e. the text is gray), depending on whether the given value is true or false.
     * @param active states whether the label should be rendered active (true) or inactive (false) */
    public void setActive(boolean active)
    {
	this.active = active;
	if (active) {
	    setForeground(Color.BLACK);
	}
	else {
	    setForeground(Color.GRAY);
	}
    }


    /** Returns whether the label is active.
     * @return true id the label is active */
    public boolean isActive()
    {
	return active;
    }
}
