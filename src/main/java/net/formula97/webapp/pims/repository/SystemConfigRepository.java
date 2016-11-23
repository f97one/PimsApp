/**
 * 
 */
package net.formula97.webapp.pims.repository;

import net.formula97.webapp.pims.domain.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author f97one
 *
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, String>, JpaSpecificationExecutor<SystemConfig> {

    SystemConfig findByConfigKey(String configKey);
}
