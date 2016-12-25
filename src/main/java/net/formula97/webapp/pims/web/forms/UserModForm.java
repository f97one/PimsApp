package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

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
    @Pattern(regexp = "^[\\p{Alnum}]$")
    private String username;

    /**
     * 変更前のパスワード
     */
    @Size(min = 0, max = 32)
    private String orgPassword;

    /**
     * 変更するパスワード
     */
    @Size(min = 0, max = 32)
    private String password;

    /**
     * 変更するパスワード（照合用）
     */
    @Size(min = 0, max = 32)
    private String passwordConfirm;

    /**
     * 表示名
     */
    @Size(min = 0, max = 128)
    private String displayName;

    private Boolean enableUser;

    /**
     * ユーザー権限（USER、またはADMIN）
     */
    @Pattern(regexp = "^[UA]$")
    private String assignedRole;

    /**
     * ユーザーIDをDisabledにしていると、中身はnullになるため、検索用のバックアップとして使用するユーザーID
     */
    private String searchUsername;

    /**
     * 検索用ユーザーIDをセットする。
     *
     * @param username 検索用ユーザーID
     */
    private void serSearchUsername(String username) {
        this.username = username;
        this.searchUsername = username;
    }
}
