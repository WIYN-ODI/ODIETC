package org.wiyn.etc.inputSpectra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import za.ac.salt.pipt.common.GenericSpectrum;


/** This class describes the target spectrum. This is assumed to be a
 * sum of various individual spectra (such as, say, of a blackbody and
 * an emission line). Methods for adding and removing spectra are
 * provided. A given spectrum may not be added more than once. (But of
 * course it may be removed and added again.) The reason for this
 * restriction is that otherwise there might be substantial trouble
 * with the property change listeners. (If anyone wants to change
 * this, presumably the hashtable for storing the listeners must be
 * replaced by a map.) */


public class TargetSpectrum extends GenericSpectrum
{
    /** the list of spectra used for computing the target spectrum */
    private List<GenericSpectrum> spectra;

    /** the hashtable for keeping track of the added property change listeners */
    private Hashtable<GenericSpectrum, PropertyChangeListener> propertyChangeListeners;


    /** Initializes the list of spectra. The default values are used
     * for the grid. */
    public TargetSpectrum()
    {
	// At the time of writing, the RSS Simulator offers five
	// spectra.
	spectra = new ArrayList<GenericSpectrum>(6);
	propertyChangeListeners = new Hashtable<GenericSpectrum, PropertyChangeListener>(5);
    }


    /** Clones the given target spectrum. Note that the spectra
     * variable isn't cloned, so that trying to access any information
     * concerning the constituent spectrum will result in a
     * NullPointerException.
     * @param targetSpectrum */
    public TargetSpectrum(TargetSpectrum targetSpectrum)
    {
	super(targetSpectrum);
	
    }


    /** Returns the flux at the given wavelength. This is obtained by
     * interpolation. If necessary, the spectrum is updated first.
     * @param wavelength the wavelength (in Angstrom)
     * @return the flux at the given wavelength */
    public double flux(double wavelength)
    {
	if (isUpdateNeeded()) {
	    update();
	    setUpdateNeeded(false);
	}
	return interp(wavelength);
    }


    /** Create the grid containing the spectrum and record the fact
     * that no update is required any longer. */

    public void update()
    {
	setUpdateNeeded(false);
	reset(DEFAULT_LB_STARTING_VALUE, DEFAULT_LB_RESOLUTION, DEFAULT_LB_RANGE);
	for (int s = 0; s < spectra.size(); s++) {
	    GenericSpectrum spectrum = (GenericSpectrum) spectra.get(s);
	    for (int i = 0; i < n(); i++) {
		y[i] += spectrum.flux(x(i));
	    }
	    spectrum.freeMemory();
	}
   }


    /** Adds the given spectrum to the list of spectra considered in
     * the calculation of the target spectrum, and add its flux to the
     * flux grid. Fires a property change event. In addition, we add
     * the default property change listener (and record it in the
     * respective hashtable). If the given spectrum is already in the
     * list of spectra, we output the stack trace and an error message
     * and just return.
     * @param spectrum the spectrum to be added */
    public void add(GenericSpectrum spectrum)
    {
	// Check whether the spectrum is already contained in the list of
	// spectra.
	if (spectra.indexOf(spectrum) >= 0) {
	    (new Exception("There was an attempt to add a spectrum more than once.")).printStackTrace();
	    System.err.println("INTERNAL ERROR: A spectrum may be added to a TargetSpectrum object once only. The given spectrum won't be added.");
	    return;
	}

	// Add the spectrum to the list of spectra.
	spectra.add(spectrum);

	// Record the fact that an update is needed.
	setUpdateNeeded(true);

	// Add the property change listener.
	PropertyChangeListener propertyChangeListener = new FirePropertyChangeListener();
 	propertyChangeListeners.put(spectrum, propertyChangeListener);
	spectrum.addPropertyChangeListener(propertyChangeListener);

	// Tell all interested parties about adding the spectrum.
	firePropertyChange("add", null, spectrum);
    }


    /** Removes the given spectrum from the list of spectra considered
     * in the calculation of the target spectrum, and frees the
     * memory, so that the target spectrum will be updated before the
     * next flux is computed. Fires a property change event. The
     * corresponding property change listener is removed as well.
     * @param spectrum the spectrum to be removed */
   
    public void remove(GenericSpectrum spectrum)
    {
	// Remove the spectrum from the list of spectra.
	spectra.remove(spectrum);
	spectrum.removePropertyChangeListener((PropertyChangeListener) propertyChangeListeners.get(spectrum));

	// Free the memory.
	freeMemory();

	// Tell all interested parties about removing the spectrum.
	firePropertyChange("remove", spectrum, null);
    }


    /** Returns the number of spectra which the target spectrum is
     * composed of.
     * @return the number of spectra which the target spectrum is composed of */
    public int getNumberOfSpectra()
    {
	return spectra.size();
    }


    /** Returns the name of the target spectrum. This is taken to be a
     * list of the names of the considered spectra separated by double
     * colons.
     * @return the name of the target spectrum */
    public String name()
    {
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < spectra.size(); i++) {
	    buffer.append(((GenericSpectrum) spectra.get(i)).name());
	    if (i < spectra.size() - 1) {
		buffer.append("::");
	    }
	}
	return buffer.toString();
    }


    /** This class implents a property change listener which fires a
     * property change event when invoked. */
    private class FirePropertyChangeListener implements PropertyChangeListener
    {
	/** Fires a property change event with the arguments "spectrum added", null and null.
	 * @param event the event */
	public void propertyChange(PropertyChangeEvent event)
	{
	    firePropertyChange("spectrum added", null, null);
	}
    }
}
