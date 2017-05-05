package org.wiyn.etc.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class ETCLog extends AppenderSkeleton {

    private JTextArea logView;
    private JPanel myPanel = null;

    public ETCLog() {

	myPanel = new JPanel ();

	logView = new JTextArea (30, 120);
	logView.setFont (new Font ("Courier", Font.PLAIN, 13));
	JScrollPane scroll = new JScrollPane (logView,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	myPanel.add (scroll, BorderLayout.CENTER);

    }

    public JPanel getPanel () {

	return myPanel;
    }

    public void appendLine (String Line) {

	if (logView != null && Line != null) {
	    String myLine = Line;
	    if (!Line.endsWith ("\n")) {
		myLine = Line + "\n";

	    }
	    logView.append (myLine);
	    if (logView.getLineCount () > 500) {
		try {
		    logView.replaceRange (null, logView.getLineStartOffset (0),
			    logView.getLineEndOffset (0));
		} catch (BadLocationException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace ();
		}
	    }
	}

    }

    public static void main (String[] args) {

	ETCLog myLog = new ETCLog ();

	JFrame f = new JFrame ();
	f.getContentPane ().add (myLog.getPanel ());
	f.pack ();
	f.setVisible (true);

	for (int ii = 0; ii < 500; ii++) {

	    myLog.appendLine ("TEst line number: " + ii + "\n");

	}

    }

    @Override
    protected void append (LoggingEvent logEvent) {
	this.appendLine (logEvent.getMessage ().toString ());

    }

    public void close () {
	// TODO Auto-generated method stub

    }

    public boolean requiresLayout () {
	// TODO Auto-generated method stub
	return false;
    }

}
