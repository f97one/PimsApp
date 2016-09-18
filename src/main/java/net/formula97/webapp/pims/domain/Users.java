/**
 * 
 */
package net.formula97.webapp.pims.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ユーザー
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = Users.TABLE_NAME)
public class Users implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8140112793498442361L;

    public static final String TABLE_NAME = "USERS";

    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_ENCODED_PASSWD = "ENCODED_PASSWD";
    public static final String COLUMN_DISPLAY_NAME = "DISPLAY_NAME";
    public static final String COLUMN_LAST_LOGIN_DATE = "LAST_LOGIN_DATE";
    public static final String COLUMN_MAIN_GROUP_ID = "MAIN_GROUP_ID";

    @Id
    @Column(name = COLUMN_USER_ID, length = 32)
    private String userId;

    @Column(name = COLUMN_ENCODED_PASSWD, length = 128)
    private String encodedPasswd;

    @Column(name = COLUMN_DISPLAY_NAME, length = 128)
    private String displayName;

    @Column(name = COLUMN_LAST_LOGIN_DATE)
    private Date lastLoginDate;

    @Column(name = COLUMN_MAIN_GROUP_ID, length = 32)
    private String mainGroupId;

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the encodedPasswd
     */
    public String getEncodedPasswd() {
        return encodedPasswd;
    }

    /**
     * @param encodedPasswd
     *            the encodedPasswd to set
     */
    public void setEncodedPasswd(String encodedPasswd) {
        this.encodedPasswd = encodedPasswd;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the lastLoginDate
     */
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * @param lastLoginDate
     *            the lastLoginDate to set
     */
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * @return the mainGroupId
     */
    public String getMainGroupId() {
        return mainGroupId;
    }

    /**
     * @param mainGroupId
     *            the mainGroupId to set
     */
    public void setMainGroupId(String mainGroupId) {
        this.mainGroupId = mainGroupId;
    }

}
