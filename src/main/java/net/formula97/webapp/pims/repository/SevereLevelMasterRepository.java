/**
 * 
 */
package net.formula97.webapp.pims.repository;

import net.formula97.webapp.pims.domain.SevereLevelMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author f97one
 *
 */
@Repository
public interface SevereLevelMasterRepository extends JpaRepository<SevereLevelMaster, Integer>, JpaSpecificationExecutor<SevereLevelMaster> {

    List<SevereLevelMaster> findAllOrderByDispOrder();
}
