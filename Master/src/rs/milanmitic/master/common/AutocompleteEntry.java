package rs.milanmitic.master.common;

import java.io.Serializable;

public class AutocompleteEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String label;

	private transient  Object value;

	private transient  Object item1;

	private transient  Object item2;

	private transient  Object item3;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getItem1() {
		return item1;
	}

	public void setItem1(Object item1) {
		this.item1 = item1;
	}

	public Object getItem2() {
		return item2;
	}

	public void setItem2(Object item2) {
		this.item2 = item2;
	}

	public Object getItem3() {
		return item3;
	}

	public void setItem3(Object item3) {
		this.item3 = item3;
	}

}
