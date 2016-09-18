/**
 * 
 */
package net.formula97.webapp.pims.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 課題項目
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = IssueItems.TABLE_NAME)
public class IssueItems implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5845972777314253899L;

    public static final String TABLE_NAME = "ISSUE_ITEMS";

    public static final String COLUMN_LEDGER_ID = "LEDGER_ID";
    public static final String COLUMN_ISSUE_ID = "ISSUE_ID";
    public static final String COLUMN_ACTION_STATUS_ID = "ACTION_STATUS_ID";
    public static final String COLUMN_SEVERE_LEVEL_ID = "SEVERE_LEVEL_ID";
    public static final String COLUMN_FOUND_USER = "FOUND_USER";
    public static final String COLUMN_FOUND_DATE = "FOUND_DATE";
    public static final String COLUMN_FOUND_PROCESS_ID = "FOUND_PROCESS_ID";
    public static final String COLUMN_CATEGORY_ID = "CATEGORY_ID";
    public static final String COLUMN_ISSUE_DETAIL = "ISSUE_DETAIL";
    public static final String COLUMN_CAUSED = "CAUSED";
    public static final String COLUMN_COUNTERMEASURES = "COUNTERMEASURES";
    public static final String COLUMN_CORRESPONDING_USER_ID = "CORRESPONDING_USER_ID";
    public static final String COLUMN_CORRESPONDING_TIME = "CORRESPONDING_TIME";
    public static final String COLUMN_CORRESPONDING_END_DATE = "CORRESPONDING_END_DATE";
    public static final String COLUMN_CONFIRMED_ID = "CONFIRMED_ID";
    public static final String COLUMN_COMFIRMED_DATE = "COMFIRMED_DATE";

    /**
     * 
     */
    @Id
    @Column(name = COLUMN_LEDGER_ID)
    private Integer ledgerId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_ISSUE_ID)
    private Integer issueId;

    @Column(name = COLUMN_ACTION_STATUS_ID)
    private Integer actionStatusId;

    @Column(name = COLUMN_SEVERE_LEVEL_ID)
    private Integer severeLevelId;

    @Column(name = COLUMN_FOUND_USER, length = 32)
    private String foundUser;

    @Column(name = COLUMN_FOUND_DATE)
    private Date foundDate;

    @Column(name = COLUMN_FOUND_PROCESS_ID)
    private Integer foundProcessId;

    @Column(name = COLUMN_CATEGORY_ID)
    private Integer categoryId;

    @Column(name = COLUMN_ISSUE_DETAIL, length = 16384)
    private String issueDetail;

    @Column(name = COLUMN_CAUSED, length = 16384)
    private String caused;

    @Column(name = COLUMN_COUNTERMEASURES)
    private String countermeasures;

    @Column(name = COLUMN_CORRESPONDING_USER_ID, length = 32)
    private String correspondingUserId;

    @Column(name = COLUMN_CORRESPONDING_TIME)
    private Date correspondingTime;

    @Column(name = COLUMN_CORRESPONDING_END_DATE)
    private Date correspondingEndDate;

    @Column(name = COLUMN_CONFIRMED_ID, length = 32)
    private String confirmedId;

    @Column(name = COLUMN_COMFIRMED_DATE)
    private Date confirmedDate;

    /**
     * @return the ledgerId
     */
    public Integer getLedgerId() {
        return ledgerId;
    }

    /**
     * @param ledgerId
     *            the ledgerId to set
     */
    public void setLedgerId(Integer ledgerId) {
        this.ledgerId = ledgerId;
    }

    /**
     * @return the issueId
     */
    public Integer getIssueId() {
        return issueId;
    }

    /**
     * @param issueId
     *            the issueId to set
     */
    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    /**
     * @return the actionStatusId
     */
    public Integer getActionStatusId() {
        return actionStatusId;
    }

    /**
     * @param actionStatusId
     *            the actionStatusId to set
     */
    public void setActionStatusId(Integer actionStatusId) {
        this.actionStatusId = actionStatusId;
    }

    /**
     * @return the severeLevelId
     */
    public Integer getSevereLevelId() {
        return severeLevelId;
    }

    /**
     * @param severeLevelId
     *            the severeLevelId to set
     */
    public void setSevereLevelId(Integer severeLevelId) {
        this.severeLevelId = severeLevelId;
    }

    /**
     * @return the foundUser
     */
    public String getFoundUser() {
        return foundUser;
    }

    /**
     * @param foundUser
     *            the foundUser to set
     */
    public void setFoundUser(String foundUser) {
        this.foundUser = foundUser;
    }

    /**
     * @return the foundDate
     */
    public Date getFoundDate() {
        return foundDate;
    }

    /**
     * @param foundDate
     *            the foundDate to set
     */
    public void setFoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }

    /**
     * @return the foundProcessId
     */
    public Integer getFoundProcessId() {
        return foundProcessId;
    }

    /**
     * @param foundProcessId
     *            the foundProcessId to set
     */
    public void setFoundProcessId(Integer foundProcessId) {
        this.foundProcessId = foundProcessId;
    }

    /**
     * @return the categoryId
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId
     *            the categoryId to set
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the issueDetail
     */
    public String getIssueDetail() {
        return issueDetail;
    }

    /**
     * @param issueDetail
     *            the issueDetail to set
     */
    public void setIssueDetail(String issueDetail) {
        this.issueDetail = issueDetail;
    }

    /**
     * @return the caused
     */
    public String getCaused() {
        return caused;
    }

    /**
     * @param caused
     *            the caused to set
     */
    public void setCaused(String caused) {
        this.caused = caused;
    }

    /**
     * @return the countermeasures
     */
    public String getCountermeasures() {
        return countermeasures;
    }

    /**
     * @param countermeasures
     *            the countermeasures to set
     */
    public void setCountermeasures(String countermeasures) {
        this.countermeasures = countermeasures;
    }

    /**
     * @return the correspondingUserId
     */
    public String getCorrespondingUserId() {
        return correspondingUserId;
    }

    /**
     * @param correspondingUserId
     *            the correspondingUserId to set
     */
    public void setCorrespondingUserId(String correspondingUserId) {
        this.correspondingUserId = correspondingUserId;
    }

    /**
     * @return the correspondingTime
     */
    public Date getCorrespondingTime() {
        return correspondingTime;
    }

    /**
     * @param correspondingTime
     *            the correspondingTime to set
     */
    public void setCorrespondingTime(Date correspondingTime) {
        this.correspondingTime = correspondingTime;
    }

    /**
     * @return the correspondingEndDate
     */
    public Date getCorrespondingEndDate() {
        return correspondingEndDate;
    }

    /**
     * @param correspondingEndDate
     *            the correspondingEndDate to set
     */
    public void setCorrespondingEndDate(Date correspondingEndDate) {
        this.correspondingEndDate = correspondingEndDate;
    }

    /**
     * @return the confirmedId
     */
    public String getConfirmedId() {
        return confirmedId;
    }

    /**
     * @param confirmedId
     *            the confirmedId to set
     */
    public void setConfirmedId(String confirmedId) {
        this.confirmedId = confirmedId;
    }

    /**
     * @return the confirmedDate
     */
    public Date getConfirmedDate() {
        return confirmedDate;
    }

    /**
     * @param confirmedDate
     *            the confirmedDate to set
     */
    public void setConfirmedDate(Date confirmedDate) {
        this.confirmedDate = confirmedDate;
    }
}
