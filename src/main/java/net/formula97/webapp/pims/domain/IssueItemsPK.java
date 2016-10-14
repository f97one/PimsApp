/**
 * 
 */
package net.formula97.webapp.pims.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


/**
 * @author f97one
 *
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueItemsPK implements Serializable {

    public static final String COLUMN_LEDGER_ID = "LEDGER_ID";
    public static final String COLUMN_ISSUE_ID = "ISSUE_ID";

    /**
     * Serialized UID
     */
    private static final long serialVersionUID = 7050763019022228894L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_LEDGER_ID)
    private Integer ledgerId;

    @Column(name = COLUMN_ISSUE_ID)
    private Integer issueId;
}
