package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.UserSearchConditionForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by f97one on 2016/10/22.
 */
@Controller
@RequestMapping("admin")
public class AdminController extends BaseWebController {

    @RequestMapping(method = RequestMethod.GET)
    public String showMenu(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        return "/admin/adminMenu";
    }

    @RequestMapping(value = "system", method = RequestMethod.GET)
    public String showSystem(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        return "/admin/system";
    }

    @RequestMapping(value = "userManagement", method = RequestMethod.GET)
    public String showUserManagement(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        UserSearchConditionForm frm = new UserSearchConditionForm();
        model.addAttribute("userSearchConditionForm", frm);
        model.addAttribute("matchUserCount", 0);
        model.addAttribute("dispUserList", new ArrayList<Users>());
        model.addAttribute("searchExecuted", false);

        return "/admin/user_list";
    }

    public String showLedgerManagement(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        return "/admin/ledgerManagement";
    }

    @RequestMapping(value = "userManagement/searchUser", method = RequestMethod.GET, name = "searchBtn")
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
}
