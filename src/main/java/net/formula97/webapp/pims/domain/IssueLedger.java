/**
 * 
 */
package net.formula97.webapp.pims.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueLedger implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5023932913460318248L;

    public static final String TABLE_NAME = "ISSUE_LEDGER";

    public static final String COLUMN_LEDGER_ID = "LEDGER_ID";
    public static final String COLUMN_LEDGER_NAME = "LEDGER_NAME";
    public static final String COLUMN_OPEN_STATUS_ID = "OPEN_STATUS_ID";
    public static final String COLUMN_PUBLIC_LEDGER = "PUBLIC_LEDGER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_LEDGER_ID)
    private Integer ledgerId;

    @Column(name = COLUMN_LEDGER_NAME, length = 64)
    private String ledgerName;

    @Column(name = COLUMN_OPEN_STATUS_ID)
    private Integer openStatus;
    
    @Column(name = COLUMN_PUBLIC_LEDGER)
    private Boolean publicLedger;
}
