/**
 * 
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findOne(username);
        
        if (users == null || username.length() == 0) {
            throw new UsernameNotFoundException(String.format(Locale.getDefault(), "Requested username ( %s ) not found.", username));
        }
        
        return new AuthorizedUsers(users);
    }

    @Transactional
    public void saveUsers(Users users) {
        userRepository.save(users);
    }

}
