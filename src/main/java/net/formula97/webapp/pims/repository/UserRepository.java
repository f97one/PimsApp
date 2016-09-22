/**
 * 
 */
package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.formula97.webapp.pims.domain.Users;

/**
 * @author f97one
 *
 */
public interface UserRepository extends JpaRepository<Users, String> {

}
