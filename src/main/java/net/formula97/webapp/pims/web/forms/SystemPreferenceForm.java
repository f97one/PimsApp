package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.misc.annotations.ConfigKey;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    @NotNull
    @Size(min = 1, max = 32)
    private String appTitle;
}
