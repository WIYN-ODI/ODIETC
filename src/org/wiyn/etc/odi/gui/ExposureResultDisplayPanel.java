package org.wiyn.etc.odi.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.wiyn.etc.configuration.PhotometryExposureResult;
import org.wiyn.etc.odi.ODI;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * The panel containing the labels for the signal-to-noise ratio and the pixel
 * saturation.
 */
public class ExposureResultDisplayPanel extends JPanel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /** the formatter used for the values */
    private NumberFormat twoFractionDigits;

    private JLabel snrLabel;
    private JLabel SkyLevelLabel;
    private JLabel SkyNoiseLabel;
    private JLabel PeakLevelLabel;
    private JLabel PhotErrLabel;
    private JLabel TotalFluxLabel;
    private SNRTable ResultsTable;

    /** the RSS setup associated with this panel */

    /** Creates the panel. */
    public ExposureResultDisplayPanel() {
	// Set the internal variable.

	Box outerBox = Box.createVerticalBox ();
	add (outerBox);

	JPanel innerArea1 = new JPanel ();
	outerBox.add (innerArea1);
	// Set the formatter. Two fraction digits are to be used.
	twoFractionDigits = NumberFormat.getInstance ();
	twoFractionDigits.setMinimumFractionDigits (2);
	twoFractionDigits.setMaximumFractionDigits (2);

	// Create all the labels...

	snrLabel = new JLabel ();
	SkyLevelLabel = new JLabel ();
	SkyNoiseLabel = new JLabel ();
	PeakLevelLabel = new JLabel ();
	PhotErrLabel = new JLabel ();
	TotalFluxLabel = new JLabel ();
	ResultsTable = new SNRTable ();

	innerArea1.setLayout (new GridLayout (8, 2));
	innerArea1.add (new JLabel ("Sky Level:  ", SwingConstants.RIGHT));
	innerArea1.add (SkyLevelLabel);
	innerArea1.add (new JLabel ("Background Noise (per pixel):  ",
		SwingConstants.RIGHT));
	innerArea1.add (SkyNoiseLabel);
	innerArea1.add (new JLabel ("Total Flux from Object:  ",
		SwingConstants.RIGHT));
	innerArea1.add (TotalFluxLabel);
	innerArea1.add (new JLabel ("Peak level above background:  ",
		SwingConstants.RIGHT));
	innerArea1.add (PeakLevelLabel);
	innerArea1.add (new JLabel ("Signal/Noise:  ", SwingConstants.RIGHT));
	innerArea1.add (snrLabel);
	innerArea1
		.add (new JLabel ("Photometry Error:  ", SwingConstants.RIGHT));
	innerArea1.add (PhotErrLabel);
	PhotErrLabel.setPreferredSize (new java.awt.Dimension (88, 15));

	outerBox.add (ResultsTable);
	// ResultsTable.setPreferredSize(new java.awt.Dimension(356, 74));
	Border etchedBorder = BorderFactory.createEtchedBorder ();
	Border titledBorder = BorderFactory.createTitledBorder (etchedBorder,
		"Photometry Results");
	setBorder (titledBorder);
    }

    /**
     * Updates the labels showing the signal-to-noise ratio and the pixel
     * saturation.
     * 
     * @param exposure
     *            the exposure assumed for updating
     */
    public void update (Vector<PhotometryExposureResult> Results) {

	if (Results == null)
	    return;
	PhotometryExposureResult Result = Results.elementAt (0);
	SkyLevelLabel.setText (twoFractionDigits.format (Result.SkyLevel));

	SkyNoiseLabel.setText (twoFractionDigits.format (Result.SkyNoise));

	StringBuffer PeakText = new StringBuffer (
		twoFractionDigits.format (Result.PeakLevel));
	if (Result.PeakLevel + Result.SkyLevel >= ODI.theODI
		.getSaturationLevel ()) {
	    PeakText.append (" Saturation Warning:!");
	    PeakLevelLabel.setForeground (Color.red);
	} else {
	    PeakLevelLabel.setForeground (Color.black);
	}
	PeakLevelLabel.setText (PeakText.toString ());
	TotalFluxLabel.setText (twoFractionDigits.format (Result.TotalFlux));

	// Depending on the S/N, the choice of the aperture effects the best S/N
	// One could be smarter and choose the optimum aperture from the
	// beginning.
	double sn = Result.SN;
	for (java.util.Iterator<PhotometryExposureResult> it = Results
		.iterator (); it.hasNext ();) {
	    double currentSN = it.next ().SN;
	    if (currentSN > sn)
		sn = currentSN;
	}

	snrLabel.setText (twoFractionDigits.format (sn));

	PhotErrLabel.setText (twoFractionDigits.format (0.92 / sn));

	ResultsTable.update (Results);

    }

    /**
     * Sets the labels showing the signal-to-noise ratio and the pixel
     * saturation to the value for NaN numbers.
     */
    public void setUndefined () {

    }

}
