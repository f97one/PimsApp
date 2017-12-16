/**
 *
 */
package net.formula97.webapp.pims.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * ステータスマスタ
 *
 * @author f97one
 *
 */
@Entity
@Table(name = StatusMaster.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusMaster implements Serializable, MasterDomain {

    /**
     *
     */
    private static final long serialVersionUID = -7130357214974979860L;

    public static final String TABLE_NAME = "STATUS_MASTER";

    public static final String COLUMN_STATUS_ID = "STATUS_ID";
    public static final String COLUMN_STATUS_NAME = "STATUS_NAME";
    public static final String COLUMN_DISP_ORDER = "DISP_ORDER";
    public static final String COLUMN_TREAT_AS_FINISHED = "TREAT_AS_FINISHED";

    @Id
    @Column(name = COLUMN_STATUS_ID)
    private Integer statusId;

    @Column(name = COLUMN_STATUS_NAME, length = 16)
    private String statusName;

    @Column(name = COLUMN_DISP_ORDER)
    private Integer dispOrder;

    @Column(name = COLUMN_TREAT_AS_FINISHED)
    private Boolean treatAsFinished;
}
