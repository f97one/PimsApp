/**
 * 
 */
package net.formula97.webapp.pims.misc;

/**
 * @author f97one
 *
 */
public class AppConstants {

    public class SysConfig {
        public static final String APP_TITLE = "AppTitle";
    }

    /**
     * ユーザー権限：一般ユーザー
     */
    public static final String ROLE_USER = "USER";

    /**
     * ユーザー権限：管理者ユーザー
     */
    public static final String ROLE_ADMIN = "ADMIN";

    public static final String EDIT_MODE_ADD = "A";
    public static final String EDIT_MODE_MODIFY = "M";
    public static final String EDIT_MODE_REMOVE = "D";
    public static final String EDIT_MODE_READONLY = "R";

    public static final String STD_DATE_FORMAT = "yyyy/MM/dd";

    /**
     * ドロップダウンリストで何も選択していないときの値
     */
    public static final int SELECTION_NOT_SELECTED = -1;

    public static final String ROLE_CODE_ADMIN = "A";
    public static final String ROLE_CODE_USER = "U";

    /**
     * プリフィックス付きのユーザー権限：管理者ユーザー
     */
    public static final String CANONICAL_ROLE_ADMIN = "ROLE_ADMIN";
    /**
     * プリフィックス付きのユーザー権限：一般ユーザー
     */
    public static final String CANONICAL_ROLE_USER = "ROLE_USER";

    /**
     * 台帳ステータス：公開
     */
    public static final int LEDGER_OPEN = 1;
    /**
     * 台帳ステータス：ブロック中
     */
    public static final int LEDGER_BLOCKING = 2;
    /**
     * 台帳ステータス：終了
     */
    public static final int LEDGER_CLOSED = 3;
}
