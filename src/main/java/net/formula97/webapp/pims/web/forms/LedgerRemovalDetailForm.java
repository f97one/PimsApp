package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.domain.IssueLedger;

import java.util.ArrayList;
import java.util.List;

@Data
public class LedgerRemovalDetailForm {

    // 基本情報部分
    /**
     * 台帳ID
     */
    private Integer ledgerId;
    /**
     * 台帳名
     */
    private String ledgerName;
    /**
     * 公開台帳フラグ
     */
    private Boolean publicLedger;

    /**
     * 課題一覧
     */
    private List<IssueItemsLineForm> issueItems;

    public LedgerRemovalDetailForm() {
        this.ledgerId = null;
        this.ledgerName = "";
        this.publicLedger = false;
        issueItems = new ArrayList<>(1);
    }

    public void importLedger(IssueLedger ledger) {
        this.ledgerId = ledger.getLedgerId();
        this.ledgerName = ledger.getLedgerName();
        this.publicLedger = ledger.getPublicLedger();
    }
}
