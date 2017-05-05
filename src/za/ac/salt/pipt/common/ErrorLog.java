package za.ac.salt.pipt.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;
import za.ac.salt.pipt.common.gui.PiptScrollPane;


/** This class provides a simple error log shared by all PIPT tools. It allows to show error messages in an own frame. To this end, various convenience methods are provided. */
public class ErrorLog
{
    /** the size of the error log frame */
    private static final Dimension ERROR_LOG_FRAME_SIZE = new Dimension(300, 200);

    /** the minimum distance between the error log frame and the edge of the screen (in pixels) */
    private static final int MINIMUM_OFFSET = 70;

    /** the frame for the error messages */
    private static JFrame errorFrame;

    /** the text area for the error messages */
    private static JEditorPane errorPane;

    // Initialize the frame for the error messages.
    static {
	// Get the text pane for the error messages.
	errorPane = new JEditorPane();
	errorPane.setContentType("text/html");
	errorPane.setEditable(false);

	// Put the editor pane into a scrollable pane.
	PiptScrollPane scrollPane = new PiptScrollPane(errorPane);

	// Create a button for closing the frame.
	JButton closeButton = new JButton("Close");
	closeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event)
		{
		    errorFrame.setVisible(false);
		}
	    });
	JPanel closeButtonPane = new JPanel();
	closeButtonPane.add(closeButton);

	// Set the content pane. A plain border is added.
	JPanel contentPane = new JPanel();
	contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	contentPane.setLayout(new BorderLayout());
	contentPane.add(scrollPane, BorderLayout.CENTER);
	contentPane.add(closeButtonPane, BorderLayout.SOUTH);

	errorFrame = new JFrame("Error");
	errorFrame.setSize(ERROR_LOG_FRAME_SIZE);
	errorFrame.setContentPane(contentPane);
    }


    /** Returns the location of the origin (i.e. the upper left corner) of the error log frame. If possible this location is chosen so that the centers of the error log frame and the given parent component coincide. However, it is ensured that the entire frame is visible on the screen.
     * @param parent the parent component
     * @return the location of the error log frame origin on the screen */
    public static Point getLocation(JComponent parent)
    {
	// If there is no parent component (i.e. if it is null), we return a
	// default offset.
	if (parent == null) {
	    return new Point(MINIMUM_OFFSET, MINIMUM_OFFSET);
	}

	// Establish the coordinates of the parent's center.
	Point parentLocation = new Point(MINIMUM_OFFSET, MINIMUM_OFFSET);
	try {
	    parentLocation = parent.getLocationOnScreen();
	}
	catch (IllegalComponentStateException icse) {
	    // Nothing has to be done, as a default location of
	    // (MINIMUM_OFFSET, MINIMUM_OFFSET) as set above is reasonable for
	    // this case.
	}
	int parentWidth = parent.getSize().width;
	int parentHeight = parent.getSize().height;
	int parentCenterX = parentLocation.x + parentWidth / 2;
	int parentCenterY = parentLocation.y + parentHeight / 2;

	// If possible, the center of the error log frame should coincide with
	// that of the parent.
	int errorFrameWidth = ERROR_LOG_FRAME_SIZE.width;
	int errorFrameHeight = ERROR_LOG_FRAME_SIZE.height;
	int errorFrameOriginX = parentCenterX - errorFrameWidth / 2;
	int errorFrameOriginY = parentCenterY - errorFrameHeight / 2;

	// However, we demand that the whole frame is visible on the screen.
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	if (errorFrameOriginX < MINIMUM_OFFSET) {
	    errorFrameOriginX = MINIMUM_OFFSET;
	}
	if (errorFrameOriginY < MINIMUM_OFFSET) {
	    errorFrameOriginY = MINIMUM_OFFSET;
	}
	if (errorFrameOriginX + errorFrameWidth > screenSize.width - MINIMUM_OFFSET) {
	    errorFrameOriginX = screenSize.width - errorFrameWidth - MINIMUM_OFFSET;
	}
	if (errorFrameOriginY + errorFrameHeight > screenSize.height - MINIMUM_OFFSET) {
	    errorFrameOriginY = screenSize.height - errorFrameHeight -MINIMUM_OFFSET;
	}

	// Return the location of the origin thus found.
	return new Point(errorFrameOriginX, errorFrameOriginY);
    }


    /** Repaints the error log frame with the given error message
     * @param parent the parent component used for positioning the error log frame
     * @param errorMessage the error message */
    public static void repaint(JComponent parent, String errorMessage)
    {
	// Set the location of the origin of the error log frame.
	errorFrame.setLocation(getLocation(parent));

	// Create the error text.
	String errorText = "<html><h2>An error has occured</h2><p>"
	    + errorMessage + "</p></html>";

	// Set the error text.
	errorPane.setText(errorText);

	// Repaint the error log frame and render it visible.
	errorFrame.repaint();
	errorFrame.setVisible(true);
    }


    /** Displays the (not particularly informative) message that the given value isn't valid.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param invalidValue the value which isn't valid */
    public static void invalidValueMessage(JComponent parent, Object invalidValue)
    {
	// Obtain the error message.
	String errorMessage = "The value " + invalidValue + " is invalid and has been rejected.";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message which informs the user that the given value (the string representation of which is shown) doesn't constitute a number.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param noNumberValue the value which is no number */
    public static void noNumberMessage(JComponent parent, Object noNumberValue)
    {
	// Obtain the error message.
	String errorMessage = "The value <code>" + noNumberValue
	    + "</code> doesn't constitute a number. It has been rejected.";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message which informs the user that the given value (the string representation of which is shown) doesn't constitute an integer.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param noIntegerValue the value which is no integer */
    public static void noIntegerMessage(JComponent parent, Object noIntegerValue)
    {
	// Obtain the error message.
	String errorMessage = "The value <code>" + noIntegerValue
	    + "</code> doesn't constitute an integer. It has been rejected.";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message which informs the user that the given value (the string representation of which is shown) doesn't constitute a boolean.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param noBooleanValue the value which is no boolean */
    public static void noBooleanMessage(JComponent parent, Object noBooleanValue)
    {
	// Obtain the error message.
	String errorMessage = "The value <code>" + noBooleanValue
	    + "</code> doesn't constitute an integer. It has been rejected.";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message which informs the user that the given value has been rejected. Both the string representation of the rejected value and the reason why it is invalid are given. The latter is taken from the supplied InvalidValueException.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param rejectedValue the rejected value
     * @param invalidValueException the InvalidValueException because of which the values has been rejected */
    public static void rejectedValueMessage(JComponent parent, Object rejectedValue, InvalidValueException invalidValueException)
    {
	// Obtain the error message.
	String errorMessage = "The value <code>"
	    + rejectedValue
	    + "</code> is invalid because of the following reason:<br><br><em>"
	    + invalidValueException.getMessage()
	    + "</em><br><br>The value has been rejected.";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message which informs the user that the given index value lies outside the given allowed range.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param index the forbidden index value
     * @param minimumValue the minimum allowed value
     * @param maximumValue the maximum allowed value */
    public static void indexOutOfRangeMessage(JComponent parent, int index, int minimumValue, int maximumValue)
    {
	// Create the error message.
	String errorMessage = "The index value " + index
	    + " lies outside the allowed range from " + minimumValue + " to "
	    + maximumValue + ".";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message which informs the user that the given file couldn't be saved, including the respective message of the given IOException.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param file the file which couldn't be saved
     * @param ioe the IOException thrown during the attempt to save the file */
    public static void fileNotSavedMessage(JComponent parent, File file, IOException ioe)
    {
	// Create the error message.
	String errorMessage = "<html>The file <code>" + file.getName()
	    + "</code> couldn't be saved due to the following reason:<br><br><em>"
	    + ioe.getMessage() + "</em></html>";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message which informs the user that the given file couldn't be opened, including the respective message of the given IOException.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param file the file which couldn't be opened
     * @param ioe the IOException thrown during the attempt to save the file */
    public static void fileNotOpenedMessage(JComponent parent, File file, IOException ioe)
    {
	// Create the error message.
	String errorMessage = "<html>The file <code>" + file.getName()
	    + "</code> couldn't be saved due to the following reason:<br><br><em>"
	    + ioe.getMessage() + "</em></html>";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message informing the user that the given file contains invalid XML content, giving the respective error message as well.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param je the JAXBException thrown during the validation */
    public static void invalidInitializationXmlMessage(JComponent parent, JAXBException je)
    {
	// Create the error message.
	String errorMessage = "<html>The XML content used for initialization is invalid due to the following reason:<br><br><em>"
	    + je.getMessage()
	    + "</em><br><br>The application cannot be started.</html>";

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message informing the user that the validation of some XML content failed, giving the respective error message as well. 
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param se the SAXException containing the reason why the validation failed */
    public static void validationFailedMessage(JComponent parent, SAXException se)
    {
	// Create the error message.
	String errorMessage = "<html>The validation of the XML failed because of the following error:<br><br><em>"
	    + se.getMessage();

	// Repaint the error log frame with this error message.
	repaint(parent, errorMessage);
    }


    /** Displays a message with the given error message.
     * @param parent the component that is used for positioning the error log frame (may be <code>null</code>)
     * @param errorMessage the error message to be displayed */
    public static void generalErrorMessage(JComponent parent, String errorMessage)
    {
	// Repaint the error log frame with the given error message.
	repaint(parent, errorMessage);
    }
}
