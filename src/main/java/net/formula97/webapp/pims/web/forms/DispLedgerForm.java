/**
 * 
 */
package net.formula97.webapp.pims.web.forms;

import java.util.Date;

/**
 * @author f97one
 *
 */
public class DispLedgerForm {
    
    private Integer ledgerId;
    private String ledgerName;
    private String status;
    private Date lastUpdatedAt;
    
    /**
     * @return the ledgerName
     */
    public String getLedgerName() {
        return ledgerName;
    }
    /**
     * @param ledgerName the ledgerName to set
     */
    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return the lastUpdatedAt
     */
    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    /**
     * @param lastUpdatedAt the lastUpdatedAt to set
     */
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    public Integer getLedgerId() {
        return ledgerId;
    }
    public void setLedgerId(Integer ledgerId) {
        this.ledgerId = ledgerId;
    }

}
