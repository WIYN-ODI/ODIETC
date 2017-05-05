package org.wiyn.etc.inputSpectrum.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.wiyn.etc.configuration.LunarProperties;
import org.wiyn.etc.configuration.SolarProperties;
import org.wiyn.etc.configuration.SpectrumGenerationData;
import org.wiyn.etc.configuration.TelescopeProperties;
import org.wiyn.etc.inputSpectra.SkySpectrum;
import org.wiyn.etc.inputSpectra.TargetSpectrum;
import org.wiyn.etc.inputSpectra.UBVRIMagnitudes;



/** This class provides the panel for the target and sky spectrum magnitudes in the U, B, V, R and I band. */
public class UBVRIMagnitudesPanel extends JPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the title of the panel (without the update information) */
    public static final String UBVRI_TITLE = "UBVRI Magnitudes";

    /** the update information */
    public static final String UPDATE_INFORMATION = "Out of date. Click on \"Display\" to update.";
 
    /** the target spectrum associated with this panel */
    private TargetSpectrum targetSpectrum;

    /** the label for the target spectrum U band magnitude */
    private JLabel targetULabel;
 
    /** the label for the target spectrum B band magnitude */
    private JLabel targetBLabel;
 
    /** the label for the target spectrum V band magnitude */
    private JLabel targetVLabel;
 
    /** the label for the target spectrum R band magnitude */
    private JLabel targetRLabel;
 
    /** the label for the target spectrum I band magnitude */
    private JLabel targetILabel;

    /** the label for the sky spectrum U band magnitude */
    private JLabel skyULabel;
 
    /** the label for the sky spectrum B band magnitude */
    private JLabel skyBLabel;
 
    /** the label for the sky spectrum V band magnitude */
    private JLabel skyVLabel;
 
    /** the label for the sky spectrum R band magnitude */
    private JLabel skyRLabel;
 
    /** the label for the sky spectrum I band magnitude */
    private JLabel skyILabel;

    /** the label for the update information */
    private JLabel updateInformationLabel;


    /** Creates the panel for the given spectrum generation data.
     * @param spectrumGenerationData the spectrum generation data */
    public UBVRIMagnitudesPanel(SpectrumGenerationData spectrumGenerationData)
    {
	// Create the panel. We choose a grid layout.
	super();
	setLayout(new GridLayout(3,1));

	// Retrieve the various properties from the spectrum generation data.
	SolarProperties solarProperties = spectrumGenerationData.getSolarProperties();
	LunarProperties lunarProperties = spectrumGenerationData.getLunarProperties();
	TelescopeProperties telescopeProperties = spectrumGenerationData.getTelescopeProperties();

	// Set the internal variable for the target spectrum.
	this.targetSpectrum = spectrumGenerationData.getTargetSpectrum();

	// Add a title.
	Border etchedBorder = BorderFactory.createEtchedBorder();
	Border titledBorder = BorderFactory.createTitledBorder(etchedBorder, UBVRI_TITLE);
	setBorder(titledBorder);

	// If the target spectrum, the sky spectrum or any of the solar, lunar
	// or telescope properties change, we have to inform the user that the
	// shown magnitudes are out of date.
	UpdateNeededListener updateNeededListener = new UpdateNeededListener();
	targetSpectrum.addPropertyChangeListener(updateNeededListener);
	solarProperties.addPropertyChangeListener(updateNeededListener);
	lunarProperties.addPropertyChangeListener(updateNeededListener);
	telescopeProperties.addPropertyChangeListener(updateNeededListener);

	// Add the label for the update information.
	updateInformationLabel = new JLabel("");
	add(updateInformationLabel);

	// Create the panel with the target spectrum magnitudes.
	JPanel targetMagnitudesPanel = new JPanel();
	targetMagnitudesPanel.setLayout(new GridLayout(1,0));
	targetMagnitudesPanel.add(new JLabel("U:"));
	targetULabel = new JLabel();
	targetMagnitudesPanel.add(targetULabel);
	targetMagnitudesPanel.add(new JLabel("B:"));
	targetBLabel = new JLabel();
	targetMagnitudesPanel.add(targetBLabel);
	targetMagnitudesPanel.add(new JLabel("V:"));
	targetVLabel = new JLabel();
	targetMagnitudesPanel.add(targetVLabel);
	targetMagnitudesPanel.add(new JLabel("R:"));
	targetRLabel = new JLabel();
	targetMagnitudesPanel.add(targetRLabel);
	targetMagnitudesPanel.add(new JLabel("I:"));
	targetILabel = new JLabel();
	targetMagnitudesPanel.add(targetILabel);
	add(targetMagnitudesPanel);

	// Create the panel with the sky spectrum magnitudes.
	JPanel skyMagnitudesPanel = new JPanel();
	skyMagnitudesPanel.setLayout(new GridLayout(1,0));
	skyMagnitudesPanel.add(new JLabel("U:"));
	skyULabel = new JLabel();
	skyMagnitudesPanel.add(skyULabel);
	skyMagnitudesPanel.add(new JLabel("B:"));
	skyBLabel = new JLabel();
	skyMagnitudesPanel.add(skyBLabel);
	skyMagnitudesPanel.add(new JLabel("V:"));
	skyVLabel = new JLabel();
	skyMagnitudesPanel.add(skyVLabel);
	skyMagnitudesPanel.add(new JLabel("R:"));
	skyRLabel = new JLabel();
	skyMagnitudesPanel.add(skyRLabel);
	skyMagnitudesPanel.add(new JLabel("I:"));
	skyILabel = new JLabel();
	skyMagnitudesPanel.add(skyILabel);
	add(skyMagnitudesPanel);
    }


    /** Updates all the magnitudes and removes the update information. While the target spectrum used is that of this object, the sky spectrum has to be provided.
     * @param skySpectrum the sky spectrum */
    public void updateMagnitudes(SkySpectrum skySpectrum)
    {
	updateTargetMagnitudeLabels();
	updateSkyMagnitudeLabels(skySpectrum);
	removeUpdateInformation();
    }


    /** Updates all the labels for the target spectrum magnitudes. */
    private void updateTargetMagnitudeLabels()
    {
	DecimalFormat numberFormat = new DecimalFormat();
 	numberFormat.setMinimumFractionDigits(1);
 	numberFormat.setMaximumFractionDigits(1);
	targetULabel.setText(numberFormat.format(UBVRIMagnitudes.getUMagnitude(targetSpectrum)));
	targetBLabel.setText(numberFormat.format(UBVRIMagnitudes.getBMagnitude(targetSpectrum)));
	targetVLabel.setText(numberFormat.format(UBVRIMagnitudes.getVMagnitude(targetSpectrum)));
	targetRLabel.setText(numberFormat.format(UBVRIMagnitudes.getRMagnitude(targetSpectrum)));
	targetILabel.setText(numberFormat.format(UBVRIMagnitudes.getIMagnitude(targetSpectrum)));
    }


    /** Updates all the labels for the sky spectrum magnitudes, using the given sky spectrum. */
    private void updateSkyMagnitudeLabels(SkySpectrum skySpectrum)
    {
	NumberFormat numberFormat = NumberFormat.getNumberInstance();
	numberFormat.setMaximumFractionDigits(1);
	numberFormat.setMinimumFractionDigits(1);
	skyULabel.setText(numberFormat.format(UBVRIMagnitudes.getUMagnitude(skySpectrum)));
	skyBLabel.setText(numberFormat.format(UBVRIMagnitudes.getBMagnitude(skySpectrum)));
	skyVLabel.setText(numberFormat.format(UBVRIMagnitudes.getVMagnitude(skySpectrum)));
	skyRLabel.setText(numberFormat.format(UBVRIMagnitudes.getRMagnitude(skySpectrum)));
	skyILabel.setText(numberFormat.format(UBVRIMagnitudes.getIMagnitude(skySpectrum)));
    }


    /** Adds the update information to the panel title. */
    private void addUpdateInformation()
    {
	updateInformationLabel.setForeground(Color.RED);
	updateInformationLabel.setText(UPDATE_INFORMATION);
    }


    /** Removes the update information. */
    private void removeUpdateInformation()
    {
	updateInformationLabel.setForeground(Color.BLACK);
	updateInformationLabel.setText("");
    }


    /** Listens for property changes and adds the update information to the panel title if it receives one. */
    private class UpdateNeededListener implements PropertyChangeListener
    {
	public void propertyChange(PropertyChangeEvent event)
	{
	    addUpdateInformation();
	}
    }
}
