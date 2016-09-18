/**
 * 
 */
package net.formula97.webapp.pims.domain;

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
public class StatusMaster implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7130357214974979860L;

    public static final String TABLE_NAME = "STATUS_MASTER";

    public static final String COLUMN_STATUS_ID = "STATUS_ID";
    public static final String COLUMN_STATUS_NAME = "STATUS_NAME";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_STATUS_ID)
    private Integer statusId;

    @Column(name = COLUMN_STATUS_NAME, length = 16)
    private String statusName;

    /**
     * @return the statusId
     */
    public Integer getStatusId() {
        return statusId;
    }

    /**
     * @param statusId
     *            the statusId to set
     */
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    /**
     * @return the statusName
     */
    public String getStatusName() {
        return statusName;
    }

    /**
     * @param statusName
     *            the statusName to set
     */
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
