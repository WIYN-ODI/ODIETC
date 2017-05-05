package za.ac.salt.pipt.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;

/**
 * A base class for 1-D numeric grids. We provide basic numerical algorithms,
 * such as adding, scaling, and cubic spline interpolation.
 * <p>
 * Note: comments will often name Angstroms as the x-value. This is for
 * concreteness in descriptions; actually, the x-values may represent any
 * quantity.
 * <p>
 * A Grid is characterized by 3 integers:
 * <li>j: the starting value; the first abcissa is at 2**j Angstroms
 * <li>k: the resolution; there are 2**k Angstroms per bin.
 * <li>m: the range; the data cover 2**m Angstroms. There are 2**m/2**k, or
 * 2**(m-k) bins in the Grid.
 * <p>
 * Parameterizing a Grid in this way is useful. The most frequent grid
 * operations involve multiplying one by another, e.g. scaling a spectrum by a
 * filter's loss-function. To do this, the grids' bins must be commensurate. If
 * they are not, then we can invoke an expensive area-preserving regridding of
 * one or the other, but we must avoid doing this very often to maintain good
 * performance.
 * <p>
 * With our parameterization, grid bins are commensurate if the k's agree, and
 * the offset of one grid with respect to the other is always an integral number
 * of bins. Grid-on-grid operations depend only on k, and changing k is very
 * fast (see point 4 below).
 * <p>
 * <li>1. Forcing a 2**j start value helps alignment between grids; no
 * fractional offsets.
 * <li>2. Forcing 2**k Angstroms per bin is *really* useful. The step size,
 * being a power of 2 (e.g. 4, 2, 1, 0.5, 0.25, 0.125, 0.0625...) can always be
 * represented exactly in a computer. Repetitive summation of the step size does
 * not accrue roundoff errors.
 * <li>3. Treating resolution and range independently reflects usage: We will
 * more often want to change the resolution (k) of a grid, perhaps to make it
 * commensurate with another grid, than we will want to change the range (e.g.
 * extending by adding zeroes, or constraining by throwing away data).
 * <li>4. Changing the resolution with binary step sizes just means splitting
 * bins apart in pairs, or adding them together in pairs. This is really fast.
 * Further changes in resolution can be done recursively.
 * <p>
 * The use of 2**j as the starting x-value deserves a closer look. Consider the
 * case of two Grids with equal k-values. The bins are the same size. The two
 * Grids may be offset, though, with one starting at 2000 Angstroms and the
 * other at 3000. This example offset causes no difficulty, because the bins
 * still match up in the region of overlap. But consider, for example, the case
 * of k=2 (4 Angstroms per bin) with one Grid starting at 2000 Angstroms and the
 * other at 2001. The bins of each are the same size, but one can't slide one
 * with respect to the other to align both bins and Angstroms. When bins align,
 * Angstroms don't, and vice versa. To avoid this, the starting values need to
 * be multiples of 2**k (and j >= k);
 */
public class Grid {
	/**
	 * the default abscissa starting value, given as the binary logarithmic
	 * value (i.e. the value of 11 corresponds to a "real" value of 2 ^ 11 =
	 * 2048)
	 */
	public static final int DEFAULT_LB_STARTING_VALUE = 11; // 2048

	/**
	 * the default abscissa resolution (i.e. the interval length per bin), given
	 * as the binary logarithmic value (i.e. the value of -4 corresponds to a
	 * "real" bin length of 2 ^ -4 = 0.0625)
	 */
	public static final int DEFAULT_LB_RESOLUTION = -4; // 0.0625

	/**
	 * the default abscissa range (i.e. difference between its boundaries),
	 * given as the binary logarithmic value (i.e. the value of 13 corresponds
	 * to a "real" range of 2 ^13 = 8192)
	 */
	public static final int DEFAULT_LB_RANGE = 13; // 8192

	/** the starting value of the abcissa (as the binary logarithmic value) */
	private int j;

	/**
	 * the resolution of the abscissa (i.e. the interval length per bin, as the
	 * binary logarithmic value)
	 */
	private int k;

	/** the overall abscissa range (as the binary logarithmic value) */
	private int m;

	/** the number of bins */
	private int n;

	/** the ordinate values (public, not private, for fast access) */
	public double[] y;

	/** 2nd derivatives of ordinate data */
	private double[] ypp;

	/**
	 * For cubic spline interpolation, we need to compute the 1st and 2nd
	 * derivatives. We don't want to do this for each interpolation, of which
	 * there might be many. But we need to recompute if any of the grid data are
	 * changed. We keep a "dirty bit" to indicate that the cached derivatives
	 * are no longer valid.
	 */
	private boolean dirty = true;

	/**
	 * Creates the grid with the default values for the abscissa starting value,
	 * resolution and range.
	 */
	public Grid() {
		this(DEFAULT_LB_STARTING_VALUE, DEFAULT_LB_RESOLUTION, DEFAULT_LB_RANGE);
	}

	/**
	 * Creates a grid with the given parameters.
	 * 
	 * @param lbStartingValue
	 *            the starting value of the abscissa, given as the binary
	 *            logarithmic value
	 * @param lbResolution
	 *            the abscissa interval length per bin, given as the binary
	 *            logarithmic value
	 * @param lbRange
	 *            the overall abscissa range, given as the binary logarithmic
	 *            value
	 */
	public Grid(int lbStartingValue, int lbResolution, int lbRange) {
		this.reset(lbStartingValue, lbResolution, lbRange);
	}

	/**
	 * Creates the grid using physical units instead of powers of 2.
	 * 
	 * @param x0
	 *            the abscissa starting value
	 * @param dx
	 *            the abscissa bin length
	 * @param range
	 *            the abscissa range
	 */
	public Grid(double x0, double dx, double range) {
		this.reset(x0, dx, range);
	}

	/**
	 * Creates the grid from the given data arrays. The default values are used
	 * for the abscissa starting value, resolution and range.
	 * 
	 * @param xdata
	 *            the abscissa values
	 * @param ydata
	 *            the ordinate values
	 * @param ndata
	 *            the number of data points
	 */
	public Grid(double xdata[], double ydata[], int ndata) {
		this();
		this.resample(xdata, ydata, ndata);
	}

	/**
	 * Creates the the grid using the default abscissa starting value,
	 * resolution and range and setting all ordinate values to the given value.
	 * 
	 * @param ordinateValue
	 *            the (constant) ordinate value
	 */
	public Grid(double ordinateValue) {
		this();
		for (int i = 0; i < n(); i++) {
			y[i] = ordinateValue;
		}
	}

	/**
	 * Reads the grid from the given file. param filename the name of the file
	 * containing the grid values
	 */
	public Grid(String filename) {
		this();
		File file = new File(filename);
		try {
			URL url = file.toURL();
			InputStream inputStream = url.openStream();
			this.read(inputStream);
		} catch (MalformedURLException e) {
			System.err.println(this.getClass().getName() + ": " + e);
		} catch (IOException e) {
			System.err.println(this.getClass().getName() + ": " + e);
		}
	}

	/**
	 * Reads the grid from the given URL.
	 * 
	 * @param url
	 *            the URL from which the grid values are retrieved
	 */
	public Grid(URL url) {
		this();
		try {
			// open the resource
			InputStream inputStream = url.openStream();
			if (url.getFile().endsWith(".gz")) {
				inputStream = new GZIPInputStream(inputStream);
			}
			this.read(inputStream);
		} catch (IOException e) {
			System.err.println(this.getClass().getName() + ": " + e);
		}
	}

	/**
	 * Creates the grid by cloning the given grid.
	 * 
	 * @param grid
	 *            the grid which is cloned
	 */
	public Grid(Grid grid) {

		this.reset(grid.j(), grid.k(), grid.m());

		for (int i = 0; i < this.n; i++) {
			this.y[i] = grid.y[i];
		}
	}

	/**
	 * Frees the memory for the arrays containing the ordinate values and the
	 * second derivatives by setting the array variables to null.
	 */
	public void freeMemory() {
		y = null;
		ypp = null;
		System.gc();
	}

	/**
	 * Returns a string representation of this grid.
	 * 
	 * @return the string representation of this grid
	 */
	public String toString() {
		StringBuffer s = new StringBuffer(128);

		s.append("Grid:");
		s.append(" j: " + this.j);
		s.append(" k: " + this.k);
		s.append(" m: " + this.m);
		s.append(" n: " + this.n);

		return (s.toString());
	}

	/*
	 * Some helper functions for our powers of 2 scheme.
	 */

	/**
	 * Returns the (binary logarithmic) abscissa offset value j such that 2^j is
	 * the next power of 2 lower than the given x0
	 * 
	 * @param x0
	 *            the offset value (mustn't be less than 1)
	 * @return j such that 2**j is the next power of 2 lower than x0
	 */
	private static int findBaseTwoOffsetFromX0(double x0) {
		int v = (int) Math.floor(x0);
		int j = 0;
		while ((v >>= 1) != 0) {
			j++;
		}
		return j;
	}

	/**
	 * Returns the (binary logarithmic) resolution k such that 2**k <= dx. dx
	 * may be less than 1.
	 * 
	 * @param dx
	 *            the bin interval length
	 */
	private static int findBaseTwoResolutionFromDx(double dx) {
		/*
		 * Round this down to the next smaller power of 2. How do we do this?
		 * For dx = 7, we want to find k=2. For dx = 0.07, we want to find k=-4
		 * The trick is to start with a suitably high guess, and keep lowering
		 * it until it's small enough. We could start with a wild value, say 28,
		 * and work it down. We will seek a better start value first by rounding
		 * up, then down.
		 */
		int k = 0;
		{
			int v = (int) Math.ceil(dx);
			// v is 1 for all values of dx<1.
			// find a k large enough
			while ((1 << k) < v) {
				k++;
			}
			// for dx>1, k is now just 1 too large; for dx<1, k is only an upper
			// limit
			while (Math.pow(2, k) > dx) {
				k--;
			}
		}
		return k;
	}

	/**
	 * Returns the number m of bins such that 2**m is greater than or equal to
	 * the given range.
	 * 
	 * @param range
	 *            the range
	 * @return the number of bins
	 */
	private static int findNumberOfBinsFromRange(double range) {
		// round up to the next whole number
		range = Math.ceil(range);
		// round up to the next power of 2
		int m = 0;
		{
			int v = (int) range;
			while (v > (1 << m)) {
				m++;
			}
		}
		return m;
	}

	/**
	 * Returns the starting value of the abscissa.
	 * 
	 * @return the starting value of the abscissa (as the binary logarithmic
	 *         value)
	 */
	public int j() {
		return this.j;
	}

	/**
	 * Returns the resolution.
	 * 
	 * @return the resolution (as the binary logarithmic value)
	 */
	public int k() {
		return this.k;
	}

	/**
	 * Returns the abscissa range.
	 * 
	 * @return the abscissa range (as the binary logarithmic value)
	 */
	public int m() {
		return m;
	}

	/**
	 * Returns the starting value of the abscissa.
	 * 
	 * @return the starting value of the abscissa
	 */
	public int x0() {
		int x0 = 1 << this.j;
		return x0;
	}

	/**
	 * Returns the abscissa bin length, i.e. the spacing in x.
	 * 
	 * @return the abscissa bin length
	 */
	public double dx() {
		double dx = 0;
		if (this.k < 0) {
			dx = 1.0 / (1 << -this.k);
		} else {
			dx = (1 << this.k);
		}
		return dx;
	}

	/**
	 * Returns the number of grid points.
	 * 
	 * @return the number of grid points
	 */
	public int n() {
		return this.n;
	}

	/**
	 * Returns the abcissa value at the given index.
	 * 
	 * @param index
	 *            the index
	 * @return the abscissa value at the given index
	 */
	public double x(int index) {
		double x = this.x0() + index * this.dx();
		return x;
	}

	/**
	 * Returns the ordinate value at the given index. If the index is out of
	 * bounds, 0 is returned instead.
	 * 
	 * @return the ordinate value at the given index
	 */
	public double y(int index) {
		double y = 0;
		if (index >= 0) {
			if (index < this.n) {
				y = this.y[index];
			}
		}
		return y;
	}

	/**
	 * Return the value of the second derivative at the given index. If the
	 * index is out of bounds, 0 is returned instead.
	 * 
	 * @param index
	 *            the index
	 * @return the second derivative at the given index
	 */
	public double ypp(int index) {
		double ypp = 0;
		if (index >= 0) {
			if (index < this.n) {
				ypp = this.ypp[index];
			}
		}
		return (ypp);
	}

	/**
	 * Returns the minimum abscissa value, i.e. the starting value, as a double.
	 * 
	 * @return the minimum abscissa value
	 */
	public double xmin() {
		double xmin = this.x(0);
		return xmin;
	}

	/**
	 * Returns the maximum abscissa value as a double.
	 * 
	 * @return the maximum abscissa value
	 */
	public double xmax() {
		double xmax = this.x(this.n - 1);
		return xmax;
	}

	/**
	 * Returns the minimum ordinate value.
	 * 
	 * @return the minimum ordinate value
	 */
	public double ymin() {
		double ymin = this.y[0];
		for (int i = 1; i < this.n; i++) {
			if (this.y[i] < ymin) {
				ymin = this.y[i];
			}
		}
		return ymin;
	}

	/**
	 * Returns the maximum ordinate value.
	 * 
	 * @return the maximum ordinate value
	 */
	public double ymax() {
		double ymax = this.y[0];
		int maxi = 0;
		for (int i = 1; i < this.n; i++) {
			if (this.y[i] > ymax && this.x(i) > 3200 && this.x(i) < 10000) {
				// insert by DRH: limit max search to optical range only
				ymax = this.y[i];
				maxi = i;
			}
		}
		System.err.println("Found maximum value: " + x(maxi) + " " + y[maxi]);
		return (ymax);

	}

	/**
	 * Resets the various grid variables according to the given parameters. The
	 * dirty bit is set so that the second derivatives will be recalculated
	 * later on.
	 * 
	 * @param lbStartingValue
	 *            the starting value of the abscissa, given as the binary
	 *            logarithmic value
	 * @param lbResolution
	 *            the abscissa interval length per bin, given as the binary
	 *            logarithmic value
	 * @param lbRange
	 *            the overall abscissa range, given as the binary logarithmic
	 *            value
	 */
	public void reset(int lbStartingValue, int lbResolution, int lbRange) {
		this.j = lbStartingValue;
		this.k = lbResolution;
		this.m = lbRange;

		// There are 2 ^ lbRange / 2 ^ lbResolution
		// = 2 ^ (lbRange - lbResolution) bins.
		this.n = 1 << (m - k);

		// Create the arrays containing the ordinate values and their second
		// derivatives.
		this.y = new double[this.n];
		this.ypp = new double[this.n];

		// The second derivates will have to be computed later on...
		this.dirty = true;
	}

	/**
	 * Resets the various grid variables to match the given parameters. If
	 * necessary, the range is adjusted. The resulting range is returned. Find j
	 * such that 2**j is the next power of 2 smaller than x0. Find k such that
	 * 2**k is the next power of 2 smaller than dx. Find m such that 2**m is the
	 * next power of 2 higher than range.
	 * 
	 * @param x0
	 *            the starting value of the abscissa
	 * @param dx
	 *            the length of a bin
	 * @param range
	 *            the abscissa range
	 * @return the adjusted range
	 */
	public double reset(double x0, double dx, double range) {
		int j = Grid.findBaseTwoOffsetFromX0(x0);

		/**
		 * Note that we may need to adjust the range. The range passed in covers
		 * the domain starting at x0; j, however, may push the "neat" x0 down a
		 * considerable bit. Adjust the range to include the "neatened" amount
		 * to the left of x0.
		 */
		range += x0 - (1 << j);
		int k = Grid.findBaseTwoResolutionFromDx(dx);
		int m = Grid.findNumberOfBinsFromRange(range);
		this.reset(j, k, m);

		return range;
	}

	/**
	 * Reads data into arrays from an input stream. The number of data points is
	 * returned.
	 * 
	 * @param xdata
	 *            the abscissa values
	 * @param ydata
	 *            the ordinate values
	 * @param inputStream
	 *            the input stream from which the data is read
	 * @return the number of data points
	 * @throws InvalidValueException
	 *             if an exception is raised while reading in the grid data
	 */
	public int read(double xdata[], double ydata[], InputStream inputStream) {
		int ndata = 0;
		int nmax = Math.min(xdata.length, ydata.length);

		try {
			InputStreamReader isr = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(isr);

			boolean done = false;

			boolean firstProcessed = false;
			double lambda = 0;
			// read the data
			while (!done) {
				String s = br.readLine();
				if (s == null) {
					done = true;
				} else {
					StringTokenizer st = new StringTokenizer(s);
					// check for comment lines
					String s1 = st.nextToken();
					s1 = s1.trim();
					if (s1.charAt(0) == '!' || s1.charAt(0) == '#'
							|| s1.matches(" *")) {
						// comment
					} else if (!st.hasMoreTokens()) {
						// we need two things per line
						throw new InvalidValueException(
								"The grid data must contain two numbers per line. Comments must be preceded by a '!' or a '#'.");
					} else {
						String s2 = st.nextToken();
						lambda = Double.parseDouble(s1);
						double value = Double.parseDouble(s2);
						if (!firstProcessed) {
							for (double ll = 3000; ll < lambda-11; ll += 10) {
								xdata[ndata] = ll;
								ydata[ndata] = 0;
								
								ndata++;
								if (ndata == nmax-1) {
									done = true;
									break;
								}
							}
							firstProcessed = true;

						}
						xdata[ndata] = lambda;
						ydata[ndata] = value;
						ndata++;
						if (ndata == nmax) {
							done = true;
						}
					}
				}
			}
			for (double ll = lambda+10; ll < 11000 && ndata < nmax; ll+=10) {
				xdata[ndata] = ll;
				ydata[ndata] = 0;
				ndata++;
			}

		} catch (NumberFormatException nfe) {
			throw new InvalidValueException(
					"The grid data contained a string which is no valid number: "
							+ nfe.getMessage()
							+ ". Each line of the data must contain two numbers. Comments must be preceded by a '!' or '#'.");
		} catch (Exception exception) {
			throw new InvalidValueException(
					"When trying to read in grid data, the following error occured: "
							+ exception.getMessage());
		}
		return ndata;
	}

	/**
	 * Reads a Grid from an InputStream. The number of data points is returned.
	 * 
	 * @param inputStream
	 *            the input stream from which the data is read
	 * @return the number of data points
	 */
	public int read(InputStream inputStream) {
		int nmax = 262144; // the maximum number of input values
		double xdata[] = new double[nmax];
		double ydata[] = new double[nmax];
		int ndata = this.read(xdata, ydata, inputStream);
		this.resample(xdata, ydata, ndata); // the data may be irregularly
		// spaced
		return ndata;
	}

	/**
	 * Computes an array of 2nd derivatives, for use later on during
	 * interpolation. This uses explicit x-values (e.g. for reading in x-values
	 * from a file).
	 * 
	 * @param ypp
	 *            the array of the second derivatives
	 * @param x
	 *            the array of abscissa values
	 * @param y
	 *            the array of ordinate values
	 * @param n
	 *            the number of bins
	 */
	private static void getYpp(double ypp[], double x[], double y[], int n) {
		// allocate the scratch space
		double[] u = new double[n];

		/* 2nd derivative is zero at the ends */
		ypp[0] = u[0] = 0.0;

		for (int i = 1; i < n - 1; i++) {
			double sig = (x[i] - x[i - 1]) / (x[i + 1] - x[i - 1]);
			double p = sig * ypp[i - 1] + 2;
			ypp[i] = (sig - 1) / p;

			/*
			 * the expression from the book: sheesh! u[i] =
			 * (6((y[i+1]-y[i])/(x[i+1]-x[i])-(y[i]-y[i-1])
			 * /(x[i]-x[i-1]))/(x[i+1]-x[i-1])-sigu[i-1])/p
			 */

			u[i] = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
			u[i] -= (y[i] - y[i - 1]) / (x[i] - x[i - 1]);
			u[i] *= (double) 6;
			u[i] /= x[i + 1] - x[i - 1];
			u[i] -= sig * u[i - 1];
			u[i] /= p;
		}

		ypp[n - 1] = 0.0;

		/* now do the back substitution */
		for (int i = n - 2; i >= 0; i--) {
			ypp[i] = (ypp[i] * ypp[i + 1]) + u[i];
		}

		/* free up the scratch space */
		u = null;

		return;
	}

	/**
	 * Use cubic spline interpolation to return the ordinate at the given
	 * abcissa value. From Numerical Recipes, Press et al. 1987 pp 88-89. This
	 * is private because Grids don't normally have x-arrays. Uses explicit
	 * x-values.
	 * 
	 * @param ypp
	 *            the array of the second derivatives
	 * @param x
	 *            the array of abscissa values
	 * @param y
	 *            the array of ordinate values
	 * @param n
	 *            the number of bins
	 * @param xForInterpolation
	 *            the abscissa value for which the interpolation is carried out
	 * @return the interpolated ordinate value
	 */
	private static double interp(double ypp[], double x[], double y[], int n,
			double xForInterpolation) {
		int k1 = 0; // guess at low bracket
		int k2 = n - 1; // guess at high bracket
		while ((k2 - k1) > 1) {
			int k = (k1 + k2) / 2;
			if (x[k] > xForInterpolation) {
				k2 = k;
			} else {
				k1 = k;
			}
		}

		double h = x[k2] - x[k1];

		if (h == 0.0) {
			return 0.0;
		}

		double a = (x[k2] - xForInterpolation) / h;
		double b = (xForInterpolation - x[k1]) / h;
		double c = (a * a * a - a) * (h * h / 6);
		double d = (b * b * b - b) * (h * h / 6);

		double yInterpolated = 0.0;
		yInterpolated += a * y[k1];
		yInterpolated += b * y[k2];
		yInterpolated += c * ypp[k1];
		yInterpolated += d * ypp[k2];
		return yInterpolated;
	}

	/**
	 * Interpolate a value on the grid. This is different from the static
	 * interp() method in that we have no array of x-values.
	 * 
	 * @param xForInterpolation
	 *            the abscissa value for which the interpolation is carried out
	 * @return the interpolated ordinate value
	 */
	public double interp(double xForInterpolation) {
		if (this.dirty) {
			// we need to recompute the derivatives

			// allocate the scratch space
			double[] u = new double[this.n];

			/* 2nd derivative is zero at the ends */
			this.ypp[0] = u[0] = 0.0;

			for (int i = 1; i < this.n - 1; i++) {
				double sig = (this.x(i) - this.x(i - 1))
						/ (this.x(i + 1) - this.x(i - 1));
				double p = sig * ypp[i - 1] + 2;
				this.ypp[i] = (sig - 1) / p;

				/*
				 * the expression from the book: sheesh! u[i] =
				 * (6((y[i+1]-y[i])/(x[i+1]-x[i])-(y[i]-y[i-1])
				 * /(x[i]-x[i-1]))/(x[i+1]-x[i-1])-sigu[i-1])/p
				 */

				u[i] = (this.y[i + 1] - this.y[i])
						/ (this.x(i + 1) - this.x(i));
				u[i] -= (this.y[i] - this.y[i - 1])
						/ (this.x(i) - this.x(i - 1));
				u[i] *= (double) 6;
				u[i] /= this.x(i + 1) - this.x(i - 1);
				u[i] -= sig * u[i - 1];
				u[i] /= p;
			}

			this.ypp[n - 1] = 0.0;

			/* now do the back substitution */
			for (int i = this.n - 2; i >= 0; i--) {
				this.ypp[i] = (this.ypp[i] * this.ypp[i + 1]) + u[i];
			}

			/* free up the scratch space */
			u = null;

			// we are clean now
			this.dirty = false;
		}

		int k1 = 0; // guess at low bracket
		int k2 = this.n - 1; // guess at high bracket
		while ((k2 - k1) > 1) {
			int k = (k1 + k2) / 2;
			if (this.x(k) > xForInterpolation) {
				k2 = k;
			} else {
				k1 = k;
			}
		}

		double h = this.x(k2) - this.x(k1);

		if (h == 0.0) {
			return 0.0;
		}

		double a = (this.x(k2) - xForInterpolation) / h;
		double b = (xForInterpolation - this.x(k1)) / h;
		double c = (a * a * a - a) * (h * h / 6);
		double d = (b * b * b - b) * (h * h / 6);

		double yInterpolated = 0.0;
		yInterpolated += a * this.y[k1];
		yInterpolated += b * this.y[k2];
		yInterpolated += c * this.ypp[k1];
		yInterpolated += d * this.ypp[k2];

		return yInterpolated;
	}

	/**
	 * Sets the ordinate value at the given index; ignore it if the index is out
	 * of bounds.
	 * 
	 * @param index
	 *            the index
	 * @param y
	 *            the value to be assigned
	 */
	public void setValue(int index, double y) {
		if (index >= 0) {
			if (index < this.n) {
				this.y[index] = y;
				this.dirty = true; // someone may need to recompute derivatives
			}
		}
		return;
	}

	/**
	 * Changes the resolution of a grid. Note that if we are already at the
	 * right resolution, we do nothing and there is no penalty.
	 * 
	 * @param lbResolution
	 *            the new resolution, given as the binary logarithmic value
	 */
	public void resample(int lbResolution) {
		while (this.k < lbResolution) {
			// go to lower resolution (combine bins)
			for (int i = 0; i < this.n - 1; i += 2) {
				this.y[i / 2] = (this.y[i] + this.y[i + 1]) / 2;
			}
			// abandon the unused bins at the end
			this.k++;
			this.n /= 2;
		}

		while (this.k > lbResolution) {
			// go to higher resolution (split bins)
			double ynew[] = new double[this.n * 2];
			for (int i = 0; i < ynew.length; i++) {
				ynew[i] = this.y[i / 2];
			}
			this.y = ynew;
			this.k--;
			this.n *= 2;
		}

		return;
	}

	/**
	 * Resamples the given data onto the grid. The data may be irregularly
	 * spaced.
	 * 
	 * @param xdata
	 *            the abscissa data
	 * @param ydata
	 *            the ordinate data
	 * @param ndata
	 *            the number of data points
	 */
	public void resample(double xdata[], double ydata[], int ndata) {
		// resample the file data onto ourselves
		double ypp[] = new double[ndata];
		Grid.getYpp(ypp, xdata, ydata, ndata);
		for (int i = 0; i < this.n; i++) { // loop over our x-values, not the
			// file's
			double xForInterpolation = this.x(i);
			// only interpolate in the domain of the given data arrays
			if (xForInterpolation < xdata[0]) {
				this.y[i] = 0;
			} else if (xForInterpolation > xdata[ndata - 1]) {
				this.y[i] = 0;
			} else {
				this.y[i] = Grid.interp(ypp, xdata, ydata, ndata,
						xForInterpolation);
			}
		}
	}

	/**
	 * Adds the given value to all the ordinate grid values.
	 * 
	 * @param addedValue
	 *            the value added to all ordinate grid values
	 */
	public void add(double addedValue) {
		for (int i = 0; i < this.n; i++) {
			this.y[i] += addedValue;
		}
	}

	/**
	 * Adds the ordinate values of the given grid to the corresponding values of
	 * grid.
	 * 
	 * @param grid
	 *            the grid whose ordinate values are added to this grid
	 */
	public void add(Grid grid) {
		// force a match in resolution
		grid.resample(this.k);

		// find the partner of our bin 0 in the other grid
		int offset = this.x0() - grid.x0(); // offset in Angstroms
		if (this.k < 0) {
			offset *= (1 << -this.k);
		} else {
			offset /= (1 << this.k);
		}

		for (int i = 0; i < this.n; i++) {
			this.y[i] += grid.y[i];
		}
	}

	/**
	 * Scales the ordinate values by the given value.
	 * 
	 * @param scalingFactor
	 *            the factor by which the ordinate values are scaled
	 */
	public void scale(double scalingFactor) {
		for (int i = 0; i < this.n; i++) {
			this.y[i] *= scalingFactor;
		}
	}

	/**
	 * Scales this grid with the given grid.
	 * 
	 * @param grid
	 *            the grid with which this grid is scaled
	 */
	public void scale(Grid grid) {
		// force a match in resolution
		grid.resample(this.k);

		// find the partner of our bin 0 in the other grid
		int offset = this.x0() - grid.x0(); // offset in Angstroms
		if (this.k < 0) {
			offset *= (1 << -this.k);
		} else {
			offset /= (1 << this.k);
		}

		for (int i = 0; i < this.n; i++) {
			this.y[i] *= grid.y[i];
		}

		return;
	}

	/**
	 * Scales this grid such that it has the given ordinate at the given
	 * abscissa value.
	 * 
	 * @param x
	 *            the abscissa value
	 * @param ynew
	 *            the new ordinate value at the abscissa value
	 */
	public void scale(double x, double ynew) {
		double yold = this.interp(x);
		if (yold != 0) {
			double s = ynew / yold;
			this.scale(s);
		}
	}

	/**
	 * Divides this grid by the given grid.
	 * 
	 * @param grid
	 *            the grid by which to divide this grid
	 */
	public void div(Grid grid) {
		// force a match in resolution
		grid.resample(this.k);

		// find the partner of our bin 0 in the other grid
		int offset = this.x0() - grid.x0(); // offset in Angstroms
		if (this.k < 0) {
			offset *= (1 << -this.k);
		} else {
			offset /= (1 << this.k);
		}

		for (int i = 0; i < this.n; i++) {
			double y = grid.y[i];
			if (y != 0) {
				this.y[i] /= y;
			}
		}
	}

	/** Inverts all the ordinate values of this grid. */
	public void invert() {
		for (int i = 0; i < this.n; i++) {
			if (this.y[i] != 0) {
				this.y[i] = 1.0 / (this.y[i]);
			}
		}
		return;
	}

	/**
	 * Raises all the ordinate values of this grid to the given power.
	 * 
	 * @param s
	 *            the power
	 */
	public void power(double s) {
		for (int i = 0; i < this.n; i++) {
			this.y[i] = Math.pow(this.y[i], s);
		}
	}

	/**
	 * Replaces each ordinate value y by base^y, where base is a given number.
	 * 
	 * @param base
	 *            the base which is raised by the ordinate values
	 */
	public void raise(double base) {
		for (int i = 0; i < this.n; i++) {
			this.y[i] = Math.pow(base, this.y[i]);
		}
	}

	/** Ensures that the second derivatives will be updated. */
	protected void ensureUpdate() {
		dirty = true;
	}

	/**
	 * Writes the grid values to the given output stream. Each line is of the
	 * form "x y", where x and y denote the abscissa and the corresponding
	 * ordinate value, respectively.
	 * 
	 * @param outputStream
	 *            the output stream
	 */
	public void write(OutputStream outputStream) {
		try {
			for (int i = 0; i < this.n; i++) {

				String s = String.format ("% 7.2f  % 7.2f \n", this.x(i), this.y[i]);
				outputStream.write(s.getBytes());
			}
		} catch (IOException e) {
			System.err.println(this.getClass().getName() + ".write: " + e);
		}
	}

	/**
	 * Writes the grid values to the named file, using the write(OutputStream)
	 * method.
	 * 
	 * @param filename
	 *            the name of the file
	 */
	public void write(String filename) {
		try {
			OutputStream outputStream = new FileOutputStream(filename);
			this.write(outputStream);
		} catch (IOException e) {
			System.err.println(this.getClass().getName() + ".write: " + e);
		}
	}

	/**
	 * Lets the user choose a file by means of a file dialog and then writes the
	 * grid values to that file, using the write(String) method.
	 */
	public void write() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save a File...");
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		File file = new File("spectrum.txt");
		chooser.setSelectedFile(file);
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			String filename = file.getPath();
			this.write(filename);
		}
	}
}