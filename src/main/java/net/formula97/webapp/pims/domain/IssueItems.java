/**
 * 
 */
package net.formula97.webapp.pims.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 課題項目
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = IssueItems.TABLE_NAME)
@IdClass(IssueItemsPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueItems implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5845972777314253899L;

    public static final String TABLE_NAME = "ISSUE_ITEMS";

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
    public static final String COLUMN_ROW_UPDATED_AT = "ROW_UPDATED_AT";

    @Id
    private Integer ledgerId;

    @Id
    private Integer issueId;

    @Column(name = COLUMN_ACTION_STATUS_ID)
    private Integer actionStatusId;

    @Column(name = COLUMN_SEVERE_LEVEL_ID)
    private Integer severeLevelId;

    @Column(name = COLUMN_FOUND_USER, length = 32)
    private String foundUser;

    @Column(name = COLUMN_FOUND_DATE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date foundDate;

    @Column(name = COLUMN_FOUND_PROCESS_ID)
    private Integer foundProcessId;

    @Column(name = COLUMN_CATEGORY_ID)
    private Integer categoryId;

    @Column(name = COLUMN_ISSUE_DETAIL, length = 32768)
    private String issueDetail;

    @Column(name = COLUMN_CAUSED, length = 32768)
    private String caused;

    @Column(name = COLUMN_COUNTERMEASURES, length = 32768)
    private String countermeasures;

    @Column(name = COLUMN_CORRESPONDING_USER_ID, length = 32)
    private String correspondingUserId;

    @Column(name = COLUMN_CORRESPONDING_TIME)
    @Temporal(TemporalType.TIME)
    private Date correspondingTime;

    @Column(name = COLUMN_CORRESPONDING_END_DATE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date correspondingEndDate;

    @Column(name = COLUMN_CONFIRMED_ID, length = 32)
    private String confirmedId;

    @Column(name = COLUMN_COMFIRMED_DATE)
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedDate;

    @Column(name = COLUMN_ROW_UPDATED_AT)
    @Temporal(TemporalType.TIMESTAMP)
    private Date rowUpdatedAt;
}
