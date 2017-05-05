package org.wiyn.etc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import za.ac.salt.pipt.common.Grid;
import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptStringTextField;

/**
 * A Panel that arranges two SpectrumPlots in a Panel.
 */
public class SpectrumViewPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    double[] xrange = { 3200, 11000 };
    double[] percentRange = { 0, 1.0 };
    private String annotationText = getAnnotationMessage ();

    public static ETCSpectrumPlotPanel theObjectPlot = null;

    private String getAnnotationMessage () {

	StringBuffer sb = new StringBuffer ();
	try {
	    InputStream aboutStream = this.getClass ().getResourceAsStream (
		    "/resources" + "/" + "SpectrumPlotAnnotations.html");
	    BufferedReader reader = new BufferedReader (new InputStreamReader (
		    aboutStream));
	    while (reader.ready ()) {

		sb.append (reader.readLine ());
	    }
	} catch (Exception e) {
	    System.err.println (e);
	}

	return sb.toString ();
    }

    public ETCSpectrumPlotPanel getSpectrumPlotPanel () {

	return theObjectPlot;
    }

    public SpectrumViewPanel() {

	theObjectPlot = new ETCSpectrumPlotPanel (null, "Object Spectrum",
		"Flux", xrange, null, false, false);
	BorderLayout myLayout = new BorderLayout ();

	this.setLayout (myLayout);

	this.add (theObjectPlot, BorderLayout.CENTER);

	{
	    JPanel TweakBox = new JPanel ();
	    TweakBox.setLayout (new FlowLayout ());

	    PiptStringTextField t = new PiptStringTextField (theObjectPlot,
		    "labelText");
	    t.setColumns (20);
	    TweakBox.add (new PiptLabel ("Set Title:", ""));
	    TweakBox.add (t, "1,0");

	    TweakBox.setMaximumSize (getPreferredSize ());
	    this.add (TweakBox, BorderLayout.SOUTH);
	}

	JComponent Annotations = new JLabel (annotationText);
	Annotations.setMaximumSize (new Dimension (400, 30));
	// this.add (Annotations, BorderLayout.SOUTH);
    }

    public static void updateObjectSpectrum (Grid objectSpectrum,
	    Grid skySpectrum, double[] yrange) {
	theObjectPlot.updateSpectrum (objectSpectrum, skySpectrum, yrange);
    }

    // public static void updateSkySpectrum (Grid skySpectrum, double[] yrange)
    // {
    // theObjectPlot.updateSpectrum (skySpectrum, yrange,
    // ETCSpectrumPlotPanel.SKYSPECTRUM);
    // }

    public static void updateThroughput (Grid throughput, double[] range) {
	double scale = theObjectPlot.getYmaxPlot ();
	if (scale == 0)
	    scale = 1;
	Grid scaled = new Grid (throughput);
	scaled.resample (1);
	scaled.write ("througput.dat");
	// scaled.scale (scale);
	theObjectPlot.updateThroughput (scaled);
    }

    JFileChooser myFileChooser;

    public void saveGraphics () {
	File f;
	if (myFileChooser == null) {
	    myFileChooser = new JFileChooser ();
	    myFileChooser.setDialogType (JFileChooser.SAVE_DIALOG);
	}

	f = new File (SpectrumViewPanel.theObjectPlot.getLabelText ());
	myFileChooser.setSelectedFile (f);
	int returnVal = myFileChooser.showSaveDialog (this);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    System.out.println ("You chose to open this file: "
		    + myFileChooser.getSelectedFile ().getName ());
	    f = myFileChooser.getSelectedFile ();
	    if (f.exists ()) {
		System.err.println ("Cannot overwrite files!!!");
		return;
	    }
	    try {
		ImageIO.write (theObjectPlot.myChart.createBufferedImage (800,
			600), "png", f);
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		System.err.println ("Saving graph to image " + f + " failed.");
	    }
	}

    }
}
