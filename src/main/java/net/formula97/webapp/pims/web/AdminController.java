package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.CategoryMaster;
import net.formula97.webapp.pims.domain.MasterDomain;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.MasterConfigService;
import net.formula97.webapp.pims.service.StatusMasterService;
import net.formula97.webapp.pims.service.SystemConfigService;
import net.formula97.webapp.pims.web.forms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    HttpSession httpSession;
    @Autowired
    private MasterConfigService masterConfigService;

    private final String mMessageSessionKey = "messageSessionKey";

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
            case MasterConfigService.MASTER_TYPE_STATUS:
                masterTypeName = "ステータス";
                itemNameLength = 16;
                break;
            case MasterConfigService.MASTER_TYPE_SEVERE_LEVEL:
                masterTypeName = "緊急度";
                itemNameLength = 8;
                break;
            case MasterConfigService.MASTER_TYPE_PROCESS:
                masterTypeName = "工程";
                itemNameLength = 16;
                break;
            case MasterConfigService.MASTER_TYPE_CATEGORY:
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

        CurrentItemForm currentItemForm = new CurrentItemForm();
        currentItemForm.setItemNameLength(itemNameLength);
        currentItemForm.setMasterType(masterType);

        try {
            List<MasterDomain> masterDomainList = masterConfigService.getWholeMasterByType(masterType);

            List<MasterItem> masterItems = new ArrayList<>();

            for (MasterDomain md : masterDomainList) {
                switch (masterType) {
                    case MasterConfigService.MASTER_TYPE_CATEGORY:
                        CategoryMaster cm = (CategoryMaster) md;
                        masterItems.add(new MasterItem(cm.getCategoryId(), cm.getCategoryName(), cm.getDispOrder(), false));
                        break;

                }
            }

            currentItemForm.setMasterList(masterItems);

        } catch (Exception e) {
            putErrMsg(model, "指定されたマスタはありません。");

            currentItemForm.setMasterList(new ArrayList<>(1));
        }

        model.addAttribute("currentItemForm", currentItemForm);

        return "/admin/master_config";
    }
}
