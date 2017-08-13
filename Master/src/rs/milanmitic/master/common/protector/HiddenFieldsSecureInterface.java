package rs.milanmitic.master.common.protector;

/**
 * Object that has hidden fields to secure must implement this interface
 * 
 * @author milan
 * 
 */
public interface HiddenFieldsSecureInterface {

	public String getSecureFields();

	public String generateSecureFieldsHash();

}
