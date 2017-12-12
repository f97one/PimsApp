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
 * カテゴリーマスタ
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = CategoryMaster.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMaster implements Serializable, MasterDomain {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7646083749403881943L;

    /**
     * テーブル名
     */
    public static final String TABLE_NAME = "CATEGORY_MASTER";
    
    public static final String COLUMN_CATEGORY_ID = "CATEGORY_ID";
    public static final String COLUMN_CATEGORY_NAME = "CATEGORY_NAME";
    public static final String COLUMN_DISP_ORDER = "DISP_ORDER";
    
    /**
     * カテゴリーID
     */
    @Id
    @Column(name = COLUMN_CATEGORY_ID)
    private Integer categoryId;
    
    /**
     * カテゴリー名
     */
    @Column(name = COLUMN_CATEGORY_NAME, length = 128)
    private String categoryName;

    /**
     * 表示順序
     */
    @Column(name = COLUMN_DISP_ORDER)
    private Integer dispOrder;
}
