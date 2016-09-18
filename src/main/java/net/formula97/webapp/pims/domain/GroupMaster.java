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
 * グループマスタ
 * 
 * @author f97one
 *
 */
@Entity
@Table(name = GroupMaster.TABLE_NAME)
public class GroupMaster implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 144919919200792119L;

    public static final String TABLE_NAME = "GROUP_MASTER";

    public static final String COLUMN_GROUP_ID = "GROUP_ID";
    public static final String COLUMN_GROUP_DESC = "GROUP_DESC";

    @Id
    @Column(name = COLUMN_GROUP_ID, length = 32)
    private String groupId;

    @Column(name = COLUMN_GROUP_DESC, length = 2048)
    private String groupDesc;

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

    /**
     * @return the groupDesc
     */
    public String getGroupDesc() {
        return groupDesc;
    }

    /**
     * @param groupDesc
     *            the groupDesc to set
     */
    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }
}
