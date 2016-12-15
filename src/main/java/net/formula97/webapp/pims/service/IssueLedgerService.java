/**
 * 
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import net.formula97.webapp.pims.repository.MySpecificationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author f97one
 *
 */
@Service
public class IssueLedgerService {

    @Autowired
    IssueLedgerRepository issueLedgerRepo;
    @Autowired
    LedgerRefUserRepository ledgerRefUserRepo;
    
    public List<IssueLedger> getPublicLedgers() {
        MySpecificationAdapter<IssueLedger> issueLedgerSpecification = new MySpecificationAdapter<>(IssueLedger.class);
        return issueLedgerRepo.findAll(issueLedgerSpecification.eq("publicLedger", true));
    }
    
    public List<IssueLedger> getLedgersForUser(String userId) {
        List<IssueLedger> joinedLedgers = issueLedgerRepo.findForUser(userId);
        List<IssueLedger> publicLedgers = getPublicLedgers();
        
        List<IssueLedger> retList = new ArrayList<>(joinedLedgers);
        retList.addAll(publicLedgers);

        return retList.stream().distinct().collect(Collectors.toList());
    }

    public IssueLedger getLedgerById(Integer ledgerId) {
        MySpecificationAdapter<IssueLedger> issueLedgerSpecification = new MySpecificationAdapter<>(IssueLedger.class);
        return issueLedgerRepo.findOne(issueLedgerSpecification.eq("ledgerId", ledgerId));
    }

    @Transactional
    public void saveLedger(IssueLedger ledger, Users users) {
        issueLedgerRepo.save(ledger);
        MySpecificationAdapter<IssueLedger> issueLedgerSpecification = new MySpecificationAdapter<>(IssueLedger.class);

        IssueLedger ledger1;
        if (ledger.getLedgerId() == null) {
            ledger1 = issueLedgerRepo.findOne(issueLedgerSpecification.eq("ledgerName", ledger.getLedgerName()));
        } else {
            ledger1 = ledger;
        }
        LedgerRefUser lru = new LedgerRefUser();
        lru.setLedgerId(ledger1.getLedgerId());
        lru.setUserId(users.getUsername());

        ledgerRefUserRepo.save(lru);
    }

    public IssueLedger getLedgerById(int ledgerId) {
        return issueLedgerRepo.findOne(ledgerId);
    }
}
