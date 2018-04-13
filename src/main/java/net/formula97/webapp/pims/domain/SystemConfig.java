/**
 * 
 */
package net.formula97.webapp.pims.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author f97one
 *
 */
@Entity
@Table(name = SystemConfig.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3886602320964573761L;

    public static final String TABLE_NAME = "SYSTEM_CONFIG";

    public static final String COLUMN_CONFIG_KEY = "CONFIG_KEY";
    public static final String COLUMN_CONFIG_VALUE = "CONFIG_VALUE";

    @Id
    @Column(name = COLUMN_CONFIG_KEY, length = 32)
    private String configKey;

    @Column(name = COLUMN_CONFIG_VALUE, length = 256)
    private String configValue;
}
