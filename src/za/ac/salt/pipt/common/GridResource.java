package za.ac.salt.pipt.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/** A class to find and open a file resource for Grid data. */
public class GridResource
{
    /** a website host */
    private String host;
	
    /** a file name, resource name, or path on the website. */	
    private String path;
	

    /** Set the host and path to the given values
     * @param host the host
     * @param path the path */
    public GridResource(String host, String path)
    {
	this.host = host;
	this.path = path;
    }


    /** Return a valid URL for the resource. First try it as a file, then use the class loader, then hunt out on the web. Return null on failure.
     * @return the the URL or (if no resource could be found) null */
    public URL getURL()
    {
	URL url = null;
		
	try {
	//    File f = new File(path);
	 //   if (f.exists()) {
//		url = f.toURL();
//	    } else {
		// ask the class loader
		url = GridResource.class.getResource(path);
		if (url == null) {
		    // no help from the class loader
		    url = new URL(host + path);
		    url.openConnection();
		    InputStream is = url.openStream();	// test it
		}
//	    }
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	    url = null;
	} catch (IOException e) {
	    e.printStackTrace();
	    url = null;
	}
	return url;
    }
	

    /** Returns a valid innput stream for the URL returned by the getURL() method..
     * @return the input stream */	
    public InputStream getInputStream()
    {
	InputStream inputStream = null;
	URL url = this.getURL();
	if (url != null) {
	    try {
		inputStream = url.openStream();
	    } catch(IOException e) {
		e.printStackTrace();
		inputStream = null;
	    }
	}
	return(inputStream);
    }
}