/**
 * 
 */
package net.formula97.webapp.pims.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import net.formula97.webapp.pims.domain.Users;

/**
 * @author f97one
 *
 */
public class AuthorizedUsers extends User {

    /**
     * 
     */
    private static final long serialVersionUID = 9030295882512596539L;
    
    private final Users users;
    
    public AuthorizedUsers(Users user) {
        super(user.getUserId(), user.getEncodedPasswd(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        this.users = user;
    }
    
    public Users getUsers() {
        return this.users;
    }
    
}
