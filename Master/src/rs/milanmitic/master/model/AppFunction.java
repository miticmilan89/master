package rs.milanmitic.master.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldBuilder;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 * <p>
 * Title: AppFunction
 * </p>
 *
 * <p>
 * Description: Domain Object describing a AppFunction entity
 * </p>
 *
 */
@Entity(name = "AppFunction")
@Table(name = "APP_FUNCTION")
public class AppFunction extends SearchInput implements Serializable, HiddenFieldsSecureInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "FUNCTION_NAME", nullable = false, unique = false)
	@Length(max = 50)
	@NotEmpty
	private String functionName;

	@Column(name = "URLS", nullable = true, unique = false)
	@Length(max = 1000)
	private String urls;

	public AppFunction() {
		super();
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getUrls() {
		return urls;
	}

	public void setUrls(String urls) {
		this.urls = urls;
	}

	/** * {@inheritDoc} */
	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof AppFunction))
			return false;
		AppFunction castOther = (AppFunction) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getFunctionName(), castOther.getFunctionName()).append(getUrls(), castOther.getUrls()).isEquals();
	}

	/** * {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getFunctionName()).append(getUrls()).toHashCode();
	}

	/**
	 * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()
	 */
	@Override
	public String getSecureFields() {
		HiddenFieldBuilder h = HiddenFieldBuilder.create(this.getClass().getName());
		if (id != null) {
			h.add(id);
		}
		return h.toString();
	}

	/**
	 * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()
	 */
	@Override
	public String generateSecureFieldsHash() {
		return Utils.generateSecureFieldsHash(this);
	}

}
