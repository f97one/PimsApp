/**
 * 
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.CommonsStringUtils;
import net.formula97.webapp.pims.repository.MySpecificationAdapter;
import net.formula97.webapp.pims.repository.UserRepository;
import net.formula97.webapp.pims.web.forms.UserSearchConditionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * @author f97one
 *
 */
@Service
public class AuthorizedUsersService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;
    
    /**
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepo.findOne(username);
        
        if (users == null || username.length() == 0) {
            throw new UsernameNotFoundException(String.format(Locale.getDefault(), "Requested username ( %s ) not found.", username));
        }
        
        return new AuthorizedUsers(users);
    }

    @Transactional
    public void saveUsers(Users users) {
        userRepo.save(users);
    }

    public List<Users> findUsers(UserSearchConditionForm searchConditions) {
        MySpecificationAdapter<Users> saUsers = new MySpecificationAdapter<>(Users.class);

        String username = CommonsStringUtils.isNullOrWhiteSpace(searchConditions.getUsername()) ? null : searchConditions.getUsername();
        String displayName = CommonsStringUtils.isNullOrWhiteSpace(searchConditions.getDisplayName()) ? null : searchConditions.getDisplayName();
        String mailAddress = CommonsStringUtils.isNullOrWhiteSpace(searchConditions.getMailAddress()) ? null : searchConditions.getMailAddress();
        Boolean enabledUser = searchConditions.getLimitEnabledUser() ? Boolean.TRUE : null;

        return userRepo.findAll(Specifications.where(saUsers.contains("username", username))
                .and(saUsers.contains("displayName", displayName))
                .and(saUsers.contains("mailAddress", mailAddress))
                .and(saUsers.eq("enabled", enabledUser)));
    }

    public Users findUserById(String username) {
        return userRepo.findOne(username);
    }
}
