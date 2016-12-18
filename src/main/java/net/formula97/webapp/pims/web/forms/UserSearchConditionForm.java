package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;

/**
 * ユーザー管理画面での検索条件で使うForm。<br />
 * Created by f97one on 2016/12/16.
 */
@Data
@NoArgsConstructor
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSearchConditionForm implements Serializable {

    /**
     * シリアライズUID
     */
    private static final long serialVersionUID = -1396281996738099495L;
}
