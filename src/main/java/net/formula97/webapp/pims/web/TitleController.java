/**
 * 
 */
package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.service.StatusMasterService;
import net.formula97.webapp.pims.web.forms.DispLedgerForm;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * @author f97one
 *
 */
@Controller
@RequestMapping("")
public class TitleController extends BaseWebController {

	private final IssueLedgerService issueLedgerSvc;
	private final StatusMasterService statusMasterSvc;

	@Autowired
	public TitleController(IssueLedgerService issueLedgerSvc, StatusMasterService statusMasterSvc) {
		this.issueLedgerSvc = issueLedgerSvc;
		this.statusMasterSvc = statusMasterSvc;
	}

	@RequestMapping(method = RequestMethod.GET)
    public String createTitle(Model model, HeaderForm form) {
        // ログイン中ユーザーを取得
        Users users = getUserState(model, form);

        // タイトル
	    String title = sysConfSvc.getConfig(AppConstants.SysConfig.APP_TITLE);
	    model.addAttribute("title", title);
	    
	    // 台帳一覧
		List<IssueLedger> issueLedgerList;
	    if (users == null) {
			issueLedgerList = issueLedgerSvc.getPublicLedgers();
	    } else {
			issueLedgerList = issueLedgerSvc.getLedgersForUser(users.getUsername());
	    }

		Map<Integer, String> smMap = statusMasterSvc.getOpenStatus();

	    List<DispLedgerForm> frm = new ArrayList<>();
		for (IssueLedger l : issueLedgerList) {
	        DispLedgerForm f = new DispLedgerForm();
	        f.setLedgerId(l.getLedgerId());
	        f.setLedgerName(l.getLedgerName());
	        f.setStatus(smMap.get(l.getOpenStatus()));
	        f.setLastUpdatedAt(new Date());
	        
	        frm.add(f);
	    }
	    
	    model.addAttribute("dispIssueLedgers", frm);

		return "/title";
	}
	
	
}
