/**
 * 
 */
package net.formula97.webapp.pims.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

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
    
    protected void setUserInfo(AuthorizedUsers authUsers) {
        
    }
    
}
