/**
 * 
 */
package net.formula97.webapp.pims.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.service.AuthorizedUsers;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.service.StatusMasterService;
import net.formula97.webapp.pims.web.forms.DispLedgerForm;

/**
 * @author f97one
 *
 */
@Controller
@RequestMapping("")
public class TitleController extends BaseWebController {
    
    @Autowired
    IssueLedgerService issueLedgerSvc;
    @Autowired
    StatusMasterService statusMasterSvc;
    
	@RequestMapping(method = RequestMethod.GET)
	public String createTitle(Model model, Principal principal) {
	    
	    // タイトル
	    String title = sysConfSvc.getConfig(AppConstants.SysConfig.APP_TITLE);
	    model.addAttribute("title", title);
	    
	    // 台帳一覧
	    List<IssueLedger> issueLeddgerList;
	    if (principal instanceof UserDetails) {
            Authentication auth = (Authentication) principal;
            AuthorizedUsers authUsers = (AuthorizedUsers) auth.getPrincipal();
            model.addAttribute("displayName", authUsers.getUsers().getDisplayName());
            
            issueLeddgerList = issueLedgerSvc.getLedgersForUser(authUsers.getUsers().getUserId());
	    } else {
            issueLeddgerList = issueLedgerSvc.getPublicLedgers();
	    }
	    
	    Map<Integer, String> smMap = statusMasterSvc.getStatusMap();
	    List<DispLedgerForm> frm = new ArrayList<>();
	    for (IssueLedger l : issueLeddgerList) {
	        DispLedgerForm f = new DispLedgerForm();
	        f.setLedgerId(l.getLedgerId());
	        f.setLedgerName(l.getLedgerName());
	        f.setStatus(smMap.get(l.getOpenStatus()));
	        f.setLastUpdatedAt(new Date());
	        
	        frm.add(f);
	    }
	    
	    model.addAttribute("dispIssueLedgers", frm);
	    
		return "title";
	}
	
	
}
