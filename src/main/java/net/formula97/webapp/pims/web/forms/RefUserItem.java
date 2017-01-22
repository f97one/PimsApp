package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.domain.LedgerRefUser;

/**
 * Created by f97one on 17/01/21.
 */
@Data
@NoArgsConstructor
public class RefUserItem  {

    /**
     * ユーザーの所属フラグ。<br />
     * 所属している場合true、そうでないときfalse
     */
    private Boolean userJoined;
    /**
     * 所属ユーザーのユーザーID
     */
    private String userId;
    /**
     * 所属ユーザーの表示名
     */
    private String displayName;

    public LedgerRefUser exportItem(int ledgerId) {
        return this.userJoined ? new LedgerRefUser(ledgerId, userId) : null;
    }
}
