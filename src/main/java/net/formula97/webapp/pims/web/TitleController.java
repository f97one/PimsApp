/**
 * 
 */
package net.formula97.webapp.pims.web;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.service.AuthorizedUsers;
import net.formula97.webapp.pims.service.IssueLedgerService;

/**
 * @author f97one
 *
 */
@Controller
@RequestMapping("")
public class TitleController extends BaseWebController {
    
    @Autowired
    IssueLedgerService issueLedgerSvc;

	@RequestMapping(method = RequestMethod.GET)
	public String createTitle(Model model, Principal principal) {
	    
	    // タイトル
	    String title = sysConfSvc.getConfig(AppConstants.SysConfig.APP_TITLE);
	    model.addAttribute("title", title);
	    
	    // 台帳一覧
	    List<IssueLedger> issueLeddgerList;
	    if (principal == null) {
	        issueLeddgerList = issueLedgerSvc.getPublicLedgers();
	    } else {
	        Authentication auth = (Authentication) principal;
	        AuthorizedUsers authUsers = (AuthorizedUsers) auth.getPrincipal();
	        
	        issueLeddgerList = issueLedgerSvc.getLedgersForUser(authUsers.getUsers().getUserId());
	    }
	    model.addAttribute("issueLedgers", issueLeddgerList);
	    
		return "title";
	}
}
