package org.wiyn.etc.inputSpectra;

import za.ac.salt.pipt.common.GenericSpectrum;


/** This class describes a power law spectrum. WARNING: The units of the flux are fairly arbitrary. You should use this class only if it is ensured that the flux values will be properly normalized! */
public class PowerLaw extends GenericSpectrum
{
    /** the power law index */
    protected double index = Double.NaN;


    /** Constructs a power law with the given power law index and frees the memory.
     * @param index the power law index */
    public PowerLaw(double index)
    {
	this.index = index;
	freeMemory();
    }


    /** Returns the power law flux (in arbitrary units) for the given wavelength.
     * @param wavelength the wavelength (in Angstrom)
     * @return the power law flux (arbitrary units) */
    public double flux(double wavelength)
    {
	return Math.pow(wavelength, index);
    }


    /** Updates the spectrum. As there is nothing to update, this means that this method does nothing. */
    public void update()
    {
	// do nothing
    }


    /** Sets the power law index. The index must be given with the correct sign; hence, e.g., for a power law of the form wavelength^-2, you would have to pass the value -2.
     * @param index the power law index */
    public void setIndex(double index)
    {
	Double oldIndex = new Double(getIndex());
	Double newIndex = new Double(index);
	this.index = index;
	if (oldIndex.compareTo(newIndex) != 0) {
	    firePropertyChange("index", oldIndex, newIndex);
	}
    }


    /** Sets the power law index. The index must be given with the correct sign; hence, e.g., for a power law of the form wavelength^-2, you would have to pass the value -2. The method is included for compatibility only.
     * @param index the power law index */
    public void safeSetIndex(double index)
    {
	setIndex(index);
    }


    /** Returns the power law index. The index is given with the correct sign; hence, e.g., the index -2 would imply a power law of the form wavelength^-2.
     * @return the power law index */
    public double getIndex()
    {
	return index;
    }


    /** Returns the name of this spectrum, which is taken to be "Power Law".
     * @return the string "Power Law" */
    public String name()
    {
	return "Power Law";
    }
}
