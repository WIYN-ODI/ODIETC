package org.wiyn.etc.odi.gui;

import java.awt.BorderLayout;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.wiyn.etc.configuration.PhotometryExposureResult;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SNRTable extends JPanel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    JTable myTable;
    myTableModel theModel;

    public SNRTable() {
	super ();

	theModel = new myTableModel (null);
	myTable = new JTable (theModel);
	for (int ii = 0; ii < ExposurePanel.Apertures.length; ii++) {
	    TableColumn tc = myTable.getColumnModel ().getColumn (ii+1);
	    tc.setHeaderValue (ExposurePanel.Apertures[ii]);
	    tc.setMaxWidth (55);
	}
	  TableColumn tc = myTable.getColumnModel ().getColumn (0);
	  tc.setHeaderValue ("Aperture Radius [xFWHM]");
	  tc.setMinWidth (130);
	 setLayout(new BorderLayout());
	// this.setPreferredSize(new java.awt.Dimension(342, 110));

	add (myTable.getTableHeader (), BorderLayout.PAGE_START);
	add (myTable, BorderLayout.CENTER);

    }

    public void update (Vector<PhotometryExposureResult> Results) {
	theModel.update (Results);
	myTable.tableChanged (new TableModelEvent (theModel));
	System.err.println ("Updating Results Table");
    }

    class myTableModel extends DefaultTableModel {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	Vector<PhotometryExposureResult> Results = null;
	NumberFormat myNF = null;
	NumberFormat FluxFormat = null;

	public myTableModel(Vector<PhotometryExposureResult> Results) {
	    this.Results = Results;

	    myNF = NumberFormat.getInstance ();
	    myNF.setMinimumFractionDigits (2);
	    myNF.setMaximumFractionDigits (2);

	    FluxFormat = NumberFormat.getInstance ();
	    FluxFormat.setMinimumFractionDigits (0);
	    FluxFormat.setMaximumFractionDigits (0);

	}

	public void update (Vector<PhotometryExposureResult> Results) {
	    this.Results = Results;
	}

	public int getColumnCount () {

	    return ExposurePanel.Apertures.length + 1;

	}

	public int getRowCount () {
	    return 3;
	}

	public Object getValueAt (int Row, int column) {
	    if (Results != null && column != 0) {
		PhotometryExposureResult r = Results.elementAt (column - 1);

		if (Row == 0)
		    return (myNF.format (r.SN));
		if (Row == 1)
		    return (myNF.format (0.92 / r.SN));

		if (Row == 2)
		    return (FluxFormat.format (r.ApertureFlux));
	    }

	    if (column == 0) {

		if (Row == 0)
		    return "Signal/Noise";
		if (Row == 1)
		    return ("error [mag]");
		if (Row == 2)
		    return ("encircled flux [e-]");

	    }
	    return new Double (0);

	}
    }

}
