package org.wiyn.etc.configuration;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import za.ac.salt.pipt.common.Phase;
import za.ac.salt.pipt.common.dataExchange.PiptData;
import za.ac.salt.pipt.common.dataExchange.PiptDataElement;

/**
 * A data holding class to define an exposure configuration
 */

public class ExposureConfig implements PiptData {

    public double ExposureTime = 0;
    public long ExposureRepeat = 1;
    public long ExposureBinning = 1;

    public ExposureConfig() {

    }

    public void setExposureTime (double exptime) {
	this.ExposureTime = exptime;
    }

    public void safeSetExposureTime (double ExposureTime) {

	if (ExposureTime >= 0 && ExposureTime < 3600 * 10) {
	    setExposureTime (ExposureTime);
	}

    }

    public double getExposureTime () {
	return ExposureTime;

    }

    public ArrayList<PiptDataElement> getChildren () {
	// TODO Auto-generated method stub
	return null;
    }

    public boolean isRequired (String childElement, Phase phase) {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean isSubTreeComplete (Phase phase) {
	// TODO Auto-generated method stub
	return false;
    }

    public void addPropertyChangeListener (PropertyChangeListener listener) {
	// TODO Auto-generated method stub

    }

    public void removePropertyChangeListener (PropertyChangeListener listener) {
	// TODO Auto-generated method stub

    }

    public double getExposureRepeat () {
	return ExposureRepeat;
    }

    public void setExposureRepeat (double exposureRepeat) {
	ExposureRepeat = (long) exposureRepeat;
    }

    public void safeSetExposureRepeat (double exposureRepeat) {
	if (exposureRepeat >= 1 && exposureRepeat < 999) {
	    setExposureRepeat (exposureRepeat);
	}
    }

    public double getExposureBinning () {
	return ExposureBinning;
    }

    public void setExposureBinning (double exposureBinning) {
	ExposureBinning = (long) exposureBinning;
    }

    public void safeSetExposureBinning (double exposureBinning) {
	if (exposureBinning >= 1 && ExposureBinning <= 4) {
	    this.setExposureBinning (exposureBinning);
	}
    }

}