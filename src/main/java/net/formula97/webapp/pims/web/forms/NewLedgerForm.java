package net.formula97.webapp.pims.web.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by f97one on 2016/10/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewLedgerForm {

    @NotNull
    @Size(min = 1, max = 64)
    private String ledgerName;

    private Integer openStatus;

    private boolean publicLedger;
}
