package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.*;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

        putMasterList(masterType, model, itemNameLength);

        return "/admin/master_config";
    }

    /**
     * マスタの一覧をViewに張る。
     *
     * @param masterType     選択されているマスタの種別
     * @param model          Viewに張るmodelのインスタンス
     * @param itemNameLength マスタの長さの上限
     */
    private void putMasterList(@RequestParam(name = "masterType") String masterType, Model model, int itemNameLength) {
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
                    case MasterConfigService.MASTER_TYPE_PROCESS:
                        ProcessMaster pm = (ProcessMaster) md;
                        masterItems.add(new MasterItem(pm.getProcessId(), pm.getProcessName(), pm.getDispOrder(), false));
                        break;
                    case MasterConfigService.MASTER_TYPE_SEVERE_LEVEL:
                        SevereLevelMaster slm = (SevereLevelMaster) md;
                        masterItems.add(new MasterItem(slm.getSevereLevelId(), slm.getSevereLevel(), slm.getDispOrder(), false));
                        break;
                    case MasterConfigService.MASTER_TYPE_STATUS:
                        StatusMaster sm = (StatusMaster) md;
                        masterItems.add(new MasterItem(sm.getStatusId(), sm.getStatusName(), sm.getDispOrder(), sm.getTreatAsFinished()));
                        break;
                }
            }

            currentItemForm.setMasterList(masterItems);

        } catch (Exception e) {
            putErrMsg(model, "指定されたマスタはありません。");

            currentItemForm.setMasterList(new ArrayList<>(1));
        }

        model.addAttribute("currentItemForm", currentItemForm);
    }

    @RequestMapping(value = "master/add", method = RequestMethod.POST, params = "newItemAdd")
    public String addMasterByType(@RequestParam(name = "masterType") String masterType, Model model, HeaderForm headerForm, NewItemForm newItemForm, RedirectAttributes redirectAttributes) {
        Users myUserDetail = getUserState(model, headerForm);

        // 文字列長評価処理
        // 上限を超えたもの、または空を受け取った場合はエラーにする
        String itemError = null;
        String processMsg;
        int itemNameLength;

        switch (masterType) {
            case MasterConfigService.MASTER_TYPE_CATEGORY:
                processMsg = "カテゴリー";
                itemNameLength = 128;

                if (newItemForm.getItemName().length() > itemNameLength) {
                    itemError = makeItemErrorMsg(processMsg, itemNameLength);
                }
                break;
            case MasterConfigService.MASTER_TYPE_PROCESS:
                processMsg = "工程";
                itemNameLength = 16;

                if (newItemForm.getItemName().length() > itemNameLength) {
                    itemError = makeItemErrorMsg(processMsg, itemNameLength);
                }
                break;
            case MasterConfigService.MASTER_TYPE_SEVERE_LEVEL:
                processMsg = "緊急度";
                itemNameLength = 8;

                if (newItemForm.getItemName().length() > itemNameLength) {
                    itemError = makeItemErrorMsg(processMsg, itemNameLength);
                }
                break;
            case MasterConfigService.MASTER_TYPE_STATUS:
                processMsg = "ステータス";
                itemNameLength = 16;

                if (newItemForm.getItemName().length() > itemNameLength) {
                    itemError = makeItemErrorMsg(processMsg, itemNameLength);
                }
                break;
            default:
                processMsg = "";
                itemNameLength = 16;

                break;
        }

        // エラーメッセージがある場合はエラーメッセージを張る
        if (itemError != null) {
            model.addAttribute("itemError", itemError);
            model.addAttribute("newItemForm", newItemForm);

            model.addAttribute("masterTypeName", processMsg);
            model.addAttribute("masterType", masterType);

            putMasterList(masterType, model, itemNameLength);

            return "/admin/master_config";
        }

        try {
            switch (masterType) {
                case MasterConfigService.MASTER_TYPE_CATEGORY:
                case MasterConfigService.MASTER_TYPE_PROCESS:
                case MasterConfigService.MASTER_TYPE_SEVERE_LEVEL:
                    masterConfigService.addMasterByType(masterType, newItemForm.getItemName());
                    newItemForm.setItemName("");
                    break;
                case MasterConfigService.MASTER_TYPE_STATUS:
                    masterConfigService.addMasterByType(masterType, newItemForm.getItemName(), newItemForm.isTreatAsFinished());
                    newItemForm.setItemName("");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            putErrMsg(model, "エラーにより登録に失敗しました。");
        }

        redirectAttributes.addFlashAttribute("processMsg", processMsg + "を追加しました。");

        model.addAttribute("newItemForm", newItemForm);

        return "redirect:/admin/master?masterType=" + masterType;
    }

    @RequestMapping(value = "master/edit", method = RequestMethod.POST, params = "updateBtn")
    public String updateMasterListByType(@RequestParam(name = "masterType") String masterType, Model model,
                                         HeaderForm headerForm, CurrentItemForm currentItemForm, RedirectAttributes redirectAttributes) {

        Users myUserDetail = getUserState(model, headerForm);

        List<MasterItem> masterItemList = currentItemForm.getMasterList();
        List<MasterDomain> masterDomainList = new ArrayList<>();

        switch (masterType) {
            case MasterConfigService.MASTER_TYPE_CATEGORY:
                for (MasterItem i : masterItemList) {
                    masterDomainList.add(new CategoryMaster(i.getItemId(), i.getItemName(), i.getDisplayOrder()));
                }
                break;
        }

        masterConfigService.updateDisplayOrder(masterType, masterDomainList);

        return "redirect:/admin/master?masterType=" + masterType;
    }

    private String makeItemErrorMsg(String processMsg, int itemNameLength) {
        String itemError;
        itemError = String.format(Locale.getDefault(), "%sは%d文字以内で入力してください。", processMsg, itemNameLength);
        return itemError;
    }

}
