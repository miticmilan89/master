package rs.milanmitic.master.service;

import org.springframework.security.core.Authentication;

import rs.milanmitic.master.common.security.MasterAuthenticationToken;

/**
 * User autentification methods
 * 
 * @author milan
 * 
 */
public interface AutentificationService {

	/**
	 * ADMIN LOGIN PROCEDURE
	 * 
	 * @param authentication
	 * @return
	 */
	public MasterAuthenticationToken adminLogin(Authentication authentication);

	/**
	 * BANK LOGIN PROCEDURE
	 * 
	 * @param authentication
	 * @return
	 */
	public MasterAuthenticationToken userLogin(Authentication authentication);

}
