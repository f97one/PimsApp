package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.domain.IssueLedger;

import java.util.ArrayList;

/**
 * Created by f97one on 17/01/28.
 */
@Data
@NoArgsConstructor
public class LedgerDetailForm {

    private Integer ledgerId;

    private String ledgerName;

    private Integer openStatus;

    private Boolean publicLedger;

    private ArrayList<RefUserItem> refUserItemList;

    public ArrayList<RefUserItem> getRefUserItemList() {
        if (this.refUserItemList == null) {
            this.refUserItemList = new ArrayList<>();
        }

        return this.refUserItemList;
    }

    public IssueLedger exportLedger() {
        IssueLedger ledger = new IssueLedger();

        ledger.setLedgerId(this.ledgerId);
        ledger.setLedgerName(this.ledgerName);
        ledger.setOpenStatus(this.openStatus);
        ledger.setPublicLedger(this.publicLedger);

        return ledger;
    }

    public void importLedger(IssueLedger ledger) {
        this.ledgerId = ledger.getLedgerId();
        this.ledgerName = ledger.getLedgerName();
        this.publicLedger = ledger.getPublicLedger();
        this.openStatus = ledger.getOpenStatus();
    }
}
