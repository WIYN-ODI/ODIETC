package za.ac.salt.pipt.common;


/** This class constitutes a filter describing the SALT mirrors. */
public class Mirrors extends Filter
{
    /** the maximum mirror area ("axis on track") */
    public final static double MAXIMUM_AREA = 550000;	  // 55 sq. m
	
    /** the default mirror area ("typical track") */
    public final static double DEFAULT_AREA = 460000;    // 46 sq. m.

    /** the mirror area */
    private double area;


    // these following data are from KHN

    /** the wavelengths (in A) */
    private static double wavelengths[] = {
	3200,
	3500,
	4000,
	5000,
	6000,
	7000,
	8000,
	9000
    };
	
			
    /** the mirror coating efficiencies at these wavelengths */
    private static double efficiencies[] = {
	0.76546645,
	0.75891980,
	0.73027823,
	0.77168576,
	0.77888707,
	0.75237316,
	0.73469722,
	0.79312602
    };


    /** Creates a mirror for SALT with the default area. */	
    public Mirrors()
    {
	this(DEFAULT_AREA);
    }


    /** Create a mirror of the given area.
     * @param area the mirror area */
    public Mirrors(double area)
    {
	super(wavelengths, efficiencies, wavelengths.length);
	this.scale(area);
	this.area = area;
    }
}
