/**
 * 
 */
package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.formula97.webapp.pims.domain.SystemConfig;

/**
 * @author f97one
 *
 */
public interface SystemConfigRepository extends JpaRepository<SystemConfig, String> {

}
