package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.misc.annotations.ConfigKey;

/**
 * Created by f97one on 17/04/20.
 */
@Data
@NoArgsConstructor
public class SystemPreferenceForm implements PreferenceForm {
    /**
     * アプリ名称
     */
    @ConfigKey("AppTitle")
    private String appName;
}
