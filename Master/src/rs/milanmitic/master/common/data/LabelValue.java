package rs.milanmitic.master.common.data;

import java.io.Serializable;

import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.util.Utils;

/**
 * Label and value - used to display values in select box
 * 
 * @author milan
 * 
 */
public class LabelValue implements SelectFieldIntf, Serializable, Comparable<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String label;

	private transient Object value;
	
	private boolean isGroup = false;

	public LabelValue() {
		this.label = null;
		this.value = null;
		this.isGroup=false;
	}

	public LabelValue(String label, Object value) {
		super();
		this.label = label;
		this.value = value;
		this.isGroup=false;
	}

	public LabelValue(String label, Object value, boolean isGroup) {
		super();
		this.label = label;
		this.value = value;
		this.isGroup=isGroup;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

	@Override
	public int compareTo(Object o1) {
		if (o1 == null)
			return 0;
		if (!(o1 instanceof LabelValue))
			return 0;
		LabelValue o = (LabelValue) o1;
		return o.getLabel() != null ? o.getLabel().compareTo(this.getLabel()) : 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LabelValue))
			return false;
		LabelValue other = (LabelValue) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

}
