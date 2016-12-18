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

        return "/admin/userManagement";
    }

    public String showLedgerManagement(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        return "/admin/ledgerManagement";
    }

    @RequestMapping(value = "searchUser", method = RequestMethod.POST, name = "searchBtn")
    public String searchUserByCondition(@ModelAttribute("userSearchConditionForm") UserSearchConditionForm userSearchConditionForm,
                                        BindingResult result, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        if (result.hasErrors()) {
            // TODO: バリデーション失敗の処理を書く
        }


        return null;
    }
}
