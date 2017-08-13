package rs.milanmitic.master.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import rs.milanmitic.master.common.security.UserRole;

@Service("userDetailsService")
public class MasterUserDetailsService implements UserDetailsService {

	@Transactional
	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		setAuths.add(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.getId()));

		User user = new User(username, "$2a$10$Ls68IS75JOU8.0IHVHGIIuck.YrpCCZ4xulJJD92DM08jI2x/A3De", true, true, true, true, setAuths);

		return user;

	}
}
