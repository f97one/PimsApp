package net.formula97.webapp.pims.web.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by f97one on 2016/10/10.
 */
public class NewLedgerForm {

    @NotNull
    @Size(min = 1, max = 64)
    private String ledgerName;

    private Integer openStatus;

    private boolean isPublic;

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public Integer getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(Integer openStatus) {
        this.openStatus = openStatus;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
