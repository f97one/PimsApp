package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.SystemConfig;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.StatusMasterService;
import net.formula97.webapp.pims.service.SystemConfigService;
import net.formula97.webapp.pims.web.forms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by f97one on 2016/10/22.
 */
@Controller
@RequestMapping("admin")
public class AdminController extends BaseWebController {

    @Autowired
    private StatusMasterService statusMasterSvc;
    @Autowired
    private SystemConfigService sysConfigSvc;

    @RequestMapping(method = RequestMethod.GET)
    public String showMenu(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        return "/admin/adminMenu";
    }

    @RequestMapping(value = "system", method = RequestMethod.GET)
    public String showSystem(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        SystemPreferenceForm systemPreferenceForm = sysConfigSvc.getViewForm();
        model.addAttribute("systemPreferenceForm", systemPreferenceForm);

        return "/admin/system_preference";
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

    @RequestMapping(value = "master", method = RequestMethod.GET)
    public String showMasterConfig(@RequestParam(name = "masterType") String masterType, Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        String masterTypeName;
        int itemNameLength;
        switch (masterType) {
            case "status":
                masterTypeName = "ステータス";
                itemNameLength = 16;
                break;
            case "severelevel":
                masterTypeName = "緊急度";
                itemNameLength = 8;
                break;
            case "process":
                masterTypeName = "工程";
                itemNameLength = 16;
                break;
            case "category":
                masterTypeName = "カテゴリー";
                itemNameLength = 128;
                break;
            default:
                masterTypeName = "";
                itemNameLength = 8;
                break;
        }
        model.addAttribute("masterTypeName", masterTypeName);

        model.addAttribute("masterType", masterType);

        // 新規追加用Form
        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setMasterType(masterType);
        newItemForm.setItemNameLength(itemNameLength);

        model.addAttribute("newItemForm", newItemForm);



        return "/admin/master_config";
    }
}
