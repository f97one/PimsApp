package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.StatusMasterService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.LedgerSearchConditionForm;
import net.formula97.webapp.pims.web.forms.UserSearchConditionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by f97one on 2016/10/22.
 */
@Controller
@RequestMapping("admin")
public class AdminController extends BaseWebController {

    @Autowired
    StatusMasterService statusMasterSvc;

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

    @RequestMapping(value = "ledgerManagement", method = RequestMethod.GET)
    public String showLedgerManagement(Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        Map<Integer, String> statusMap =statusMasterSvc.getStatusMap();
        model.addAttribute("statusMap", statusMap);
        model.addAttribute("ledgerSearchConditionForm", new LedgerSearchConditionForm());

        return "/admin/ledger_list";
    }
}
