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
 * カテゴリーマスタ
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = CategoryMaster.TABLE_NAME)
public class CategoryMaster implements Serializable {
    
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_CATEGORY_ID)
    private Integer categoryId;
    
    /**
     * カテゴリー名
     */
    @Column(name = COLUMN_CATEGORY_NAME, length = 128)
    private String categoryName;

    @Column(name = COLUMN_DISP_ORDER)
    private Integer dispOrder;
    
    public CategoryMaster() {
        this.dispOrder = 0;
    }
    
    /**
     * @return the categoryId
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getDispOrder() {
        return dispOrder;
    }

    public void setDispOrder(Integer dispOrder) {
        this.dispOrder = dispOrder;
    }
        
}
