package org.wiyn.etc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class ReleaseInfo {
    private static Logger myLogger = Logger.getLogger (ReleaseInfo.class);
 
    
    private static ReleaseInfo theInfo;

    public String _ReleaseIdentifier_;
    public float _Version_;
    public Date _ReleaseDate_;

    public Date _ValidFrom_;
    public Date _ExpiresAt_;

    public String _ReleaseComment_;
    String aboutString;

   

    private ReleaseInfo() {

	this._Version_ = 4f;
	this._ReleaseComment_ = "Proposal Cycle 2017B Release";
	this._ReleaseDate_ = (new GregorianCalendar (2017, Calendar.MAY,
		1)).getTime ();

	this._ValidFrom_ = _ReleaseDate_;

	this._ExpiresAt_ = (new GregorianCalendar (2020, Calendar.JANUARY, 1))
		.getTime ();
	this._ReleaseIdentifier_ = new String (
		"ODI Exposure Time Calculator Version " + _Version_ + " "
			+ this._ReleaseComment_);

	this.aboutString = this.getAboutMessage ();

    }

    public static ReleaseInfo getInstance () {

	if (theInfo == null)
	    theInfo = new ReleaseInfo ();

	return theInfo;

    }

    public boolean isExpired () {

	Date now = new Date (System.currentTimeMillis ());

	if (_ExpiresAt_ != null && _ExpiresAt_.before (now)) {
	    System.err
		    .println ("Warning: This appliction has expired on: "
			    + _ExpiresAt_
			    + " \n "
			    + "Results of this Applications are probably not approiat for use any more. Please check for updates.");
	    return true;
	}

	return false;

    }

    public JDialog about (JFrame owner) {

	JLabel Message = new JLabel (this.aboutString);

	JOptionPane p = new JOptionPane (Message,
		JOptionPane.INFORMATION_MESSAGE);

	JDialog r = p.createDialog (owner, "About");

	return r;
    }

    public void history (JFrame owner) {

	
	JTextPane tp = new JTextPane ();
	JScrollPane js = new JScrollPane ();
	js.getViewport ().add (tp);

	tp.setSize (500, 400);

	try {
	    URL url = ReleaseInfo.class.getClassLoader ().getResource (
		    "resources/etc/Changelog.html");
	    if (url != null)
		tp.setPage (url);
	} catch (Exception e) {
	    myLogger.error ("Error reading Changelog message", e);
	}
	tp.setEditable (false);

	JOptionPane.showMessageDialog (owner, js);

    }

    private String getAboutMessage () {
	InputStream aboutStream = null;

	StringBuffer sb = new StringBuffer ();

	try {
	    aboutStream = this.getClass ().getClassLoader ()
		    .getResourceAsStream ("resources/etc/"  + "about.html");
	    BufferedReader reader = new BufferedReader (new InputStreamReader (
		    aboutStream));
	    while (reader.ready ()) {

		sb.append (reader.readLine ());
	    }
	} catch (Exception e) {
	    myLogger.error ("Could not read about message: ", e);
	    return ("Error: No about message available.");
	}

	String result = sb.toString ();

	Field[] f = getClass ().getFields ();
	// System.err.println ("Size of field array:" + f.length);
	for (int ii = 0; ii < f.length; ii++) {
	    Field myField = f[ii];

	    String name = myField.getName ();
	    // System.err.println (name);

	    if (name.matches ("_.*_")) {
		// System.err.println (" + " + name);

		try {
		    result = result.replaceAll (name, myField.get (this)
			    .toString ());
		} catch (IllegalArgumentException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace ();
		} catch (IllegalAccessException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace ();
		}
	    }

	}
	// System.err.println (result);
	return result;
    }

    public static void main (String[] s) {
	BasicConfigurator.configure ();
	// JDialog about = ReleaseInfo.getInstance ().about (null);

	// about.setVisible (true);

	ReleaseInfo.getInstance ().history (null);

    }

}
