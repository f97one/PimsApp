/**
 * 
 */
package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.formula97.webapp.pims.domain.StatusMaster;

/**
 * @author f97one
 *
 */
@Repository
public interface StatusMasterRepository extends JpaRepository<StatusMaster, Integer> {

}