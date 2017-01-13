package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.misc.AppConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by f97one on 2016/10/23.
 */
@Data
@NoArgsConstructor
public class IssueItemForm implements Serializable {

    private static final long serialVersionUID = 5734607814996487082L;
    /**
     * レコード更新日時(ミリ秒値)
     */
    private Long recordTimestamp;
    /**
     * 発生日
     */
    @Pattern(regexp = "2\\d{3}/[01]\\d/[0123]\\d")
    private String foundDate;
    /**
     * 緊急度
     */
    @NotNull
    @Min(0)
    private Integer severity;
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
    private Integer categoryId;
    /**
     * 原因工程ID
     */
    @Digits(integer = 8, fraction = 0)
    private Integer processId;
    /**
     * 障害内容
     */
    @NotNull
    @Size(max = 32768)
    private String issueDetail;
    /**
     * 原因
     */
    @Size(max = 32768)
    private String caused;
    /**
     * 対応内容
     */
    @Size(max = 32768)
    private String countermeasures;
    /**
     * 対応者ID
     */
    private String correspondingUserId;
    /**
     * 対応時間
     */
    @Digits(integer = 3, fraction = 2)
    private Double correspondingTime;
    /**
     * 対応終了日
     */
    @DateTimeFormat(pattern = AppConstants.STD_DATE_FORMAT)
    private String correspondingEndDate;
    /**
     * 確認者ID
     */
    private String confirmedUserId;
    /**
     * 確認日
     */
    @DateTimeFormat(pattern = AppConstants.STD_DATE_FORMAT)
    private String confirmedDate;

    public IssueItemForm convertToForm(IssueItems item) {
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        this.foundDate = sdf.format(item.getFoundDate());
        this.severity = item.getSevereLevelId();
        this.foundUserId = item.getFoundUser();
        this.categoryId = item.getCategoryId() == null ? null : item.getCategoryId();
        this.processId = item.getFoundProcessId() == null ? null : item.getFoundProcessId();
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
            this.correspondingTime = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        if (item.getCorrespondingEndDate() != null) {
            this.correspondingEndDate = sdf.format(item.getCorrespondingEndDate());
        }
        this.confirmedUserId = item.getConfirmedId();
        if (item.getConfirmedDate() != null) {
            this.confirmedDate = sdf.format(item.getConfirmedDate());
        }

        return this;
    }

    public IssueItems exportToEntity() {
        IssueItems items = new IssueItems();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        try {
            items.setFoundDate(sdf.parse(this.foundDate));
        } catch (ParseException e) {
            e.printStackTrace();
            items.setFoundDate(null);
        }
        // 未選択アイテムだった場合はnullにする
        Integer s = this.severity == AppConstants.SELECTION_NOT_SELECTED ? null : this.severity;
        items.setSevereLevelId(s);

        items.setFoundUser(this.foundUserId);

        Integer c = this.categoryId == AppConstants.SELECTION_NOT_SELECTED ? null : this.categoryId;
        items.setCategoryId(c);

        Integer p = this.processId == AppConstants.SELECTION_NOT_SELECTED ? null : this.processId;
        items.setFoundProcessId(p);

        items.setIssueDetail(this.issueDetail);
        items.setCaused(this.caused);
        items.setCountermeasures(this.countermeasures);
        items.setCorrespondingUserId(this.correspondingUserId);

        if (this.correspondingTime != null) {
            long ct = (long) (this.correspondingTime * 3.6 * 1000000);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ct);
            items.setCorrespondingTime(calendar.getTime());
        }

        try {
            items.setCorrespondingEndDate(sdf.parse(this.correspondingEndDate));
        } catch (ParseException e) {
            e.printStackTrace();
            items.setCorrespondingEndDate(null);
        }
        items.setCorrespondingUserId(this.correspondingUserId);
        try {
            items.setConfirmedDate(sdf.parse(this.confirmedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d = new Date(this.recordTimestamp);
        items.setRowUpdatedAt(d);

        return items;
    }
}
