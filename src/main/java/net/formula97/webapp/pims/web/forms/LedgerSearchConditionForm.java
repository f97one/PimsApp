package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by f97one on 17/01/14.
 */
@Data
@NoArgsConstructor
@Component
public class LedgerSearchConditionForm implements Serializable {


    private static final long serialVersionUID = -1973971996984121526L;

    /**
     * 台帳名
     */
    @Size(max = 64)
    private String ledgerName;
    /**
     * 台帳ステータス選択リスト
     */
    private List<Integer> ledgerStatus;

    public List<Integer> getLedgerStatus() {
        if (this.ledgerStatus == null) {
            this.ledgerStatus = Collections.emptyList();
        }
        return this.ledgerStatus;
    }
}
