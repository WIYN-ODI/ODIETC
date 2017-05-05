package org.wiyn.etc.odi;

import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import za.ac.salt.pipt.common.Filter;
import za.ac.salt.pipt.common.GridResource;
import za.ac.salt.pipt.common.Phase;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataElement;

/**
 * A data holding and services class that describes ODI at WIYN.
 * 
 * WIYN telescope and ODI component's throughput curves are stored in
 * /resources/instruments/wiynodi
 * 
 * 
 * TODO: make this class a child of an abstract instrument class to allow future
 * extension of the ETC for other instruments.
 * 
 * @author harbeck
 * 
 */
public class ODI implements PiptData {

    private static Logger myLogger = Logger.getLogger ("etc.odi.ODI");

    final static String website = "http://www.wiyn.org/";
    final static String filterDirectory = "/resources/filters";
    final static String filterDirectoryIndex = "index.txt";
    final static String WIYNODIDirectory = "/resources/instruments/wiynodi";

    // Setup for color filters
    private Hashtable<String, String> filterList;

    private String currentFilter = null;
    // private String[] filterKeyNames;

    // CCD characteristics
    private double SaturationLevel = 65000;

    // Initial Settings for the readout noise
    private double ReadoutNoise = 10.0;

    public static Hashtable<String, Double> ReadoutModeTable;
    static {

	ReadoutModeTable = new Hashtable<String, Double> ();
	ReadoutModeTable.put ("10e- (fast)", new Double (10));
	ReadoutModeTable.put (" 6e- (slow)", new Double (6));

    }

    private double DarkCurrent = 0.008; // e-/sec & pixel
    public static Hashtable<String, Double> DarkCurrentTable;
    static {

	DarkCurrentTable = new Hashtable<String, Double> ();
	DarkCurrentTable.put ("0.04 e-/sec/pix", new Double (0.04));
	DarkCurrentTable.put ("0.008 e-/sec/pix", new Double (0.008));

    }

    public static Hashtable DetectorMaterialHashTable = new Hashtable<String, String> ();

    static {
	DetectorMaterialHashTable = new Hashtable<String, String> ();
	DetectorMaterialHashTable.put ("Lot 6 as build", "lot6.txt");
	// DetectorMaterialHashTable.put ("High rho (Lot 5)",
	// "OTA_high_rho.txt");
	// DetectorMaterialHashTable.put ("Perfect detector",
	// "ccd_perfect.txt");
    }

    private String DetectorMaterial = "Lot 6 as build";

    /**
     * This filter will hold common absorption of the instrument, i.e., optic,
     * coatings, and detector throughput
     */
    private Filter commonFilter = null;

    private Filter TelluricLines = null;
    public boolean considerFitlerOnly;

    // There is only one ODI instance allowed
    public static ODI theODI = new ODI ();

    private ODI() {

	getcommonFilter ();

	//
	// Init color filters
	//

	filterList = new Hashtable<String, String> ();
	// filterList.put ("Empty", "void");
	loadFilterNames ();

	//
	// Initialize readout noise setting
	//
	this.safeSetReadNoise (this.getReadNoiseNames ()[1]);
    }

    /**
     * Interface to set an optical filter in ODI.
     * 
     * The filter name is verified against the internal table of allowable
     * filter names. If the name doe snot match, no filter is set. The internal
     * table of filter names can be queurried through getFilterlist().
     * 
     * @param f
     *            Name of the optical filter to select.
     */
    public void safeSetOpticalFilter (String f) {
	if ((this.filterList != null && f != null && this.filterList
		.containsKey (f)) || f == null) {
	    this.currentFilter = f;
	    myLogger.debug ("ODI Changed filter to : " + f);
	} else {
	    myLogger.error ("Tried to select non-exitent filter: " + f);
	}

    }

    public String[] getFilterKeyNames () {
	// get the filter names out of the hashtable;
	Enumeration<String> keys = filterList.keys ();
	Vector<String> temp = new Vector<String> ();
	while (keys.hasMoreElements ()) {
	    temp.add (keys.nextElement ());
	}
	String[] filterKeyNames = new String[temp.size ()];
	for (int ii = 0; ii < temp.size (); ii++) {
	    filterKeyNames[ii] = new String ((String) temp.get (ii));
	    // if (ii == 0)
	    // this.safeSetOpticalFilter (filterKeyNames[ii]);
	}

	return filterKeyNames;
    }

    /**
     * Return the currently selected filter in the ODI configuration.
     * 
     * @return Name of the selected filter.
     */
    public String getOpticalFilter () {
	return this.currentFilter;

    }

    /**
     * Return an array of Strings that desccribe readout noise configurations.
     * 
     * @return
     */
    public String[] getReadNoiseNames () {
	Enumeration<String> keys = ReadoutModeTable.keys ();
	Vector<String> temp = new Vector<String> ();
	while (keys.hasMoreElements ()) {
	    temp.add (keys.nextElement ());
	}
	String[] ReadnoiseKeyNames = new String[temp.size ()];
	for (int ii = 0; ii < temp.size (); ii++) {
	    ReadnoiseKeyNames[ii] = new String ((String) temp.get (ii));
	    // if (ii == 0)
	    // this.safeSetOpticalFilter(keyNames[ii]);
	}
	return ReadnoiseKeyNames;
    }

    public String[] getDetectorMaterialNames () {

	Enumeration<String> keys = DetectorMaterialHashTable.keys ();
	Vector<String> temp = new Vector<String> ();
	while (keys.hasMoreElements ()) {
	    temp.add (keys.nextElement ());
	}
	String[] DetectorNames = new String[temp.size ()];
	for (int ii = 0; ii < temp.size (); ii++) {
	    DetectorNames[ii] = new String ((String) temp.get (ii));
	    // if (ii == 0)
	    // this.safeSetOpticalFilter(keyNames[ii]);
	}
	return DetectorNames;
    }

    public String[] getDarkCurrentNames () {

	Enumeration<String> keys = DarkCurrentTable.keys ();
	Vector<String> temp = new Vector<String> ();
	while (keys.hasMoreElements ()) {
	    temp.add (keys.nextElement ());
	}
	String[] DarkCurrentNames = new String[temp.size ()];
	for (int ii = 0; ii < temp.size (); ii++) {
	    DarkCurrentNames[ii] = new String ((String) temp.get (ii));
	    // if (ii == 0)
	    // this.safeSetOpticalFilter(keyNames[ii]);
	}
	return DarkCurrentNames;
    }

    /**
     * Get a string describing the currently selected readout noise mode
     * 
     * @return String describing the currently active readout noise.
     */
    public String getReadNoise () {
	if (ReadoutModeTable != null
		&& ReadoutModeTable.containsValue (this.ReadoutNoise)) {
	    for (Iterator<String> it = ReadoutModeTable.keySet ().iterator (); it
		    .hasNext ();) {
		String key = it.next ();
		if (ReadoutModeTable.get (key) == this.ReadoutNoise) {
		    myLogger.debug ("Return RON descriptor [" + key
			    + "] for readnoise value " + this.ReadoutNoise);
		    return key;
		}
	    }
	}
	myLogger.error ("Could not find a matching descriptor for current Readoutnoise. Something is messed up in ReadoutModeTable.");
	return "Invalid";
    }

    /**
     * Set the readnoise mode of ODI. The decriptive string has to match an
     * entry in the internal ReadoutModeTable; otherwise, no change in readnoise
     * mode will occur.
     * 
     * @param readnoise
     */
    public void safeSetReadNoise (String readnoise) {
	if (ODI.ReadoutModeTable != null && readnoise != null
		&& ODI.ReadoutModeTable.keySet ().contains (readnoise)) {
	    myLogger.debug ("Set Readoutnoise to: " + readnoise);
	    this.ReadoutNoise = ReadoutModeTable.get (readnoise);
	} else {
	    myLogger.error ("Could not set Readnoise to: " + readnoise
		    + " . Value not know to system.");
	}

    }

    /**
     *
     */
    public String getDetectorMaterial () {
	if (DetectorMaterialHashTable != null) {
	    if (DetectorMaterialHashTable.keySet ().contains (
		    this.DetectorMaterial)) {

		return DetectorMaterial;
	    }

	    else {
		DetectorMaterial = (String) DetectorMaterialHashTable.keySet ()
			.toArray ()[0];

	    }
	}

	myLogger.error ("Could not find a matching descriptor for current DetectorMaterial. Something is messed up in DetectorMaterial.");
	return "Invalid";
    }

    /**
     * Set the readnoise mode of ODI. The decriptive string has to match an
     * entry in the internal ReadoutModeTable; otherwise, no change in readnoise
     * mode will occur.
     * 

     */
    public void safeSetDetectorMaterial (String Material) {
	if (ODI.DetectorMaterialHashTable != null && Material != null
		&& ODI.DetectorMaterialHashTable.keySet ().contains (Material)) {
	    myLogger.debug ("Set Detector Material to: " + Material
		    + " which refers to throughput file "
		    + DetectorMaterialHashTable.get (Material));
	    this.DetectorMaterial = (Material);
	} else {
	    myLogger.error ("Could not set Detector Material to: [" + Material
		    + "] . Value not know to system.");
	}

    }

    /**
    *
    */
    public String getDarkCurrent () {
	if (DarkCurrentTable != null
		&& DarkCurrentTable.containsValue (this.DarkCurrent)) {
	    for (Iterator<String> it = DarkCurrentTable.keySet ().iterator (); it
		    .hasNext ();) {
		String key = it.next ();
		if (DarkCurrentTable.get (key) == this.DarkCurrent) {
		    myLogger.debug ("Return DC descriptor [" + key
			    + "] for DC value " + this.DarkCurrent);
		    return key;
		}
	    }
	}
	myLogger.error ("Could not find a matching descriptor for current Dark Current. "
		+ "Something is messed up in DarkCurrentTable.");
	return "Invalid";
    }

    /**
     * Set the readnoise mode of ODI. The decriptive string has to match an
     * entry in the internal ReadoutModeTable; otherwise, no change in readnoise
     * mode will occur.
     * 
     *
     */
    public void safeSetDarkCurrent (String dc) {
	if (ODI.DarkCurrentTable != null && dc != null
		&& ODI.DarkCurrentTable.keySet ().contains (dc)) {
	    myLogger.debug ("Set Readoutnoise to: " + dc);
	    this.DarkCurrent = DarkCurrentTable.get (dc);
	} else {
	    myLogger.error ("Could not set DarkCurrent to: " + dc
		    + " . Value not know to system.");
	}

    }

    public Filter getSpectrumFilter () {
	return null;
    }

    /**
     * returns the master ODI & WIYN throughput filter. Items included are: 3
     * WIYN mirrors reflectivity WIYN filter to be included: optical element
     * reflection losses optical elements transmission losses
     * 
     */

    public Filter getWIYNODI_ThroughputFilter () {

	Filter start = null;

	if (this.considerFitlerOnly) {
	    myLogger.debug ("Ignoring telescope & instrument throughput per request");
	    start = new Filter (1.);
	} else {
	    start = new Filter (getcommonFilter ());
	}

	Filter ccd = getCCD ((String) DetectorMaterialHashTable
		.get (this.DetectorMaterial));
	if (ccd != null) {
	    start.scale (ccd);
	}
	Filter filter = getFilter (currentFilter);
	// an optical filter
	if (filter != null) {
		start.scale(filter);

	}

	myLogger.debug ("Returning ODI Throughput filter: " + start);
	return start;
    }

    /**
     * Returns a Filter object based on a name describing available ODI filters.
     * 
     * @param name
     * @return
     */
    public Filter getFilter (String name) {

	Filter f;

	// if (name.compareTo ("box") == 0 ) return new Filter (6000,6500);
	if (name != null && !name.equals ("Empty") && filterList != null) {
	    String fname = (String) filterList.get (name);
	    myLogger.debug ("Read filter from " + fname);
	    f = loadResourceFilter (ODI.filterDirectory, fname);
	} else
	    f = new Filter (1.0);
	//f.write ("test");
	return f;
    }

    private Filter getMirrorFilter () {

	Filter f = loadResourceFilter (WIYNODIDirectory, "WIYN_primary.dat");
	return f;
    }

    private Filter getSylgardFilter () {
	Filter f = loadResourceFilter (WIYNODIDirectory, "sylgard184.txt");
	return f;
    }

    private Filter getFusedSilicaFilter () {

	Filter f = loadResourceFilter (WIYNODIDirectory, "ODI_FusedSilica.txt");
	return f;
    }

    private void loadFilterNames () {

	try {
	    InputStream filterindex = this.getClass ().getResourceAsStream (
		    filterDirectory + "/" + filterDirectoryIndex);

	    BufferedReader reader = new BufferedReader (new InputStreamReader (
		    filterindex));

	    while (reader.ready ()) {

		String Line = reader.readLine ();

		StringTokenizer st = new StringTokenizer (Line);
		if (st.countTokens () == 2) {

		    String fname = st.nextToken ();
		    String ffile = st.nextToken ();
		    myLogger.debug (fname + "-> " + ffile);
		    filterList.put (fname, ffile);
		}

	    }

	} catch (Exception e) {

	    myLogger.error ("Cannot open ODI filter index.", e);

	}
	// filterList.put ("box", "box");

    }

    /**
     * Return a pre-calculated filter that does not change for the instrument.
     * The light beam wil always go through he telescope, and the optics. Also,
     * the detector QE curve does not change.
     * 
     * @return
     */
    private Filter getcommonFilter () {

	if (commonFilter == null) {
	    myLogger.debug ("Calculating WIYN/ODI throughput");
	    // create the common Filter
	    Filter start = new Filter (1.0);
	    Filter mirror = getMirrorFilter ();
	    Filter coatings = loadResourceFilter (WIYNODIDirectory,
		    "ODI_coatings.txt");
	    // Filter coatings = loadResourceFilter (WIYNODIDirectory,
	    // "infiniteOpticsL2.txt");
	    Filter pbl6y = loadResourceFilter (WIYNODIDirectory,
		    "ODI_PBL6Y.txt");
	    Filter silica = getFusedSilicaFilter ();

	    Filter pODIFuge = loadResourceFilter (WIYNODIDirectory,
		    "podifudge.txt");

	   // Filter ADC = loadResourceFilter (WIYNODIDirectory,
		//    "ADCSylgAndCoatingTrans.txt");
	    
	    Filter ADC = loadResourceFilter (WIYNODIDirectory,
		    "adc_coatingandsylgard_noaoscan.txt");
	    
	    if (mirror != null) {
		start.scale (mirror);
		start.scale (mirror);
		start.scale (mirror);
	    }

	    // used to be 4 times the coating; I think for the four surfaces of
	    // the ADC. now replaced by the sylgard bonded sample.
	    /*
	     * if (coatings != null) { for (int ii = 0; ii < 4; ii++) {
	     * start.scale (coatings); } }
	     */
	    
	    
	    if (pbl6y != null) {
		start.scale (pbl6y);
	    }
	    if (silica != null) {
		start.scale (silica);
	    }
	    if (pODIFuge != null) {
		start.scale (pODIFuge);
	    }

	    {
		start.scale (ADC);
		start.scale (ADC);
	    }
	    // Filter ccd = getCCD ("");
	    // if (ccd != null)
	    // start.scale (ccd);

	    loadTelluricLines ();
	    if (TelluricLines != null) {
		start.scale (TelluricLines);

	    }

	    commonFilter = start;

	    // commonFilter.write (".odiThroughput.dat");
	    myLogger.debug ("Done");
	}

	return commonFilter;
    }

    /**
     * Returns a filter object that describes the CCD quantum efficiency curve.
     * 
     * @param name
     *            Name of the detector file. Currently this parameter is not
     *            uesd.
     * @return Filter object with CCD sensitivity Curve.
     */

    private Filter getCCD (String name) {

	Filter f = loadResourceFilter (WIYNODIDirectory, name);
	return f;
    }

    /** Loads a filter out of the ODI resource tree */
    public static Filter loadResourceFilter (String path, String name) {
	Filter f = null;
	String fullname = new String (path + "/" + name);
	try {
	    // TODO: Clean this code up and remove SALT legacy
	    GridResource grid = new GridResource (website, fullname);
	    URL url = grid.getURL ();
	    if (url != null) {
		f = new Filter (url);
	    }
	} catch (Exception e) {
	    myLogger.error ("Cannot read ODI filter: " + fullname, e);
	}
	return f;

    }

    // public String getCurrentFilter() {
    // return currentFilter;
    // }

    // public void safeSetCurrentFilter(String currentFilter) {
    // if (this.filterList.containsKey(currentFilter))
    // this.currentFilter = currentFilter;
    // else
    // System.err
    // .println("Bad: tried to set non-existing filter in ODI.java");
    // }

    // public void setKeyNames (String[] keyNames) {
    // this.filterKeyNames = keyNames;
    // }

    public double getReadoutNoise () {
	return ReadoutNoise;
    }

    public void setReadoutNoise (double readoutNoise) {
	ReadoutNoise = readoutNoise;
    }

    public double getDarkCurrentValue () {

	return DarkCurrent;
    }

    public double getSaturationLevel () {
	return SaturationLevel;
    }

    public void setSaturationLevel (double saturationLevel) {
	SaturationLevel = saturationLevel;
    }

    public static void main (String[] args) {
	System.out.println ("Done");
    }

    public double getPixelScale () {

	return 0.11;
    }

    public void setFilterOnly (boolean selected) {
	this.considerFitlerOnly = selected;

    }

    private void loadTelluricLines () {

	this.TelluricLines = loadResourceFilter (WIYNODIDirectory,
		"kpnotellurix.txt");
	// this.TelluricLines = new Filter(1.0);
    }

    public ArrayList<PiptDataElement> getChildren () {
	// TODO Auto-generated method stub
	return null;
    }

    public boolean isRequired (String childElement, Phase phase) {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean isSubTreeComplete (Phase phase) {
	// TODO Auto-generated method stub
	return false;
    }

    public void addPropertyChangeListener (PropertyChangeListener listener) {
	// TODO Auto-generated method stub

    }

    public void removePropertyChangeListener (PropertyChangeListener listener) {
	// TODO Auto-generated method stub

    }

}
