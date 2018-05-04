package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by f97one on 2016/12/24.
 */
@Data
@NoArgsConstructor
public class UserModForm {

    /**
     * 編集用ユーザーID
     */
    @NotBlank
    @Size(min = 1, max = 32)
    @Pattern(regexp = "[\\p{Alnum}]*")
    private String username;

    /**
     * 変更前のパスワード
     */
    @Size(max = 32)
    private String orgPassword;

    /**
     * 変更するパスワード
     */
    @Size(max = 32)
    private String password;

    /**
     * 変更するパスワード（照合用）
     */
    @Size(max = 32)
    private String passwordConfirm;

    /**
     * 表示名
     */
    @Size(max = 128)
    private String displayName;

    private Boolean enableUser;

    /**
     * ユーザー権限（USER、またはADMIN）
     */
    @Pattern(regexp = "^[UA]$")
    private String assignedRole;

    /**
     * メールアドレス<br />
     * RFC5322には違反しているものの、メールアドレスの書式は簡易判断としている。
     */
    @Size(max = 128)
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+[@][a-zA-Z0-9.-]+$")
    private String mailAddress;

    /**
     * ユーザーIDをDisabledにしていると、中身はnullになるため、検索用のバックアップとして使用するユーザーID
     */
    private String searchUsername;

    public UserModForm(Users users) {
        setSearchUsername(users.getUsername());

        this.mailAddress = users.getMailAddress();
        this.displayName = users.getDisplayName();
        this.enableUser = users.getEnabled();

        switch (users.getAuthority()) {
            case AppConstants.ROLE_ADMIN:
                this.assignedRole = AppConstants.ROLE_CODE_ADMIN;
                break;
            case AppConstants.ROLE_USER:
                this.assignedRole = AppConstants.ROLE_CODE_USER;
                break;
            default:
                this.assignedRole = "";
                break;
        }

        // パスワード類は空にする
        this.password = "";
        this.passwordConfirm = "";
        this.orgPassword = "";
    }

    /**
     * 検索用ユーザーIDをセットする。
     *
     * @param username 検索用ユーザーID
     */
    private void setSearchUsername(String username) {
        this.username = username;
        this.searchUsername = username;
    }

    public boolean isPasswdMatches() {
        return password == null && passwordConfirm == null || !(password == null || passwordConfirm == null) && password.equals(passwordConfirm);
    }

    public Users createDomain() {
        Users users = new Users();

        users.setUsername(this.username);
        users.setPassword(BCrypt.hashpw(this.password, BCrypt.gensalt()));
        users.setDisplayName(this.displayName);
        users.setEnabled(this.enableUser);
        switch (this.assignedRole) {
            case AppConstants.ROLE_CODE_USER:
                users.setAuthority(AppConstants.ROLE_USER);
                break;
            case AppConstants.ROLE_CODE_ADMIN:
                users.setAuthority(AppConstants.ROLE_ADMIN);
                break;
        }
        users.setMailAddress(this.mailAddress);

        return users;
    }
}
