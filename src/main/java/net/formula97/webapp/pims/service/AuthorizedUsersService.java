/**
 * 
 */
package net.formula97.webapp.pims.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.UserRepository;

/**
 * @author f97one
 *
 */
@Service
public class AuthorizedUsersService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    
    /**
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Users users = userRepository.findOne(userId);
        
        if (users == null) {
            throw new UsernameNotFoundException(String.format(Locale.getDefault(), "Requested userId ( %s ) not found.", userId));
        }
        
        return new AuthorizedUsers(users);
    }

}
