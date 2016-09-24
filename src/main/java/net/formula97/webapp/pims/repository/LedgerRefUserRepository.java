package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.LedgerRefUserPK;

/**
 * @author f97one
 *
 */
@Repository
public interface LedgerRefUserRepository extends JpaRepository<LedgerRefUser, LedgerRefUserPK> {

}
