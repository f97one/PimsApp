package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * ユーザー管理画面での検索条件で使うForm。<br />
 * Created by f97one on 2016/12/16.
 */
@Data
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSearchConditionForm implements Serializable {

    /**
     * シリアライズUID
     */
    private static final long serialVersionUID = -1396281996738099495L;

    /**
     * 検索キーワード：ユーザー名
     */
    @Size(max = 32)
    private String username;

    /**
     * 検索キーワード：表示名
     */
    @Size(max = 128)
    private String displayName;

    /**
     * 検索キーワード：メールアドレス
     */
    @Size(max = 128)
    private String mailAddress;

    /**
     * 検索キーワード：有効なユーザーのみに限定
     */
    private Boolean limitEnabledUser;

    public UserSearchConditionForm() {
        this.username = null;
        this.displayName = null;
        this.mailAddress = null;
        this.limitEnabledUser = false;
    }
}
