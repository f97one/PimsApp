/**
 * 
 */
package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.AuthorizedUsers;
import net.formula97.webapp.pims.service.AuthorizedUsersService;
import net.formula97.webapp.pims.service.SystemConfigService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

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
    HeaderForm setUpHeaderForm() {
        return new HeaderForm();
    }

    protected Users getUserState(Model model, HeaderForm form) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Users users = null;
        
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            users = ((AuthorizedUsers) authorizedUsersSvc.loadUserByUsername(username)).getUsers();

            form.setUsers(users);
            model.addAttribute(form);
        }
        
        return users;
    }

    protected void putErrMsg(Model model, String errMessage) {
        model.addAttribute("errMsg", errMessage);
        model.addAttribute("infoMsg", null);
    }

    protected void putInfoMsg(Model model, String infoMessage) {
        model.addAttribute("errMsg", null);
        model.addAttribute("infoMsg", infoMessage);
    }
}
