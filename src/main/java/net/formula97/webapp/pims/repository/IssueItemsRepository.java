package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.IssueItemsPK;

public interface IssueItemsRepository extends JpaRepository<IssueItems, IssueItemsPK> {

}
