package rs.milanmitic.master.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import rs.milanmitic.master.common.SelectFieldIntf;
import rs.milanmitic.master.common.pagging.SearchInput;
import rs.milanmitic.master.common.protector.HiddenFieldBuilder;
import rs.milanmitic.master.common.protector.HiddenFieldsSecureInterface;
import rs.milanmitic.master.common.util.Utils;

/**
 * <p>
 * Title: Participant
 * </p>
 *
 * <p>
 * Description: Domain Object describing a Participant entity
 * </p>
 *
 */
@Entity(name = "Participant")
@Table(name = "PARTICIPANT")
public class Participant extends SearchInput implements Serializable, HiddenFieldsSecureInterface, SelectFieldIntf {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "NAME", nullable = true, unique = false)
	@Length(max = 30)
	@NotEmpty
	private String name;

	@Column(name = "ADRRESS", nullable = true, unique = false)
	@Length(max = 50)
	private String adrress;

	@Column(name = "CITY", nullable = true, unique = false)
	@Length(max = 50)
	private String city;

	@Column(name = "PHONE", nullable = true, unique = false)
	@Length(max = 20)
	private String phone;

	@Version
	private Long version;

	public Participant() {
		super();
		addColumnMapping("1", "a.id");
		addColumnMapping("2", "lower(a.name)");
		addColumnMapping("3", "lower(a.adrress)");
		addColumnMapping("4", "lower(a.city)");
		addColumnMapping("5", "lower(a.phone)");
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdrress() {
		return adrress;
	}

	public void setAdrress(String adrress) {
		this.adrress = adrress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	/** * {@inheritDoc} */
	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Participant))
			return false;
		Participant castOther = (Participant) other;
		return new EqualsBuilder().append(getId(), castOther.getId()).append(getName(), castOther.getName()).append(getAdrress(), castOther.getAdrress()).append(getCity(), castOther.getCity()).append(getPhone(), castOther.getPhone())
				.append(getVersion(), castOther.getVersion()).isEquals();
	}

	/** * {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getId()).append(getName()).append(getAdrress()).append(getCity()).append(getPhone()).append(getVersion()).toHashCode();
	}

	/**
	 * @see HiddenFieldsSecureInterface#generateSecureFieldsHash()
	 */
	@Override
	public String getSecureFields() {
		HiddenFieldBuilder h = HiddenFieldBuilder.create(this.getClass().getName()).add(this.version);
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

	@Override
	public Object getValue() {
		return id;
	}

	@Override
	public String getLabel() {
		return name;
	}

}
