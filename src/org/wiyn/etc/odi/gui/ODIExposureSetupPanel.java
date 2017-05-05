package org.wiyn.etc.odi.gui;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import layout.TableLayout;

import org.apache.log4j.BasicConfigurator;
import org.wiyn.etc.configuration.ExposureConfig;
import org.wiyn.etc.odi.ODI;

import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.gui.PiptIntegerSpinner;
import za.ac.salt.pipt.common.gui.PiptLabel;
import za.ac.salt.pipt.common.gui.PiptNumberTextField;
import za.ac.salt.pipt.common.gui.PiptStringComboBox;

public class ODIExposureSetupPanel extends javax.swing.JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -5388203004604751347L;
    ExposureConfig myExposureConfig = null;
    private JLabel ExposureTimeLabel;

    private JLabel RepeatExposureLabel;
    // private ReadoutModePanel readoutModePanel1;
    private JComboBox ODIFilterCombo;
    // private ODIFilterOnlyPanel myFilterOnlyPanel;
    private JLabel ODIFilterLabel;
    private JLabel ReadoutModeLabel;
    private JLabel QELabel;
    private JLabel BinningLabel;

    public ODIExposureSetupPanel(ExposureConfig exposureConfig) {

	// GridLayout thisLayout = new GridLayout (6, 2);
	// thisLayout.setHgap (2);
	// thisLayout.setVgap (0);
	// thisLayout.setColumns (2);
	// thisLayout.setRows (6);
	// this.setLayout (thisLayout);

	TableLayout thisLayout = new TableLayout (
		new double[][] {
			{ 0.5, 0.5 },
			{ TableLayout.FILL, TableLayout.FILL, TableLayout.FILL,
				TableLayout.FILL, TableLayout.FILL,
				TableLayout.FILL } });
	setLayout (thisLayout);

	{

	    ExposureTimeLabel = new PiptLabel ();
	    this.add (ExposureTimeLabel, "0 ,0 ");

	    ExposureTimeLabel.setText ("Exposure Time [s]");
	    JComponent p = new PiptNumberTextField ((PiptData) exposureConfig,
		    "ExposureTime", 9);

	    this.add (p, "1, 0");
	}
	{
	    RepeatExposureLabel = new PiptLabel ("# Exposures", "");
	    this.add (RepeatExposureLabel, "0, 1");

	    JComponent p = new PiptIntegerSpinner (exposureConfig,
		    "ExposureRepeat", 1, 99, 1);
	    this.add (p, "1,1");

	}
	{
	    BinningLabel = new PiptLabel ("Binning", "");
	    this.add (BinningLabel, "0,2");

	    JComponent p = new PiptIntegerSpinner (exposureConfig,
		    "ExposureBinning", 1, 4, 1);

	    this.add (p, "1,2");

	}
	
	
	
	{
	    ReadoutModeLabel = new PiptLabel ("Readout Mode", "");
	    this.add (ReadoutModeLabel, "0,3");

	    // readoutModePanel1 = new ReadoutModePanel(exposureConfig);
	    PiptStringComboBox ODIReadModeCombo = new PiptStringComboBox (
		    ODI.theODI, "ReadNoise", ODI.theODI.getReadNoiseNames (),
		    PiptStringComboBox.NO_ORDER);
	    ODIReadModeCombo.setEditable (false);
	    this.add (ODIReadModeCombo, "1,3");

	}
	{

	    ODIFilterCombo = new PiptStringComboBox (ODI.theODI,
		    "OpticalFilter", ODI.theODI.getFilterKeyNames (),
		    PiptStringComboBox.ASCENDING_ORDER);

	    ODIFilterCombo.setEditable (false);

	    ODIFilterLabel = new PiptLabel ("Filter", "");

	    this.add (ODIFilterLabel, "0,4");
	    this.add (ODIFilterCombo, "1,4");
	}
	
	{
	    ReadoutModeLabel = new PiptLabel ("Detector Material", "");
	    this.add (ReadoutModeLabel, "0,5");

	    // readoutModePanel1 = new ReadoutModePanel(exposureConfig);
	    PiptStringComboBox ODIReadModeCombo = new PiptStringComboBox (
		    ODI.theODI, "DetectorMaterial", ODI.theODI.getDetectorMaterialNames (),
		    PiptStringComboBox.NO_ORDER);
	    ODIReadModeCombo.setEditable (false);
	    this.add (ODIReadModeCombo, "1,5");

	}
	
	// {
	// this.add(new PiptLabel("Ignore Telescope Throughput", ""), "0,5");
	// this.myFilterOnlyPanel = new ODIFilterOnlyPanel();
	// this.add(myFilterOnlyPanel, "1,5");
	//
	// }

	Border etchedBorder = BorderFactory.createEtchedBorder ();
	Border titledBorder = BorderFactory.createTitledBorder (etchedBorder,
		"ODI Exposure Setup");
	setBorder (titledBorder);

	this.myExposureConfig = exposureConfig;
	this.setMaximumSize (this.getPreferredSize ());
    }

    public static void main (String[] args) {
	JFrame j = new JFrame ();
	BasicConfigurator.configure ();
	ODIExposureSetupPanel p = new ODIExposureSetupPanel (
		new ExposureConfig ());
	j.getContentPane ().add (p);
	j.pack ();
	j.setVisible (true);

    }

}

// class ODIFilterOnlyPanel extends JPanel implements ActionListener {
//
// /**
// *
// */
// private static final long serialVersionUID = 1L;
// JCheckBox ignoreTelescopeBox;
//
// public ODIFilterOnlyPanel() {
// setLayout(new FlowLayout(FlowLayout.LEFT));
//
// ignoreTelescopeBox = new JCheckBox();
// this.add(ignoreTelescopeBox);
// ignoreTelescopeBox.addActionListener(this);
//
// }
//
// public void actionPerformed(ActionEvent e) {
// // TODO Auto-generated method stub
// ODI.theODI.setFilterOnly(ignoreTelescopeBox.isSelected());
//
// }
//
// }
