package net.formula97.webapp.pims.web.forms;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.formula97.webapp.pims.domain.IssueItems;

import java.util.Date;

/**
 * Created by f97one on 2016/11/12.
 */
@Data
@NoArgsConstructor
public class IssueItemsLineForm {

    private Integer issueId;
    private String severeLevel;
    private String actionStatus;
    private Date foundDate;
    private String foundUser;
    private String moduleCategory;
    private String process;
    private String issueDetail;
    private String caused;
    private String countermeasures;
    private String correspondingUser;
    private Float correspondingTime;
    private Date correspondingEndDate;
    private String confirmedUser;
    private Date confirmedDate;
    private String lineCssStyle;

    public IssueItemsLineForm convertToForm(IssueItems items) {
        this.issueId = items.getIssueId();


        return this;
    }
}
