package rs.milanmitic.master.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * User password encoder. Currently extend spring BCryptPasswordEncoder, but
 * later can be changed.<br/>
 * @author milan
 * 
 */
@Component(value = "masterUserPasswordEncoder")
public class MasterUserPasswordEncoder extends BCryptPasswordEncoder {

}
