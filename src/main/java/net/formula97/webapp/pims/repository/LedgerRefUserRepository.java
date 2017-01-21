package net.formula97.webapp.pims.repository;

import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.LedgerRefUserPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author f97one
 *
 */
@Repository
public interface LedgerRefUserRepository extends JpaRepository<LedgerRefUser, LedgerRefUserPK>, JpaSpecificationExecutor<LedgerRefUser> {

}
