package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.IssueItemsPK;

/**
 * @author f97one
 *
 */
@Repository
public interface IssueItemsRepository extends JpaRepository<IssueItems, IssueItemsPK> {

}
