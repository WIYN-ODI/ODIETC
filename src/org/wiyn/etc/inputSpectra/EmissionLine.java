package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.GenericSpectrum;
import za.ac.salt.pipt.common.dataExchange.InvalidValueException;


/** This class describes the spectrum of an emission line. The
 * emission line is taken to be a Gaussian, i.e. it is assumed to be
 * of the form<br />

 * f(lambda) = f_tot * exp(((lambda-lambda_c)/sigma)^2) / (sqrt(2*pi) * sigma)<br />

 * with the lambda_c at the line center, the standard deviation sigma
 * and the total flux f_tot in the line. */


public class EmissionLine extends GenericSpectrum
{
    /** the wavelength of the center of the emission line (in Angstrom) */
    private double centralWavelength = Double.NaN;

    /** the FWHM of this emission line (in Angstrom) */
    private double fwhm = Double.POSITIVE_INFINITY;

    /** the overall flux in the emission line */
    private double totalFlux = Double.POSITIVE_INFINITY;

    /** the FWHM used in the previous calculation of a flux (in Angstrom) */
    private double previousFWHM = Double.POSITIVE_INFINITY;

    /** the total flux used in the previous calculation of a flux */
    private double previousTotalFlux = Double.POSITIVE_INFINITY;

    /** the standard deviation of the emission line (in Angstrom) */
    private double sigma = 0;

    /** the scaling factor for the emission line fluxes */
    private double emissionLineScalingFactor = 0;


    /** Sets the central wavelength, width and total flux, and frees the memory. */
    public EmissionLine (double centralWavelength, double fwhm, double totalFlux)
    {
	// Set the internal variables to the given values.
	this.centralWavelength = centralWavelength;
	this.fwhm = fwhm;
	this.totalFlux = totalFlux;

	// Free the memory.
	freeMemory();
    }


    /** Returns the flux at the given wavelength.
     * @param wavelength the wavelength (in Angstrom)
     * @return the flux at the given wavelength */
    public double flux(double wavelength)
    {
	// It is reasonable to suspect that the properties of the emission line
	// will rarely change. Hence in order to save time we save the computed
	// scaling factor and reuse it next time (unless, of course, a relevant
	// emission property changed, in which case we compute the new value for
	// the scaling factor. The same is true for the standard deviation.
	if (fwhm != previousFWHM || totalFlux != previousTotalFlux) {
	    // fwhm = 2 sqrt(2 ln 2) sigma
	    sigma = fwhm / (2 * Math.sqrt(2 * Math.log(2)));

	    // The scaling factor contains the total flux and the factor that
	    // normalizes the emission line spectrum.
	    emissionLineScalingFactor = totalFlux / (Math.sqrt(2*Math.PI) * sigma);
	}

	// Get the emission line flux.
	double flux = emissionLineScalingFactor * 
	    Math.exp(-0.5*Math.pow((wavelength-centralWavelength)/sigma, 2));

	// Store the current FWHM and total flux so that next time we know
	// whether they have changed.
	previousFWHM = fwhm;
	previousTotalFlux = totalFlux;

	// Return the flux.
	return  flux;
    }


    /** Updates the spectrum. As there is nothing to update, this means that this method does nothing. */
    public void update()
    {
	// do nothing
    }


    /** Sets the wavelength of the emission line center.
     * @param centralWavelength the wavelength of the center of the emission line (in Angstrom) */
    public void setCentralWavelength(double centralWavelength)
    {
	Double oldCentralWavelength = new Double(getCentralWavelength());
	Double newCentralWavelength = new Double(centralWavelength);
	this.centralWavelength = centralWavelength;
	if (oldCentralWavelength.compareTo(newCentralWavelength) != 0) {
	    firePropertyChange("centralWavelength", oldCentralWavelength, newCentralWavelength);
	}
    }


    /** Checks whether the given wavelength value is positive and, if so, assigns it to the central wavelength, using the setCentralWavelength() method.
     * @param centralWavelength the wavelength of the center of the emission line (in Angstrom)
     * @throws InvalidValueException if the given wavelength isn't positive */
    public void safeSetCentralWavelength(double centralWavelength)
    {
	if (centralWavelength <= 0) {
	    throw new InvalidValueException("The central wavelength must be positive.");
	}
	setCentralWavelength(centralWavelength);
    }


    /** Returns the wavelength of the emission line center.
     * @return the wavelength of the center of the emission line (in Angstrom) */
    public double getCentralWavelength()
    {
	return centralWavelength;
    }


    /** Sets the full width half maximum (FWHM).
     * @param fwhm the full width half maximum (in Angstrom) */
    public void setFWHM(double fwhm)
    {
	Double oldFWHM = new Double(getFWHM());
	Double newFWHM = new Double(fwhm);
	this.fwhm = fwhm;
	if (oldFWHM.compareTo(newFWHM) != 0) {
	    firePropertyChange("fwhm", oldFWHM, newFWHM);
	}
    }


    /** Checks whether the given full width half maximum (FWHM) value is positive and, if so, assigns it to the FWHM, using the setFWHM() method.
     * @param fwhm the full width half maximum (in Angstrom)
     * @throws InvalidValueException if the given FWHM isn't positive */
    public void safeSetFWHM(double fwhm)
    {
	if (fwhm <= 0) {
	    throw new InvalidValueException("The FWHM must be positive.");
	}
	setFWHM(fwhm);
    }


    /** Returns the full width half maximum (FWHM).
     * @return the full width half maximum (in Angstrom) */
    public double getFWHM()
    {
	return fwhm;
    }


    /** Sets the total flux in the emission line.
     * @param totalFlux the total flux in the emission line (in erg/cm^2/s/A) */
    public void setTotalFlux(double totalFlux)
    {
	Double oldTotalFlux = new Double(getTotalFlux());
	Double newTotalFlux = new Double(totalFlux);
	this.totalFlux = totalFlux;
	if (oldTotalFlux.compareTo(newTotalFlux) != 0) {
	    firePropertyChange("totalFlux", oldTotalFlux, newTotalFlux);
	}
    }


    /** Checks whether the given value for the total flux in the emission line is non-negative and, if so, assigns it to the total flux, using the setTotalFlux() method.
     * @param totalFlux the total flux in the emission line (in erg/cm^2/s/A)
     * @throws InvalidValueException if the given total flux is negative */
    public void safeSetTotalFlux(double totalFlux)
    {
	if (totalFlux < 0) {
	    throw new InvalidValueException("The total flux must be non-negative.");
	}
	setTotalFlux(totalFlux);
    }


    /** Returns the total flux in the emission line.
     * @return the total flux in the emission line (in erg/cm^2/s/A) */
    public double getTotalFlux()
    {
	return totalFlux;
    }


    /** Returns the name of this spectrum, which is taken to be "Emission Line".
     * @return the string "Emission Line" */
    public String name()
    {
	return "Emission Line";
    }
}
