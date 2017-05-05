package org.wiyn.etc.inputSpectrum.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.wiyn.etc.inputSpectra.TargetSpectrum;
import org.wiyn.etc.inputSpectra.UserSuppliedSpectrum;
import org.wiyn.etc.inputSpectra.VNormalizedUserSuppliedSpectrum;

import za.ac.salt.pipt.common.GenericSpectrum;


/** This class provides a panel for including (or excluding) a user-supplied spectrum in the target spectrum calculation and for setting its URL. The user is not prompted for the URL but for a local file, which is internally converted into a URL. The user-supplied spectrum is normalized to match a V magnitude. */
public class UserSuppliedSpectrumPanel extends SpectrumPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the maximum number of characters shown for a URL */
    public static final int MAXIMUM_URL_CHARACTERS = 45;

    /** a brief data description (to be used in a tool tip) */
    public static final String DATA_DESCRIPTION
	= "The data must be arranged as follows:\nColumn 1: wavelength in Angstrom\nColumn 2: flux in erg/cm^2/s/A";

    /** the user-supplied spectrum associated with this panel */
    private UserSuppliedSpectrum userSuppliedSpectrum;

    /** a label for the filename */
    private JLabel urlLabel;


    /** Creates the panel for the given user-supplied spectrum.
     * @param normalizedUserSuppliedSpectrum the V magnitude normalized user-supplied spectrum associated with this panel
     * @param targetSpectrum the target spectrum */
    public UserSuppliedSpectrumPanel(VNormalizedUserSuppliedSpectrum normalizedUserSuppliedSpectrum, TargetSpectrum targetSpectrum)
    {
	super(normalizedUserSuppliedSpectrum, targetSpectrum);
    }


    /** Returns the panel for setting the URL of the data for the given user-supplied spectrum. The user is asked for a local file rather than URL; the required conversion is arried out automatically. If the given spectrum doesn't constitute a user-supplied spectrum, the stack trace is output and an empty panel is returned instead.
     * @param spectrum the spectrum (must be of the type UserSuppliedSpectrum)
     * @return the panel for setting the URL of the given spectrum */
    public JPanel propertiesPanel(final GenericSpectrum spectrum)
    {
	// Initialize the label for the URL.
	urlLabel = new JLabel();

	// Create the panel with a flow layout (there will be one row only,
	// anyway).
	JPanel pane = new JPanel();
	pane.setLayout(new FlowLayout());

	// Check whether the given spectrum really is a user-supplied spectrum.
	if (!(spectrum instanceof UserSuppliedSpectrum)) {
	    (new Exception("The given spectrum doesn't constitute a user-supplied spectrum.")).printStackTrace();
	    return pane;
	}

	// As we now know that the given spectrum really is a user-supplied
	// spectrum, we may perform a respective cast.
	userSuppliedSpectrum = (UserSuppliedSpectrum) spectrum;

	// Add a button for opening a file selection dialog.
	JButton fileSelectionButton = new JButton("Choose File");
	fileSelectionButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event)
		{
		    // Let the user choose the file.
		    chooseFile();

		    // Update the label for the URL.
		    updateURLLabel();
		}
	    });
	fileSelectionButton.setToolTipText("Click this to select a file. " + DATA_DESCRIPTION);
	pane.add(fileSelectionButton);

	// In order to avoid confused users, we better indicate that either a
	// file or a URL has to be chosen.
	pane.add(new JLabel("or"));

	// Add a button for choosing a URL.
	JButton urlSelectionButton = new JButton("Choose URL");
	urlSelectionButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event)
		{
		    // Let the user choose the URL.
		    chooseURL();

		    // Update the label for the URL.
		    updateURLLabel();
		}
	    });
	urlSelectionButton.setToolTipText("Click this to choose a URL. " + DATA_DESCRIPTION);
	pane.add(urlSelectionButton);

	// Add a label for introducing the URL.
	pane.add(new JLabel("URL:"));

	// Get the text for the URL label and add the label.
	updateURLLabel();
	pane.add(urlLabel);

	// Return the panel.
	return pane;
    }


    /** Lets the user choose a file, converts the choice into a URL and sets the URL variable of the user-supplied spectrum associated with this panel to that value. If the user chooses a non-existing file (which includes choosing a directory), they get a warning and may choose again. */
    private void chooseFile()
    {
	// We have to keep track of whether a valid file has been chosen.
	boolean validFileChosen = false;

	// While the user hasn't chosen a valid file (or has to chosen to cancel
	// the file selection), we have to ask for a choice.
	File file =null;
	while (!validFileChosen) {
	    // Let the user chooser a file by means of a file selection dialog.
	    JFileChooser fileChooser = new JFileChooser();
	    int result = fileChooser.showOpenDialog(this);

	    // If the user wants to cancel, so be it.
	    if (result == JFileChooser.CANCEL_OPTION) {
		break;
	    }

	    // On the other hand, if the user has chosen a file, retrieve its
	    // name and check whether its valid.
	    if (result == JFileChooser.APPROVE_OPTION) {
		file = fileChooser.getSelectedFile();
		if (file.isFile()) {
		    validFileChosen = true;
		}
		else {
		    JLabel noValidFileLabel 
			= new JLabel("<html>The choice<br><br>" + file.getName() + "<br><br>doesn't constitute a valid file. Please choose again.");
		    JOptionPane.showMessageDialog(this, noValidFileLabel, "No valid file", JOptionPane.ERROR_MESSAGE);
		}
	    }
	}

	// If the user has chosen a valid file, obtain the corresponding URL
	// and assign this URL to the user-supplied spectrum. In case the
	// conversion into a URL fails, we just output the stack trace.
	if (validFileChosen) {
	    try {
		userSuppliedSpectrum.setURL(file.toURL());
	    }
	    catch (MalformedURLException mue) {
		mue.printStackTrace();
		return;
	    }
	}
    }


    /** Lets the user enter a URL for the user-supplied spectrum associated with this panel. If the current value of the respective variable is non-null, it is offered as the default choice. In case an invalid URL is entered, a respective error message is issued and the user is prompted for another URL. */
    private void chooseURL()
    {
	// We have to keep track of whether a valid URL has been provided.
	boolean validURLProvided = false;

	// While the user hasn't provided a valid file or has decided to cancel,
	// we prompt for a URL.
	URL url = null;
	String defaultURL = null;
	while (!validURLProvided) {
	    // If no default URL has been provided yet, choose the URL of the
	    // user-supplied spectrum (unless it is null, in which case simply
	    // choose an empty string).
	    if (defaultURL == null) {
		if (userSuppliedSpectrum.getURL() != null) {
		    defaultURL = userSuppliedSpectrum.getURL().toString();
		}
		else {
		    defaultURL = "";
		}
	    }

	    // Get the URL from the user.
	    JLabel urlInputLabel = new JLabel("Please enter a URL.");
	    String urlInput = JOptionPane.showInputDialog(this, urlInputLabel, defaultURL);

	    // If the object returned by the dialog is null, the user has
	    // decided to cancel. We honor the decision and exit the loop.
	    if (urlInput == null) {
		break;
	    }

	    // Otherwise we try to create a URL object from the input. If that
	    // cannot be achieved successfully, an error message is issued (and
	    // the user will be prompted for another URL, using the current
	    // input as the default value). If it can be achieved, however, we
	    // record the fact that a valid URL has been given.
	    try {
		url = new URL(urlInput);
		validURLProvided = true;
	    }
	    catch (MalformedURLException mue) {
		JLabel noValidURLLabel
		    = new JLabel("<html>The chosen URL<br><br>" + urlInput + "<br><br>is no valid URL. Please enter a correct URL.</html>");
		JOptionPane.showMessageDialog(this, noValidURLLabel, "No valid URL", JOptionPane.ERROR_MESSAGE);
		defaultURL = urlInput;
	    }
	}
	
	// If a valid URL has been provided, assign it to the user-supplied
	// spectrum.
	if (validURLProvided) {
	    userSuppliedSpectrum.setURL(url);
	}
    }


    /** Sets the URL of the user-supplied spectrum associated with this panel as the text of the URL label. If the URL object of the spectrum is null, choose "N/A" as the text. In case the URL is longer than the maximum number of characters, its middle part is truncated. */
    private void updateURLLabel()
    {
        URL url = userSuppliedSpectrum.getURL();
	if (url != null) {
	    String urlText = url.toString();
	    if (urlText.length() > MAXIMUM_URL_CHARACTERS) {
		// Get half the maximum allowed URL length, taking the omission
		// string "..." (which will be inserted in the middle) into
		// account.
		int halfLength = (MAXIMUM_URL_CHARACTERS - 3) / 2;

		// Introduce variables for the number of characters we will use
		// from the beginning and the end of URL. If 2 * halfLength is
		// one less than the allowed number of characters, we get one
		// character more from the end.
		int beginningLength = halfLength;
		int endLength = (2 * halfLength == MAXIMUM_URL_CHARACTERS - 3)
				 ? halfLength : halfLength + 1;

		// Get the letters from the beginning and end of the URL.
		String beginningPart = urlText.substring(0, beginningLength);
		String endPart = urlText.substring(urlText.length() - endLength);

		// Replace the middle part of the URL by the omission string
		// "...".
		urlText = beginningPart + "..." + endPart;
	    }

	    // Set the text of the URL label.
	    urlLabel.setText(urlText);
	}
	else {
	    urlLabel.setText("N/A");
	}
    }
}

