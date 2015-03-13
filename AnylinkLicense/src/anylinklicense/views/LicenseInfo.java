package anylinklicense.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/* test */
public class LicenseInfo {
	private String ItemName;
	private String ItemValue;
 
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport( this);
	public LicenseInfo(
			      String ItemName,
			      String ItemValue ) 
	{
		super();
 
		this.ItemName    = ItemName  ;
		this.ItemValue    = ItemValue ;
 
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
  
	public String getItemName() {
		return ItemName;
	}
	public String getItemValue() {
		return ItemValue;
	}	
 
	public void setItemName(String ItemName) {
		this.ItemName = ItemName;
	}
	public void setItemValue(String ItemValue) {
		this.ItemValue = ItemValue;
	}
 
}