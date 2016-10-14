/**
 * 
 */
package net.formula97.webapp.pims.domain;

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
 * 工程マスタ
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = ProcessMaster.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessMaster implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5862351189593476909L;

    /**
     * テーブル名
     */
    public static final String TABLE_NAME = "PROCESS_MASTER";

    public static final String COLUMN_PROCESS_ID = "PROCESS_ID";
    public static final String COLUMN_PROCESS_NAME = "PROCESS_NAME";
    public static final String COLUMN_DISP_ORDER = "DISP_ORDER";
    
    /**
     * 工程ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_PROCESS_ID)
    private Integer processId;

    /**
     * 工程名
     */
    @Column(name = COLUMN_PROCESS_NAME, length = 16)
    private String processName;

    @Column(name = COLUMN_DISP_ORDER)
    private Integer dispOrder;
}
