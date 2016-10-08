/**
 * 
 */
package net.formula97.webapp.pims.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author f97one
 *
 */
@Entity
@Table(name = IssueLedger.TABLE_NAME)
public class IssueLedger implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5023932913460318248L;

    public static final String TABLE_NAME = "ISSUE_LEDGER";

    public static final String COLUMN_LEDGER_ID = "LEDGER_ID";
    public static final String COLUMN_LEDGER_NAME = "LEDGER_NAME";
    public static final String COLUMN_OPEN_STATUS_ID = "OPEN_STATUS_ID";
    public static final String COLUMN_IS_PUBLIC = "IS_PUBLIC";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_LEDGER_ID)
    private Integer ledgerId;

    @Column(name = COLUMN_LEDGER_NAME, length = 64)
    private String ledgerName;

    @Column(name = COLUMN_OPEN_STATUS_ID)
    private Integer openStatus;
    
    @Column(name = COLUMN_IS_PUBLIC)
    private Boolean isPublic;

    public IssueLedger() {
        this.isPublic = false;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * @return the ledgerId
     */
    public Integer getLedgerId() {
        return ledgerId;
    }

    /**
     * @param ledgerId
     *            the ledgerId to set
     */
    public void setLedgerId(Integer ledgerId) {
        this.ledgerId = ledgerId;
    }

    /**
     * @return the ledgerName
     */
    public String getLedgerName() {
        return ledgerName;
    }

    /**
     * @param ledgerName
     *            the ledgerName to set
     */
    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    /**
     * @return the openStatus
     */
    public Integer getOpenStatus() {
        return openStatus;
    }

    /**
     * @param openStatus
     *            the openStatus to set
     */
    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isPublic == null) ? 0 : isPublic.hashCode());
        result = prime * result + ((ledgerId == null) ? 0 : ledgerId.hashCode());
        result = prime * result + ((ledgerName == null) ? 0 : ledgerName.hashCode());
        result = prime * result + ((openStatus == null) ? 0 : openStatus.hashCode());
        return result;
    }

    /**
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IssueLedger other = (IssueLedger) obj;
        if (isPublic == null) {
            if (other.isPublic != null)
                return false;
        } else if (!isPublic.equals(other.isPublic))
            return false;
        if (ledgerId == null) {
            if (other.ledgerId != null)
                return false;
        } else if (!ledgerId.equals(other.ledgerId))
            return false;
        if (ledgerName == null) {
            if (other.ledgerName != null)
                return false;
        } else if (!ledgerName.equals(other.ledgerName))
            return false;
        if (openStatus == null) {
            if (other.openStatus != null)
                return false;
        } else if (!openStatus.equals(other.openStatus))
            return false;
        return true;
    }
}
