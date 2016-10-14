/**
 * 
 */
package net.formula97.webapp.pims.web.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author f97one
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispLedgerForm {
    
    private Integer ledgerId;
    private String ledgerName;
    private String status;
    private Date lastUpdatedAt;
}
