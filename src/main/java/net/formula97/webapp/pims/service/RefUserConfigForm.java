package net.formula97.webapp.pims.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.web.forms.RefUserItem;

import java.util.List;

/**
 * Created by f97one on 17/01/22.
 */
@Data
@NoArgsConstructor
public class RefUserConfigForm {

    private List<RefUserItem> refUserList;
}
