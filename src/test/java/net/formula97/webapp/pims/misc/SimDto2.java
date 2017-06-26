package net.formula97.webapp.pims.misc;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.misc.annotations.ConfigKey;
import net.formula97.webapp.pims.web.forms.PreferenceForm;

/**
 * Created by f97one on 2017/06/25.
 */
@Data
@NoArgsConstructor
public class SimDto2 implements PreferenceForm {

    @ConfigKey("Member1")
    private String member1;

    @ConfigKey("Member1")
    private String member2;
}
