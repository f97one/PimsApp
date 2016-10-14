/**
 * 
 */
package net.formula97.webapp.pims.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.AuthorizedUsers;
import net.formula97.webapp.pims.service.AuthorizedUsersService;
import net.formula97.webapp.pims.service.SystemConfigService;
import net.formula97.webapp.pims.web.forms.HeaderForm;

/**
 * @author f97one
 *
 */
@Controller
public class BaseWebController {

    @Autowired
    AuthorizedUsersService authorizedUsersSvc;
    @Autowired
    SystemConfigService sysConfSvc;

    @ModelAttribute
    HeaderForm setUpForm() {
        return new HeaderForm();
    }

    protected Users getUserState(Model model, Principal principal) {
        Users users = null;
        
        if (principal != null) {
            Authentication auth = (Authentication) principal;
            AuthorizedUsers authUsers = (AuthorizedUsers) auth.getPrincipal();
            
            users = authUsers.getUsers();
        }
        
        return users;
    }

    protected void putErrMsg(Model model, String errMessage) {
        model.addAttribute("errMsg", errMessage);
    }
}
