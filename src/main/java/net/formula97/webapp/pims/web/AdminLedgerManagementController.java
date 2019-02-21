package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.service.IssueItemsService;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.service.LedgerRefUserService;
import net.formula97.webapp.pims.service.StatusMasterService;
import net.formula97.webapp.pims.web.forms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by f97one on 17/01/14.
 */
@Controller
@RequestMapping("admin/ledgerManagement")
public class AdminLedgerManagementController extends BaseWebController {

    @Autowired
    IssueLedgerService issueLedgerSvc;
    @Autowired
    IssueItemsService issueItemsSvc;
    @Autowired
    LedgerRefUserService ledgerRefUserSvc;
    @Autowired
    private StatusMasterService statusMasterSvc;

    public AdminLedgerManagementController(StatusMasterService statusMasterSvc) {
        this.statusMasterSvc = statusMasterSvc;
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchLedger(Model model, HeaderForm headerForm, LedgerSearchConditionForm conditionForm) {
        Users myUserDetail = getUserState(model, headerForm);

        // ステータスと検索条件を張りなおす
        Map<Integer, String> statusMap = statusMasterSvc.getStatusMap();
        model.addAttribute("statusMap", statusMap);
        model.addAttribute("ledgerSearchConditionForm", conditionForm);

        List<IssueLedger> ledgerList = issueLedgerSvc.getLedgerByList(conditionForm);
        model.addAttribute("matchLedgerCount", ledgerList.size());
        model.addAttribute("ledgerList", ledgerList);

        model.addAttribute("searchExecuted", true);

        return "/admin/ledger_list";
    }

    @RequestMapping(value = "{ledgerId}", method = RequestMethod.GET)
    public String configureLedger(@PathVariable Integer ledgerId, Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        IssueLedger ledger = issueLedgerSvc.getLedgerById(ledgerId);
        LedgerDetailForm frm = new LedgerDetailForm();

        if (ledger == null) {
            // 台帳が見つからない時はエラー
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

            frm.importLedger(new IssueLedger());

        } else {
            frm.importLedger(ledger);

            ArrayList<RefUserItem> refUserList = (ArrayList<RefUserItem>) ledgerRefUserSvc.getReferenceConditionById(ledgerId);
            frm.setRefUserItemList(refUserList);

            model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);
        }

        model.addAttribute("ledgerDetailForm", frm);

        return "/admin/ledger_detail";
    }

    @RequestMapping(value = "ledgerDetail/{ledgerId}", method = RequestMethod.POST, params = "updateItemBtn")
    public String updateLedgerSummary(
            @PathVariable Integer ledgerId, LedgerDetailForm detailForm, BindingResult result, Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        IssueLedger issueLedger = issueLedgerSvc.getLedgerById(ledgerId);
        IssueLedger formLedger = detailForm.exportLedger();
        if (issueLedger == null || !ledgerId.equals(formLedger.getLedgerId())) {
            // 台帳が見つからない時、またはformとURLでIDが一致しないときはエラー
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

            model.addAttribute("ledgerDetailForm", detailForm);

        } else if (result.hasErrors()) {
            // バリデーションエラーの場合
            model.addAttribute("ledgerDetailForm", detailForm);

        } else {
            issueLedgerSvc.saveLedger(formLedger);

            putInfoMsg(model, "台帳を更新しました。");

            model.addAttribute("ledgerDetailForm", detailForm);
        }

        return "/admin/ledger_detail";
    }

    @RequestMapping(value = "ledgerDetail/{ledgerId}", method = RequestMethod.POST, params = "registerRefUserBtn")
    public String updateParticipants(@PathVariable Integer ledgerId, LedgerDetailForm detailForm, BindingResult result,
                                     Model model, HeaderForm headerForm) {

        Users myUserDetail = getUserState(model, headerForm);

        IssueLedger issueLedger = issueLedgerSvc.getLedgerById(ledgerId);

        if (issueLedger == null || !ledgerId.equals(detailForm.getLedgerId())) {
            // 台帳が見つからない時、またはformとURLでIDが一致しないときはエラー
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

            model.addAttribute("ledgerDetailForm", detailForm);
        } else if (result.hasErrors()) {
            // バリデーションエラーの場合
            System.out.println("バリデーションエラーに突入");
            model.addAttribute("ledgerDetailForm", detailForm);
        } else {
            System.out.println("通常処理に突入");

            List<LedgerRefUser> updateParticipants = new ArrayList<>();
            List<LedgerRefUser> removeParticipants = new ArrayList<>();

            for (RefUserItem refUserItem : detailForm.getRefUserItemList()) {
                LedgerRefUser refUser = refUserItem.exportItem(ledgerId);
                // 更新用と削除用に分別する
                if (refUser == null) {
                    removeParticipants.add(new LedgerRefUser(ledgerId, refUserItem.getUserId()));
                } else {
                    updateParticipants.add(refUser);
                }
            }

            ledgerRefUserSvc.refreshParticipants(updateParticipants, removeParticipants);

            putInfoMsg(model, "台帳参加ユーザーを更新しました。");

            model.addAttribute("ledgerDetailForm", detailForm);
        }

        return "/admin/ledger_detail";
    }

    @RequestMapping(value = "ledgerDetail/remove/{ledgerId}", method = RequestMethod.GET)
    public String confirmToRemoveLedger(@PathVariable Integer ledgerId, Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        IssueLedger issueLedger = issueLedgerSvc.getLedgerById(ledgerId);

        LedgerRemovalDetailForm frm = new LedgerRemovalDetailForm();
        if (issueLedger == null) {
            // 台帳が見つからない時、またはformとURLでIDが一致しないときはエラー
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

        } else {
            frm.importLedger(issueLedger);

            List<IssueItemsLineForm> issueItems = issueItemsSvc.getIssueItemsForDisplay(ledgerId);
            frm.setIssueItems(issueItems);
        }

        model.addAttribute("ledgerRemovalDetailForm", frm);

        return "/admin/ledger_removal";
    }

    @RequestMapping(value = "ledgerDetail/remove/{ledgerId}", method = RequestMethod.POST, params = "removeBtn")
    public String removeLedgersById(@PathVariable Integer ledgerId, Model model, HeaderForm headerForm, RedirectAttributes redirectAttributes) {
        Users myUserDetail = getUserState(model, headerForm);

        try {
            IssueLedger ledger = issueLedgerSvc.getLedgerById(ledgerId);

            issueLedgerSvc.removeLedgerById(ledgerId);

            String info = String.format(Locale.getDefault(), "台帳 %s を削除しました。", ledger.getLedgerName());
            redirectAttributes.addFlashAttribute("infoMsg", info);
        } catch (Exception e) {
            String msg = String.format(Locale.getDefault(), "LedgerID %d was not found on this system.", ledgerId);
            if (e instanceof IllegalArgumentException && msg.equals(e.getMessage())) {
                redirectAttributes.addFlashAttribute("errMsg", "指定された台帳は見つかりません。");
            } else {
                e.printStackTrace();
                throw e;
            }
        }

        return "redirect:/admin/ledgerManagement";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String showLedgerEditorFormAsAdd(Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        NewLedgerForm newLedgerForm = new NewLedgerForm();
        newLedgerForm.setPublicLedger(false);

        model.addAttribute("newLedgerForm", newLedgerForm);

        return "/admin/ledger_add";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST, params = "addLedgerBtn")
    public String createNewLedger(@Validated NewLedgerForm newLedgerForm, BindingResult result, Model model, HeaderForm headerForm, RedirectAttributes redirectAttributes) {
        Users myUserDetail = getUserState(model, headerForm);

        if (result.hasErrors()) {
            model.addAttribute("newLedgerForm", newLedgerForm);
            return "/admin/ledger_add";
        } else {
            IssueLedger ledger = new IssueLedger();
            ledger.setLedgerName(newLedgerForm.getLedgerName());
            ledger.setOpenStatus(newLedgerForm.getOpenStatus());
            ledger.setPublicLedger(newLedgerForm.isPublicLedger());

            issueLedgerSvc.saveLedger(ledger);

            String msg = String.format(Locale.getDefault(), "台帳 %s を追加しました。", newLedgerForm.getLedgerName());
            redirectAttributes.addFlashAttribute("infoMsg", msg);
            return "redirect:/admin/ledgerManagement";
        }
    }
}
