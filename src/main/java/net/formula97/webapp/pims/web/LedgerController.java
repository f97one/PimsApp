/**
 * 
 */
package net.formula97.webapp.pims.web;

import java.security.Principal;
import java.util.List;

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

import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.IssueLedgerService;

/**
 * @author f97one
 *
 */
@Controller
@RequestMapping("ledger")
public class LedgerController extends BaseWebController {

    @Autowired
    IssueLedgerService issueLedgerSvc;

    @ModelAttribute
    NewLedgerForm setUpNewLedgerForm() {
        return new NewLedgerForm();
    }
    
    @RequestMapping(name = "{ledgerId}", method = RequestMethod.GET)
    public List<IssueItems> getLedgerItems(@PathVariable Integer ledgerId, Model model, Principal principal) {
        Users users = getUserState(model, principal);
        
        if (users == null) {
            
        } else {
            
        }


        return null;
    }

    @RequestMapping(value = "create", params = "addLedgerBtn", method = RequestMethod.POST)
    public String addLedger(@Validated NewLedgerForm form, BindingResult result, Model model, Principal principal) {
        Users users = getUserState(model, principal);

        if (result.hasErrors()) {

        }

        if (users == null) {
            
        } else {
            
        }
        
        //return "redirect:/title";s
        return null;
    }
}
