package rs.milanmitic.master.common.data;

import java.io.Serializable;

import rs.milanmitic.master.common.security.UserType;
import rs.milanmitic.master.common.util.Utils;

/**
 * Participant data used later in application
 * 
 * @author milan
 * 
 */
public class LoggedParticipantData extends LoggedUserData implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long participantId;

	private String participantName;

	@Override
	public UserType getUserType() {
		return UserType.USER;
	}

	/**
	 * @return the participantId
	 */
	public Long getParticipantId() {
		return participantId;
	}

	/**
	 * @param participantId
	 *            the participantId to set
	 */
	public void setParticipantId(Long participantId) {
		this.participantId = participantId;
	}

	/**
	 * @return the participantName
	 */
	public String getParticipantName() {
		return participantName;
	}

	/**
	 * @param participantName
	 *            the participantName to set
	 */
	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}

	@Override
	public String toString() {
		return Utils.toStringGlobal(this);
	}

}
