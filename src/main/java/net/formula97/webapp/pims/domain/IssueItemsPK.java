/**
 * 
 */
package net.formula97.webapp.pims.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;


/**
 * @author f97one
 *
 */
@Embeddable
public class IssueItemsPK implements Serializable {

    public static final String COLUMN_LEDGER_ID = "LEDGER_ID";
    public static final String COLUMN_ISSUE_ID = "ISSUE_ID";

    /**
     * Serialized UID
     */
    private static final long serialVersionUID = 7050763019022228894L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ManyToOne(optional = false)
    @Column(name = COLUMN_LEDGER_ID)
    private Integer ledgerId;

    @ManyToOne(optional = false)
    @Column(name = COLUMN_ISSUE_ID)
    private Integer issueId;

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
     * @return the issueId
     */
    public Integer getIssueId() {
        return issueId;
    }

    /**
     * @param issueId the issueId to set
     */
    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

}
