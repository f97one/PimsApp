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
 * 緊急度マスタ
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = SevereLevelMaster.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SevereLevelMaster implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8526932189587228945L;

    /**
     * テーブル名
     */
    public static final String TABLE_NAME = "SEVERE_LEVEL_MASTER";

    public static final String COLUMN_SEVERE_LEVEL_ID = "SEVERE_LEVEL_ID";
    public static final String COLUMN_SEVERE_LEVEL = "SEVERE_LEVEL";
    public static final String COLUMN_DISP_ORDER = "DISP_ORDER";

    /**
     * 緊急度ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_SEVERE_LEVEL_ID)
    private Integer severeLevelId;

    @Column(name = COLUMN_SEVERE_LEVEL, length = 8)
    private String severeLevel;
    
    @Column(name = COLUMN_DISP_ORDER)
    private Integer dispOrder;
}
