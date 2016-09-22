/**
 * 
 */
package net.formula97.webapp.pims.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.formula97.webapp.pims.domain.IssueLedger;

/**
 * @author f97one
 *
 */
@RestController
@RequestMapping("api/ledger")
public class LedgerRestController {

    List<IssueLedger> getLedger() {
        
        return null;
    }
    
}
