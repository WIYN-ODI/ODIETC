package za.ac.salt.pipt.common;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;


/** This class implements the Johnson UBVRI filters. */
public class JohnsonFilter extends Filter implements SpectrumOperator
{
    /** the constant denoting the U filter */ 	
    public static final String U = "U";

    /** the constant denoting the B filter */ 	
    public static final String B = "B";

    /** the constant denoting the V filter */ 	
    public static final String V = "V";

    /** the constant denoting the R filter */ 	
    public static final String R = "R";
 
    /** the constant denoting the I filter */ 	
    public static final String I = "I";
 	
    /** the central wavelength */
    private double refWave;

    /** the flux at the reference wavelength corresponding to M = 0 */
    private double refFlux;

    // U filter

    /** the central wavelength for the filter */
    private static final double U_WAVE = 3790;

    /** the flux for M = 0 at the central wavelength of the filter */
    private static final double U_FLUX = 3.96e-9;

    /** the wavelengths for the filter */
    private static final double U_X[] = {
	3000.,3050.,3100.,3150.,3200.,3250.,3300.,3350.,3400.,
	3450.,3500.,3555.,3600.,3650.,3700.,3750.,3800.,3850.,
	3900.,3950.,4000.,4050.,4100,4150.,4200.
    };

    /** the throughputs for the filter */
    private static final double U_Y[] = {
	0.000,0.016,0.068,0.167,0.287,0.423,0.560,0.673,0.772,
	0.841,0.905,0.943,0.981,0.993,1.000,0.989,0.916,0.804,
	0.625,0.423,0.238,0.114,0.051,0.019,0.000
    };

    // B filter

    /** the central wavelength for the filter */
    private static final double B_WAVE = 4410;

    /** the flux for M = 0 at the central wavelength of the filter */
    private static final double B_FLUX = 6.31e-9;

    /** the wavelengths for the filter */
    private static final double B_X[] = {
	3600.,3700.,3800.,3900.,4000.,4100.,4200.,4300.,4400.,
	4500.,4600.,4700.,4800.,4900.,5000.,5100.,5200.,5300.,
	5400.,5500.,5600.
    };

    /** the throughputs for the filter */
    private static final double B_Y[] = {
	0.000,0.030,0.134,0.567,0.920,0.978,1.000,0.978,0.935,
	0.853,0.740,0.640,0.536,0.424,0.325,0.235,0.150,0.095,
	0.043,0.009,0.000
    };

    // V filter

    /** the central wavelength for the filter */
    private static final double V_WAVE = 5610;

    /** the flux for M = 0 at the central wavelength of the filter */
    private static final double V_FLUX = 3.70e-9;

    /** the wavelengths for the filter */
    private static final double V_X[] = {
	4700.,4800.,4900.,5000.,5100.,5200.,5300.,5400.,5500.,
	5600.,5700.,5800.,5900.,6000.,6100.,6200.,6300.,6400.,
	6500.,6600.,6700.,6800.,6900,7000.
    };

    /** the throughputs for the filter */
    private static final double V_Y[] = {
	0.000,0.030,0.163,0.458,0.780,0.967,1.000,0.973,0.898,
	0.792,0.684,0.574,0.461,0.359,0.270,0.197,0.135,0.081,
	0.045,0.025,0.017,0.013,0.009,0.000
    };

    // R filter

    /** the central wavelength for the filter */
    private static final double R_WAVE = 6680;

    /** the flux for M = 0 at the central wavelength of the filter */
    private static final double R_FLUX = 2.26e-9;

    /** the wavelengths for the filter */
    private static final double R_X[] = {
	5500.,5600.,5700.,5800.,5900.,6000.,6100.,6200.,6300.,
	6400.,6500.,6600.,6700.,6800.,6900.,7000.,7100.,7200.,
	7300.,7400.,7500.,8000.,8500.,9000.
    };

    /** the throughputs for the filter */
    private static final double R_Y[] = {
	0.000,0.230,0.740,0.910,0.980,1.000,0.980,0.960,0.930,
	0.900,0.860,0.810,0.780,0.720,0.670,0.610,0.560,0.510,
	0.460,0.400,0.350,0.140,0.030,0.000
    };

    // I filter

    /** the central wavelength for the filter */
    private static final double I_WAVE = 7920;

    /** the flux for M = 0 at the central wavelength of the filter */
    private static final double I_FLUX = 1.14e-9;

    /** the wavelengths for the filter */
    private static final double I_X[] = {
	7000.,7100.,7200.,7300.,7400.,7500.,7600.,7700.,7800.,
	7900.,8000.,8100.,8200.,8300.,8400.,8500.,8600.,8700.,
	8800.,8900.,9000.,9100.,9200.
    };

    /** the throughputs for the filter */
    private static final double I_Y[] = {
	0.000,0.024,0.232,0.555,0.785,0.910,0.965,0.985,0.990,
	0.995,1.000,1.000,0.990,0.980,0.950,0.910,0.860,0.750,
	0.560,0.330,0.150,0.030,0.000
    };


    /** Constructs the filter for the given band ("U", "B", "V", "R" or "I") by resampling the respective wavelengths and throughputs.
     * @param band the band
     * @throws InvalidValueException if the given filter band is unknown */
    public JohnsonFilter(String band)
    {
	if (band.equals(U)) {
	    this.resample(U_X, U_Y, U_X.length);
	    this.refWave = U_WAVE;
	    this.refFlux = U_FLUX;
	}
	else if (band.equals(B)) {
	    this.resample(B_X, B_Y, B_X.length);
	    this.refWave = B_WAVE;
	    this.refFlux = B_FLUX;
	}
	else if (band.equals(V)) {
	    this.resample(V_X, V_Y, V_X.length);
	    this.refWave = V_WAVE;
	    this.refFlux = V_FLUX;
	}
	else if (band.equals(R)) {
	    this.resample(R_X, R_Y, R_X.length);
	    this.refWave = R_WAVE;
	    this.refFlux = R_FLUX;
	}
	else if (band.equals(I)) {
	    this.resample(I_X, I_Y, I_X.length);
	    this.refWave = I_WAVE;
	    this.refFlux = I_FLUX;
	}
	else {
	    throw new InvalidValueException("The filter band \"" + band + "\" is unknown.");
	}
    }


    /** Returns the reference wavelength.
     * @return the reference wavelength (in A) */
    public double getRefWave()
    {
	return this.refWave;
    }


    /** Sets the reference wavelength.
     * @param refWave the reference wavelength (in A) */
    public void setRefWave(double refWave)
    {
	this.refWave = refWave;
    }


    /** Returns the reference flux.
     * @return the reference flux */
    public double getRefFlux()
    {
	return this.refFlux;
    }


    /** Sets the reference flux.
     * @param refFlux the reference flux */
    public void setFlux(double refFlux)
    {
	this.refFlux = refFlux;
	return;
    }

    /** Returns the apparent magnitude for a spectrum.
     * @param spectrum the sperctrum for which the apparent magnitude is computed
     * @return the apparent magnitude */
    public double getMagnitude(GenericSpectrum spectrum)
    {
	double sum_fs = 0;	// sum of filter times spectrum
	double sum_f = 0;	// sum of filter only

	int n = spectrum.n();	// for speed
	for (int i = 0; i < n; i++) {
	    //double x = spectrum.x(i);
	    double y = spectrum.y[i];
	    //double g = this.interp(x);
	    double f = this.y[i];
	    sum_fs += f*y;
	    sum_f += f;
	}
	double f = sum_fs / sum_f;
	double m = -2.5 * Math.log(f/this.refFlux) / Math.log(10);
	return m;
    }
}
