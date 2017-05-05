package za.ac.salt.pipt.common.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import za.ac.salt.pipt.common.ErrorLog;


/** This class provides a button for loading a file into a PiptGenericTextArea component. The Java Swing JFileChooser is used for letting the user specify the file. */
public class PiptFileChooser extends JPanel
{
    /** the button for invoking the file chooser */
    private JButton chooseButton;

    /** the text area into which the file is loaded */
    private PiptGenericTextArea textArea;


    /** Creates the button for choosing the file, endowing it with the required action listener.
     * @param textArea the text area into which the file is loaded */
    public PiptFileChooser(PiptGenericTextArea textArea)
    {
	// Set the internal text area variable.
	this.textArea = textArea;

	// Create the button with its listener and add it to the panel.
	chooseButton = new JButton("Choose File");
	chooseButton.addActionListener(new ChooseButtonListener());
	add(chooseButton);
    }


    /** Enables or disables the button, according to the given parameter value.
     * @param renderActive states whether to enable (true) or disable the button (false) */
    public void setActive(boolean renderActive)
    {
	chooseButton.setEnabled(renderActive);
    }


    /** This listener invokes the file chooser and ensures that the chosen file is loaded into the text area. */
    private class ChooseButtonListener implements ActionListener
    {
	/** Lets the user choose a file by means of a JFileChooser and loads the chosen file into the text area.
	 * @param event the event responsible for the call to this method */
	public void actionPerformed(ActionEvent event)
	{
	    // Let the user choose a file.
	    JFileChooser fileChooser = new JFileChooser();
	    int option = fileChooser.showOpenDialog(PiptFileChooser.this);

	    // If the user refused to choose a file, there is nothing to do.
	    if (option != JFileChooser.APPROVE_OPTION) {
		return;
	    }

	    // Otherwise read in the chosen file...
	    File file = fileChooser.getSelectedFile();
	    StringBuilder content = new StringBuilder();
	    try {
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = fileReader.readLine()) != null) {
		    content.append(line + "\n");
		}
		fileReader.close();
	    }
	    catch (IOException ioe) {
		ErrorLog.fileNotOpenedMessage(PiptFileChooser.this, file, ioe);
		return;
	    }

	    // ... and put its content into the text area.
	    textArea.setDataObjectValue(content.toString());
	}
    }
}
