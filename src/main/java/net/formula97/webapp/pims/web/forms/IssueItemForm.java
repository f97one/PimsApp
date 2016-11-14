package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.domain.IssueItems;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by f97one on 2016/10/23.
 */
@Data
@NoArgsConstructor
public class IssueItemForm implements Serializable {

    private static final long serialVersionUID = 5734607814996487082L;
    /**
     * 画面の操作モード
     */
    private String opeMode;
    /**
     * 現在の台帳名
     */
    private String currentLedgerName;
    /**
     * 課題番号
     */
    private String issueNumberLabel;
    /**
     * レコード更新日時
     */
    private Date recordTimestamp;
    /**
     * 発生日
     */
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date foundDate;
    /**
     * 緊急度
     */
    @NotNull
    private String severity;
    /**
     * 発見者のユーザーID
     */
    @NotNull
    @Size(min = 1, max = 32)
    private String foundUserId;
    /**
     * 機能種別ID
     */
    @Digits(integer = 8, fraction = 0)
    private String categoryId;
    /**
     * 原因工程ID
     */
    @Digits(integer = 8, fraction = 0)
    private String processId;
    /**
     * 障害内容
     */
    @NotNull
    @Max(32768)
    private String issueDetail;
    /**
     * 原因
     */
    @Max(32768)
    private String caused;
    /**
     * 対応内容
     */
    @Max(32768)
    private String countermeasures;
    /**
     * 対応者ID
     */
    private String correspondingUserId;
    /**
     * 対応時間
     */
    @Digits(integer = 3, fraction = 2)
    private String correspondingTime;
    /**
     * 対応終了日
     */
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date correspondingEndDate;
    /**
     * 確認者ID
     */
    private String confirmedUserId;
    /**
     * 確認日
     */
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date confirmedDate;

    public IssueItemForm convertToForm(IssueItems item) {
        this.foundDate = item.getFoundDate();
        this.severity = item.getSevereLevelId() == null ? null : String.valueOf(item.getSevereLevelId());
        this.foundUserId = item.getFoundUser();
        this.categoryId = item.getCategoryId() == null ? null : String.valueOf(item.getCategoryId());
        this.processId = item.getFoundProcessId() == null ? null : String.valueOf(item.getFoundProcessId());
        this.issueDetail = item.getIssueDetail();
        this.caused = item.getCaused();
        this.countermeasures = item.getCountermeasures();
        this.correspondingUserId = item.getCorrespondingUserId();

        if (item.getCorrespondingTime() == null) {
            this.correspondingTime = null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getCorrespondingTime());
            float ct = (float) cal.get(Calendar.HOUR_OF_DAY) + (60.0f / (float) cal.get(Calendar.MINUTE));
            BigDecimal bd = new BigDecimal(ct);
            this.correspondingTime = String.valueOf(bd.setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        this.correspondingEndDate = item.getCorrespondingEndDate();
        this.confirmedUserId = item.getConfirmedId();
        this.confirmedDate = item.getConfirmedDate();

        return this;
    }

    public IssueItems exportToEntity() {
        IssueItems items = new IssueItems();

        items.setFoundDate(this.foundDate);
        items.setSevereLevelId(Integer.parseInt(this.severity));
        items.setFoundUser(this.foundUserId);
        items.setCategoryId(Integer.parseInt(this.categoryId));
        items.setFoundProcessId(Integer.parseInt(this.processId));
        items.setIssueDetail(this.issueDetail);
        items.setCaused(this.caused);
        items.setCountermeasures(this.countermeasures);
        items.setCorrespondingUserId(this.correspondingUserId);

        if (this.correspondingTime != null) {
            long ct = (long) (Double.parseDouble(this.correspondingTime) * 3.6 * 1000000);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ct);
            items.setCorrespondingTime(calendar.getTime());
        }

        items.setCorrespondingEndDate(this.correspondingEndDate);
        items.setCorrespondingUserId(this.correspondingUserId);
        items.setConfirmedDate(this.confirmedDate);

        return items;
    }
}
