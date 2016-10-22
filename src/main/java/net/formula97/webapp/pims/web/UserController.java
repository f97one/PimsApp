package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.AuthorizedUsersService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.UserConfigForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by f97one on 2016/10/22.
 */
@Controller
@RequestMapping("user")
public class UserController extends BaseWebController {

    @Autowired
    AuthorizedUsersService authUserSvc;

    @ModelAttribute
    private UserConfigForm setUpUserConfigForm() {
        return new UserConfigForm();
    }

    @RequestMapping(path = "config", method = RequestMethod.GET)
    public String openMyUserConfig(Model model, HeaderForm headerForm, UserConfigForm userConfigForm) {
        Users users = getUserState(model, headerForm);

        userConfigForm.setUsername(users.getUsername());
        userConfigForm.setDisplayName(users.getDisplayName());
        userConfigForm.setEnableUser(users.isEnabled());
        userConfigForm.setAssignedRole(users.getAuthority());

        userConfigForm.setDisableUsernameModify(true);
        userConfigForm.setDisableRoleModify(true);
        userConfigForm.setDisableUserEnableModify(true);
        userConfigForm.setDisableDisplayNameModify(false);

        model.addAttribute("userConfigForm", userConfigForm);

        return "user/userdetail";
    }

    @RequestMapping(path = "config", method = RequestMethod.POST, name = "updateBtn")
    public String updateUserPasswd(Model model, HeaderForm headerForm, UserConfigForm userConfigForm) {
        Users users = getUserState(model, headerForm);

        return "redirect:/user/config";
    }
}
