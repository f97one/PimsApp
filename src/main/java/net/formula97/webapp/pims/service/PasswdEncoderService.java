/**
 * 
 */
package net.formula97.webapp.pims.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * パスワード生成に関するユーティリティクラス。
 * 
 * @author f97one
 *
 */
public class PasswdEncoderService {

    public static String getEncodedPasswd(String rawPasswd) {
        return new BCryptPasswordEncoder().encode(rawPasswd);
    }

    public static boolean isEquals(String rawPasswd, String encodedPasswd) {
        return new BCryptPasswordEncoder().matches(rawPasswd, encodedPasswd);
    }
}
