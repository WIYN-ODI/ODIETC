package za.ac.salt.pipt.common;


/** This interface applies to any class that modifies a spectrum. Examples include mirrors, filters, atmospheres, galaxies. */
public interface SpectrumOperator
{
    /** Applies some kind of modification to the given spectrum.
     * @param spectrum the spectrum to be modified */
    public void apply(GenericSpectrum spectrum);
}