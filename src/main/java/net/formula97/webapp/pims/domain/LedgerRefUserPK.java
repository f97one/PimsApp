/**
 * 
 */
package net.formula97.webapp.pims.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author f97one
 *
 */
@Embeddable
public class LedgerRefUserPK implements Serializable {

    public static final String COLUMN_LEDGER_ID = "LEDGER_ID";
    public static final String COLUMN_USER_ID = "USER_ID";

    /**
     * 
     */
    private static final long serialVersionUID = -8598787961632263081L;

    @Column(name = COLUMN_LEDGER_ID)
    private Integer ledgerId;
    
    @Column(name = COLUMN_USER_ID, length = 32)
    private String userId;

    /**
     * @return the ledgerId
     */
    public Integer getLedgerId() {
        return ledgerId;
    }

    /**
     * @param ledgerId the ledgerId to set
     */
    public void setLedgerId(Integer ledgerId) {
        this.ledgerId = ledgerId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
