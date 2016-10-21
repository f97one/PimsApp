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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public String createTitle(Model model, HeaderForm form) {
        // ログイン中ユーザーを取得
        Users users = getUserState(model, form);

        // タイトル
	    String title = sysConfSvc.getConfig(AppConstants.SysConfig.APP_TITLE);
	    model.addAttribute("title", title);
	    
	    // 台帳一覧
	    List<IssueLedger> issueLeddgerList;
	    if (users == null) {
            issueLeddgerList = issueLedgerSvc.getPublicLedgers();
	    } else {
            issueLeddgerList = issueLedgerSvc.getLedgersForUser(users.getUsername());
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
