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
 * @author f97one
 *
 */
@Entity
@Table(name = SystemConfig.TABLE_NAME)
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

    public SystemConfig() {
        this.configKey = "";
    }
    
    /**
     * @return the configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * @param configKey
     *            the configKey to set
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * @return the configValue
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * @param configValue
     *            the configValue to set
     */
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
