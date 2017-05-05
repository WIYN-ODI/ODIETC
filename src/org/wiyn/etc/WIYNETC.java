package org.wiyn.etc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.wiyn.etc.gui.ETCLog;

/**
 * This class provides access to the RSS Simulator Tool. It provides
 * constructors for creating the respective frame from a given Rss object or an
 * input stream containing the XML content of a Rss element. A main method
 * expecting the name of an XML document containing a Rss element (and no more)
 * as an argument is given as well.
 */
public class WIYNETC extends JFrame {

    private static final long serialVersionUID = 1L;
    /** the title of the RSS Simulator Tool (to appear as the frame title) */

    private static final String ETC_TOOL_TITLE = ReleaseInfo.getInstance ()._ReleaseIdentifier_;

    private JFrame linkMe;
    WiynEtcPanel myETCPanel;
    public static ETCLog UserLogger;

    private static Logger myLogger = Logger.getLogger (WIYNETC.class);

    public WIYNETC() {
	this.linkMe = this;
	UserLogger = new ETCLog ();
	this.myETCPanel = new WiynEtcPanel ();
	startETCTool ();
    }

    /** Starts the Exposure time calculator tool in a new frame. */
    private void startETCTool () {

	// Create the menu.
	JMenuBar menuBar = new JMenuBar ();

	{
	    JMenu fileMenu = new JMenu ("File");

	    JMenuItem about = new JMenuItem ("About");
	    about.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent event) {
		    ReleaseInfo.getInstance ().about (linkMe).setVisible (true);
		}
	    });
	    // Create the menu item for exiting the program.

	    JMenuItem history = new JMenuItem ("Change Log");
	    history.addActionListener (new ActionListener () {

		public void actionPerformed (ActionEvent event) {
		    ReleaseInfo.getInstance ().history (linkMe);
		}
	    });

	    JMenuItem exitItem = new JMenuItem ("Exit");
	    exitItem.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent event) {
		    System.exit (0);
		}
	    });

	    // Put everything in place.

	    fileMenu.add (about);
	    fileMenu.add (history);
	    fileMenu.addSeparator ();
	    fileMenu.add (exitItem);

	    menuBar.add (fileMenu);

	}

	{
	    JMenu plotMenu = new JMenu ("Plot");

	    JMenuItem setYMaxItem = new JMenuItem ("Set Y Max...");
	    setYMaxItem.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent event) {
		    // TODO: Code to set Ymax of the plot here
		    myETCPanel.spectrumViewPanel.theObjectPlot.safeSetYMax (1.);
		}
	    });

	    JMenuItem setYLabelItem = new JMenuItem ("Set Y Label...");
	    setYMaxItem.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent event) {

		    myETCPanel.spectrumViewPanel.theObjectPlot
			    .safeSetYLabel ("Throughput");
		}
	    });

	    // Create the menu item for exiting the program.

	    JMenuItem savePlotItem = new JMenuItem ("Save Spectrum Plot...");
	    savePlotItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_S,
		    ActionEvent.ALT_MASK));
	    savePlotItem.addActionListener (new ActionListener () {
		public void actionPerformed (ActionEvent event) {
		    // TODO: Add code to call file saver routine here
		    System.err.println ("Save Plot Menu called");
		    myETCPanel.spectrumViewPanel.saveGraphics ();
		}
	    });

	    // Put everything in place.

	    plotMenu.add (setYMaxItem);
	    plotMenu.add (setYLabelItem);
	    plotMenu.add (savePlotItem);

	    //menuBar.add (plotMenu);

	}

	setJMenuBar (menuBar);

	// Create the rest of the frame.
	setTitle (ETC_TOOL_TITLE);

	setContentPane (this.myETCPanel);

	pack ();
	setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	setVisible (true);
    }

    /**
     * Starts the RSS Simulator Tool, setting the values from the XML document
     * specified by the command line argument.
     * 
     * @param argv
     *            the command line arguments (exactly one argument, namely the
     *            file name of the XML document, is expected)
     */

    public static void main (String[] argv) {
	BasicConfigurator.configure ();
	if (ReleaseInfo.getInstance ().isExpired ()) {

	    myLogger
		    .warn ("Warning: This application is tagged as expired since "
			    + ReleaseInfo.getInstance ()._ExpiresAt_);
	    JOptionPane
		    .showMessageDialog (null,
			    "This applciation is expired. Please update or use at your own risk.");
	}

	@SuppressWarnings("unused")
	WIYNETC rssSimulatorTool;
	rssSimulatorTool = new WIYNETC ();

    }
}
