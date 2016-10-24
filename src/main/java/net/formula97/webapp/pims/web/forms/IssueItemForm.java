package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by f97one on 2016/10/23.
 */
@Data
@NoArgsConstructor
public class IssueItemForm {

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
     * 発生日
     */
    private Date foundDate;

}
