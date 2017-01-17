package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.LedgerSearchConditionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by f97one on 17/01/14.
 */
@Controller
@RequestMapping("admin/ledgerManagement")
public class AdminLedgerManagementController extends BaseWebController {

    @Autowired
    IssueLedgerService issueLedgerSvc;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchLedger(Model model, HeaderForm headerForm, LedgerSearchConditionForm conditionForm) {
        Users myUserDetail = getUserState(model, headerForm);

        List<IssueLedger> ledgerList = issueLedgerSvc.getLedgerByList(conditionForm);
        model.addAttribute("matchLedgerCount", ledgerList.size());
        model.addAttribute("ledgerList", ledgerList);

        return "/admin/ledger_list";
    }
}
