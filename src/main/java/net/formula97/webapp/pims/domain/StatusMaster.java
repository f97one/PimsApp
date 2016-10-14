/**
 *
 */
package net.formula97.webapp.pims.domain;

import groovy.transform.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class StatusMaster implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7130357214974979860L;

    public static final String TABLE_NAME = "STATUS_MASTER";

    public static final String COLUMN_STATUS_ID = "STATUS_ID";
    public static final String COLUMN_STATUS_NAME = "STATUS_NAME";
    public static final String COLUMN_DISP_ORDER = "DISP_ORDER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_STATUS_ID)
    private Integer statusId;

    @Column(name = COLUMN_STATUS_NAME, length = 16)
    private String statusName;

    @Column(name = COLUMN_DISP_ORDER)
    private Integer dispOrder;
}
