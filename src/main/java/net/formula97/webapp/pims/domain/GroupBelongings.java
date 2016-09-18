/**
 * 
 */
package net.formula97.webapp.pims.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * グループ所属
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = GroupBelongings.TABLE_NAME)
public class GroupBelongings implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5526063160318412517L;

    public static final String TABLE_NAME = "GROUP_BELONGINGS";

    public static final String COLUMN_USER_ID = "USER_ID";
    public static final String COLUMN_GROUP_ID = "GROUP_ID";

    @Id
    @Column(name = COLUMN_USER_ID, length = 32)
    private String userId;

    @Id
    @Column(name = COLUMN_GROUP_ID, length = 32)
    private String groupId;

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     *            the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
