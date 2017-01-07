package net.formula97.webapp.pims.repository;

import net.formula97.webapp.pims.domain.CategoryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author f97one
 *
 */
@Repository
public interface CategoryMasterRepository extends JpaRepository<CategoryMaster, Integer>, JpaSpecificationExecutor<CategoryMaster> {

    @Query("Select r From CategoryMaster r Order By r.dispOrder")
    List<CategoryMaster> findAllOrderByDispOrder();
}
