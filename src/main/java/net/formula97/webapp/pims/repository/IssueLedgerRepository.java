package net.formula97.webapp.pims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.formula97.webapp.pims.domain.IssueLedger;

/**
 * @author f97one
 *
 */
@Repository
public interface IssueLedgerRepository extends JpaRepository<IssueLedger, Integer> {

    @Query("SELECT o FROM IssueLedger o WHERE o.isPublic = true ORDER BY ledgerId")
    List<IssueLedger> findByPublicLedger();
}
