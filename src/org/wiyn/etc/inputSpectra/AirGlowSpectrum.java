package org.wiyn.etc.inputSpectra;

import java.io.InputStream;
import java.net.URL;

import za.ac.salt.pipt.common.GridResource;
import za.ac.salt.pipt.common.GridSpectrum;


/** This class provides an air glow spectrum. */
public class AirGlowSpectrum extends GridSpectrum
{
    /** the host where to look on the web for the air glow file */
    private String host = "http://www.wiyn.org/";
	
    /** the UVES file to look for */
    private String uves_path = "/resources/rss_pipt-data/uves.airglow.txt";

    /** the re-recuced and cached RSS version */
    private String rss_path = "/resources/rss_pipt-data/salt.airglow.txt";
	

    /*
     * Data from KHN
     */

    // these data from KHN
    private static double xdata[] = {
	3140,
	3750,
	3759,
	4810,
	4819,
	6750,
	6759,
	9000,
	9500
    };

    private static double edata[] = {
	0.6,
	0.6,
	0.6,
	0.6,
	0.8,
	0.8,
	0.8,
	0.8,
	0.8 // added by DRH without any justification.
    };

    private static double Xdata[] = {
	1.143,
	1.143,
	1.241,
	1.241,
	1.144,
	1.144,
	1.108,
	1.108,
	1.108
    };
	
    /** Cache a copy for speed. */	
    private static AirGlowSpectrum ags = null;
	

    /** Creates the air glow spectrum from the standard file. */
    public AirGlowSpectrum()
    {
	// Create the grid (without the correct values yet). */
	reset(DEFAULT_LB_STARTING_VALUE, DEFAULT_LB_RESOLUTION, DEFAULT_LB_RANGE);

	if (AirGlowSpectrum.ags == null) {
	    GridResource gr = new GridResource(host, rss_path);
	    InputStream inputStream = gr.getInputStream();
	    if (inputStream != null) {
		this.read(inputStream);
		// cache it
		AirGlowSpectrum.ags = (AirGlowSpectrum)(new AirGlowSpectrum(this));
	    }
	} else {
	    for (int i = 0; i < ags.n(); i++) {
		this.y[i] = ags.y[i];
	    }
	}
	this.setDiffuse(true);	// AirGlow spectrum is diffuse emission
    }
	

    /** Creates the air glow spectrum as a clone of the given spectrum.
     * @param airGlowSpectrum the cloned air glow spectrum */
    public AirGlowSpectrum(AirGlowSpectrum airGlowSpectrum)
    {
	this.reset(airGlowSpectrum.j(), airGlowSpectrum.k(), airGlowSpectrum.m());
	for (int i = 0; i < airGlowSpectrum.n(); i++) {
	    this.y[i] = airGlowSpectrum.y[i];
	}
    }

	
    /** Creates the air glow spectrum from the file located at the given URL.
     * @param url the URL where to look for the file from which the spectrum is created */
    public AirGlowSpectrum(URL url)
    {
	super(url);
	this.setDiffuse(true); // AirGlow spectrum is diffuse emission
    }


    /** Creates a SALT air glow spectrum from scratch. We perform the calibration specified in SALT-3172AS0005 Spec. Gen. doc.
     * @param scratch does nothing, really. We include it here to give a calling signature different than the no-arg constructor. */
    public AirGlowSpectrum(boolean scratch)
    {
	this.setDiffuse(true); // AirGlow spectrum is diffuse emission

	// read in the UVES air glow data
	GridResource gr = new GridResource(host, uves_path);
	InputStream inputStream = gr.getInputStream();
	if (inputStream != null) {
	    this.read(inputStream);
	}

	// now perform the calculations specified in the SALT spec. gen. doc.

	// make the special data grids
	GridSpectrum e = new GridSpectrum(xdata, edata, xdata.length);
	GridSpectrum X = new GridSpectrum(xdata, Xdata, xdata.length);

	// get a SALT atmosphere
	Atmosphere a = new Atmosphere();

	// apply the flux correction
	this.scale(e);

	// apply extinction from outside the atmosphere to the UVES zenith distance
	a.apply(this, X);

	// remove extinction from the UVES zenith distance to the zenith
	// do the scaling part first
	this.div(X);
	// remove one airmass (we're going to zenith, not outside the atmosphere)
	X.add(-1);
	// remove the extinction
	a.remove(this, X);

	/**
	 * Now remove zodiacal light.
	 */

	// get a solar spectrum
	SolarSpectrum ss = new SolarSpectrum();

	// extinct it to the zenith
	a.apply(ss, 1);

	// scale by all the constants at once
	ss.scale(5500, -0.21 * 8.18e-18);

	// remove it from the airglow
	this.add(ss);

	// no negative fluxes...
	for (int i = 0; i < this.n(); i++) {
	    this.y[i] = Math.max(this.y[i], Double.MIN_VALUE);
	}
    }


    /** Returns the string "air glow spectrum".
     * @return the string "air glow spectrum" */
    public String name()
    {
	return "air glow spectrum";
    }
}
