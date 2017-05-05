package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.GenericSpectrum;

public class ABReference extends GenericSpectrum {

    final static double Fv = 3.63e-20; // ergs/s/Hz/cm^2
    final static double lightspeed = 2.99792458e10; // cm/s ???? factor of 10
    // modifactionhere ???
    final static double FvC = Fv * lightspeed / (1e-8);

    public double flux (double wavelength) {
	// TODO Auto-generated method stub
	return ABReference.getReferenceFluxAt (wavelength);
    }

    public static double getReferenceFluxAt (double wavelength) {
	return FvC / (wavelength * wavelength);

    }

    public String name () {
	// TODO Auto-generated method stub
	return "AB Reference Flux";
    }

    public void update () {

    }

}
