package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.formula97.webapp.pims.domain.CategoryMaster;

/**
 * @author f97one
 *
 */
@Repository
public interface CategoryMasterRepository extends JpaRepository<CategoryMaster, Integer> {

}
