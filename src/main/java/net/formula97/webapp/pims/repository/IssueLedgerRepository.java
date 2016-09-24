package net.formula97.webapp.pims.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.formula97.webapp.pims.domain.IssueLedger;

public interface IssueLedgerRepository extends JpaRepository<IssueLedger, Integer> {

}
