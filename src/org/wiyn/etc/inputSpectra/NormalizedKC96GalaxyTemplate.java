package org.wiyn.etc.inputSpectra;

public class NormalizedKC96GalaxyTemplate extends KC96GalaxyTemplate implements
	NormalizedSpectrum {

    private FluxNormalizationDescriptor myMagSystem;
    private boolean magSystemChanged = true;
    private boolean referencemagChanged = true;
    private double normalizationFactor;
    private double referenceMag = 20;

    public NormalizedKC96GalaxyTemplate() {
	super ();
    }

//    private NormalizedKC96GalaxyTemplate(String Template) {
//	super (Template);
//    }

    public double getMagnitude () {
	// TODO Auto-generated method stub
	return referenceMag;
    }

    public double nonNormalizedFlux (double wavelength) {
	// TODO Auto-generated method stub
	return super.flux (wavelength);
    }

    public FluxNormalizationDescriptor getMagSystem () {
	if (this.myMagSystem == null)

	    this.myMagSystem = new FluxNormalizationDescriptor (
		    FluxNormalizationDescriptor.FluxModes[0]);

	return this.myMagSystem;
    }

    public void setMagSystem (FluxNormalizationDescriptor d) {

	if (this.myMagSystem == null) {
	    this.myMagSystem = new FluxNormalizationDescriptor (d);
	} else {
	    this.magSystemChanged = true;
	    this.myMagSystem.set (d);

	}
	firePropertyChange ("MagSystem", null, d);
    }

    public double flux (double wavelength) {

	if (this.referencemagChanged || magSystemChanged) {
	    normalizationFactor = FluxNormalization.getNormalisationFactor (
		    myMagSystem, this);
	    magSystemChanged = false;
	    referencemagChanged = false;
	}
	return normalizationFactor * nonNormalizedFlux (wavelength);
    }

    public void safeSetMagnitude (double magnitude) {
	this.setMagnitude (magnitude);

    }

    public void setMagnitude (double vMagnitude) {
	Double oldVMagnitude = new Double (getMagnitude ());
	Double newVMagnitude = new Double (vMagnitude);
	this.referenceMag = vMagnitude;
	this.referencemagChanged = true;
	if (oldVMagnitude.compareTo (newVMagnitude) != 0) {
	    firePropertyChange ("vMagnitude", oldVMagnitude, newVMagnitude);
	}
    }

    public FluxNormalizationDescriptor[] getFluxModes () {
	// TODO Auto-generated method stub
	return FluxNormalizationDescriptor.FluxModes;
    }

}
