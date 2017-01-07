/**
 * 
 */
package net.formula97.webapp.pims.repository;

import net.formula97.webapp.pims.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author f97one
 *
 */
@Repository
public interface UserRepository extends JpaRepository<Users, String>, JpaSpecificationExecutor<Users> {

    @Query("Select u From Users u, LedgerRefUser l Where l.ledgerId = :ledgerId And l.userId = u.username")
    List<Users> findRelatedUsers(@Param("ledgerId") int ledgerId);
}
