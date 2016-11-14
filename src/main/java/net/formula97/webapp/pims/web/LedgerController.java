/**
 * 
 */
package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.IssueItemsService;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.service.LedgerRefUserService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.IssueItemsLineForm;
import net.formula97.webapp.pims.web.forms.NewLedgerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author f97one
 *
 */
@Controller
@RequestMapping("ledger")
public class LedgerController extends BaseWebController {

    @Autowired
    IssueLedgerService issueLedgerSvc;
    @Autowired
    IssueItemsService issueItemsSvc;
    @Autowired
    LedgerRefUserService ledgerRefUserSvc;

    @ModelAttribute
    NewLedgerForm setUpNewLedgerForm() {
        return new NewLedgerForm();
    }

    @RequestMapping(value = "{ledgerId}", method = RequestMethod.GET)
    public String getLedgerItems(@PathVariable Integer ledgerId, Model model, HeaderForm form) {
        Users users = getUserState(model, form);

        IssueLedger ledger = issueLedgerSvc.getLedgerById(ledgerId);

        List<IssueItemsLineForm> itemForms = new ArrayList<>();
        long incompleteItems = 0;
        long completeItems = 0;
        long totalItems = 0;

        if (ledger == null) {
            putErrMsg(model, "台帳が見つかりません。");
            ledger = new IssueLedger(-1, "", 1, true);
        } else {
            if (!ledger.getPublicLedger()) {
                boolean badDisp = false;
                if (users == null) {
                    badDisp = true;
                } else {
                    LedgerRefUser refUser = ledgerRefUserSvc.findReferenceForUser(users.getUsername(), ledger.getLedgerId());
                    if (refUser == null) {
                        badDisp = true;
                    }
                }

                if (badDisp) {
                    putErrMsg(model, "この台帳は公開されていません。");
                    ledger = new IssueLedger(-1, "", 1, true);
                } else {
                    List<IssueItems> issueItems = issueItemsSvc.getIssueItemsByLedgerId(ledgerId);
                    if (issueItems.size() > 0) {
                        itemForms = issueItemsSvc.getIssueItemsForDisplay(ledgerId);

                        // 完了、未完了の数
                        totalItems = itemForms.size();
                        incompleteItems = itemForms.stream().filter((r) -> r.getConfirmedDate() == null).count();
                        completeItems = totalItems - incompleteItems;
                    }
                }
            } else {
                List<IssueItems> issueItems = issueItemsSvc.getIssueItemsByLedgerId(ledgerId);
                if (issueItems.size() > 0) {
                    itemForms = issueItemsSvc.getIssueItemsForDisplay(ledgerId);

                    // 完了、未完了の数
                    totalItems = itemForms.size();
                    incompleteItems = itemForms.stream().filter((r) -> r.getConfirmedDate() == null).count();
                    completeItems = totalItems - incompleteItems;
                }
            }
        }

        model.addAttribute("issueLedger", ledger);
        model.addAttribute("issueItems", itemForms);
        model.addAttribute("incompleteItems", incompleteItems);
        model.addAttribute("completeItems", completeItems);
        model.addAttribute("totalItems", totalItems);

        return "/ledger/issueList";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String showLedger(NewLedgerForm form, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);
        model.addAttribute("newLedgerForm", form);

        return "/ledger/addLedger";
    }

    @RequestMapping(value = "create", params = "addLedgerBtn", method = RequestMethod.POST)
    public String addLedger(@Validated NewLedgerForm form, BindingResult result, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        String dest = null;
        if (result.hasErrors()) {
            return showLedger(form, model, headerForm);
        }

        if (users == null) {
            putErrMsg(model, "台帳の追加にはログインが必要です。");
            dest = "/ledger/addLedger";
        } else {
            IssueLedger ledger = new IssueLedger();
            ledger.setLedgerName(form.getLedgerName());
            ledger.setOpenStatus(1);
            ledger.setPublicLedger(form.isPublicLedger());

            issueLedgerSvc.saveLedger(ledger, users);

            dest = "redirect:/";
        }

        return dest;
    }
}
