package org.wiyn.etc.inputSpectra;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.GridResource;
import za.ac.salt.pipt.common.GridSpectrum;

public class KC96GalaxyTemplate extends GenericSpectrum {

    private GridSpectrum galaxySpectrum;
    private String galaxyType = TemplateIndex.keys ().nextElement ();
    private double redshift = 0.0;

    private static String ResourcePath = "/resources/rss_pipt-data/KC96/";
    public static Hashtable<String, String> TemplateIndex = new Hashtable<String, String> ();

    static {
	TemplateIndex.put ("Bulge", "bulge_template.ascii");
	TemplateIndex.put ("Elliptical", "elliptical_template.ascii");
	TemplateIndex.put ("S0", "s0_template.ascii");
	TemplateIndex.put ("Sa", "sa_template.ascii");
	TemplateIndex.put ("Sb", "sb_template.ascii");
	TemplateIndex.put ("Sc", "sc_template.ascii");
	TemplateIndex.put ("Starburst 1", "starb1_template.ascii");
	TemplateIndex.put ("Starburst 2", "starb2_template.ascii");
	TemplateIndex.put ("Starburst 3", "starb3_template.ascii");
	TemplateIndex.put ("Starburst 4", "starb4_template.ascii");
	TemplateIndex.put ("Starburst 5", "starb5_template.ascii");
	TemplateIndex.put ("Starburst 6", "starb6_template.ascii");
    }

    static public String[] getTemplateKeys () {
	String[] retVal = new String[TemplateIndex.size ()];
	Enumeration<String> e = TemplateIndex.keys ();
	for (int ii = 0; ii < retVal.length; ii++) {
	    retVal[ii] = new String (e.nextElement ());
	    // System.err.println (retVal[ii]);
	}
	return retVal;
    }

    public KC96GalaxyTemplate() {
	this (TemplateIndex.keys ().nextElement ());
    }

    protected KC96GalaxyTemplate(String Identifier) {
	super.freeMemory ();
	galaxySpectrum = null;

	if (TemplateIndex.get (Identifier) != null) {
	    this.galaxyType = Identifier;
	    this.setUpdateNeeded (true);
	}

    }

    public String getGalaxyType () {

	return galaxyType;
    }

    public void safeSetGalaxyType (String newSelection) {
	if (newSelection != null && newSelection != galaxyType) {
	    if (TemplateIndex.get (newSelection) != null) {
		this.galaxyType = newSelection;
		this.setUpdateNeeded (true);
	    }
	}

    }

    public void redshift (double redshift) {

	int n = this.galaxySpectrum.n ();
	for (int ii = n - 1; ii > 0; ii--) {
	    double wavelength = this.galaxySpectrum.x (ii);
	    wavelength = wavelength / (1 + redshift);
	    this.galaxySpectrum.y[ii] = this.galaxySpectrum.interp (wavelength);
	}
	this.galaxySpectrum.y[0] = 0;

    }

    public double getRedshift () {
	return this.redshift;
    }

    public void safeSetRedshift (double r) {
	if (this.redshift != r) {
	    this.setUpdateNeeded (true);
	}
	this.redshift = r;

    }

    public double flux (double wavelength) {
	if (this.isUpdateNeeded ())
	    update ();// TODO Auto-generated method stub
	if (this.galaxySpectrum != null)
	    return this.galaxySpectrum.interp (wavelength);
	else
	    return 0;
    }

    public String name () {

	return "KC96 Galaxy Template";
    }

    @Override
    public void update () {
	// TODO Auto-generated method stub
	if (galaxyType == null) {
	    System.err.println ("Galaxy type is set to null - aborting");
	    return;
	}
	String resource = TemplateIndex.get (galaxyType);
	if (resource == null) {
	    System.err.println ("Could not find file for key: " + galaxyType);
	    return;
	}
	GridResource gr = new GridResource ("http://www.wiyn.org/",
		ResourcePath + resource)

	;

	InputStream inputStream = null;
	if (gr != null) {
	    inputStream = gr.getInputStream ();
	    galaxySpectrum = new GridSpectrum ();
	    galaxySpectrum.read (inputStream);
	}

	if (this.redshift != 0)
	    this.redshift (redshift);
	this.setUpdateNeeded (false);
    }

    public static void main (String[] args) {

	KC96GalaxyTemplate t = new KC96GalaxyTemplate ("Buldge");
	System.err.println (t);
    }

}
