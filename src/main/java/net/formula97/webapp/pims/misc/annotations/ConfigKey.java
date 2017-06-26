package net.formula97.webapp.pims.misc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * システム設定テーブルのキー名称と表示アイテムのバインドを決めるアノテーション。<br />
 * Created by f97one on 2017/06/25.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigKey {
    /**
     * システム設定テーブルにマップするときのキー名称
     *
     * @return バインドするキー名称、設定なしの時は空文字を仮定
     */
    String value() default "";
}
