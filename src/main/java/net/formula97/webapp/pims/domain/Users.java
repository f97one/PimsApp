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
import javax.persistence.Temporal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.formula97.webapp.pims.misc.AppConstants;

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
    public static final String COLUMN_MAIL_ADDRESS = "MAIL_ADDRESS";
    public static final String COLUMN_AUTHORITY = "AUTHORITY";

    @Id
    @Column(name = COLUMN_USER_ID, length = 32)
    private String userId;

    @JsonIgnore
    @Column(name = COLUMN_ENCODED_PASSWD, length = 128, nullable = false)
    private String encodedPasswd;

    @Column(name = COLUMN_DISPLAY_NAME, length = 128)
    private String displayName;

    @Column(name = COLUMN_LAST_LOGIN_DATE)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @Column(name = COLUMN_MAIL_ADDRESS, length = 128)
    private String mailAddress;

    @Column(name = COLUMN_AUTHORITY, length = 64, nullable = false)
    private String authority;
    
    public Users() {
        this.userId = "";
        this.encodedPasswd = "";
        this.authority = AppConstants.AUTHORITY_USER;
    }
    
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
     * @return the mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * @param mailAddress
     *            the mailAddress to set
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * @return the authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * @param authority the authority to set
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
