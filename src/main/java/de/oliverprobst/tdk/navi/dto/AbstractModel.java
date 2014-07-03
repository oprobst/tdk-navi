package de.oliverprobst.tdk.navi.dto;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModel {

	private long timestamp;

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	protected void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	protected PropertyChangeSupport propertyChangeSupport;

	public AbstractModel() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		setTimestamp(System.currentTimeMillis());
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

}