package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by f97one on 2016/10/20.
 */
@Data
@NoArgsConstructor
public class UserConfigForm implements Serializable {

    private static final long serialVersionUID = -4230871371120474803L;

    @NotNull
    @Length(min = 1, max = 32)
    private String username;

    @NotNull
    @Length(min = 1, max = 32)
    private String orgPassword;

    @NotNull
    @Length(min = 1, max = 32)
    private String password;

    @NotNull
    @Length(min = 1, max = 32)
    private String passwordConfirm;

    @NotNull
    @Length(min = 0, max = 128)
    private String displayName;

    private Boolean enableUser;

    private String assignedRole;

    private boolean disableUsernameModify;

    private boolean disableDisplayNameModify;

    private boolean disableUserEnableModify;

    private boolean disableRoleModify;

    public boolean isPasswdMatches() {
        if (password == null && passwordConfirm == null) {
            return true;
        } else {
            if (password == null || passwordConfirm == null) {
                return false;
            } else {
                return password.equals(passwordConfirm);
            }
        }
    }
}
