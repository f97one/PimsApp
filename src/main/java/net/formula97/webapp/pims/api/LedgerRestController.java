/**
 * 
 */
package net.formula97.webapp.pims.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.service.IssueLedgerService;

/**
 * @author f97one
 *
 */
@RestController
@RequestMapping("api/ledger")
public class LedgerRestController {
    
    @Autowired
    IssueLedgerService issueLedgerSvc;

    @RequestMapping(method = RequestMethod.GET)
    List<IssueLedger> getPublicLedger() {
        return issueLedgerSvc.getPublicLedgers();
    }
    
}
