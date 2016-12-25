package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.UserModForm;
import net.formula97.webapp.pims.web.forms.UserSearchConditionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by f97one on 2016/12/23.
 */
@Controller
@RequestMapping("admin/userManagement")
public class AdminUserManagementController extends BaseWebController {

    @RequestMapping(value = "searchUser", method = RequestMethod.GET, name = "searchBtn")
    public String searchUserByCondition(@ModelAttribute("userSearchConditionForm") UserSearchConditionForm userSearchConditionForm,
                                        BindingResult result, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        if (result.hasErrors()) {
            model.addAttribute("matchUserCount", 0);
            return "/admin/user_list";
        }
        model.addAttribute("searchExecuted", true);

        List<Users> searchResult = authorizedUsersSvc.findUsers(userSearchConditionForm);
        model.addAttribute("matchUserCount", searchResult.size());
        model.addAttribute("dispUserList", searchResult);

        return "/admin/user_list";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String showUserAddView(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        UserModForm userModForm = new UserModForm();
        userModForm.setAssignedRole(AppConstants.ROLE_USER);
        model.addAttribute("userModForm", userModForm);

        model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);

        return "/admin/user_detail";
    }
}
