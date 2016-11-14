/**
 * 
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        return issueLedgerRepo.findAll(Specifications.where(IssueLedgerSpecifications.isPublicSpecified(true)));
    }
    
    public List<IssueLedger> getLedgersForUser(String userId) {
        List<IssueLedger> joinedLedgers = issueLedgerRepo.findForUser(userId);
        List<IssueLedger> publicLedgers = getPublicLedgers();
        
        List<IssueLedger> retList = new ArrayList<>(joinedLedgers);
        retList.addAll(publicLedgers);
        
        IssueLedger[] retArray = retList.stream().distinct().toArray(IssueLedger[]::new);
        
        return new ArrayList<>(Arrays.asList(retArray));
    }

    @Transactional
    public void saveLedger(IssueLedger ledger, Users users) {
        issueLedgerRepo.save(ledger);

        IssueLedger ledger1;
        if (ledger.getLedgerId() == null) {
            ledger1 = issueLedgerRepo.findOne(Specifications.where(IssueLedgerSpecifications.nameEquals(ledger.getLedgerName())));
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

    public static class IssueLedgerSpecifications {
        
        public static Specification<IssueLedger> nameContains(String ledgerName) {
            return StringUtils.isEmpty(ledgerName) ? null : (root, query, cb) -> cb.like(root.get("ledgerName"), "%" + ledgerName + "%");
        }

        public static Specification<IssueLedger> nameEquals(String ledgerName) {
            return StringUtils.isEmpty(ledgerName) ? null : (root, query, cb) -> cb.equal(root.get("ledgerName"), ledgerName);
        }
        
        public static Specification<IssueLedger> openStatusSpecified(Integer openStatusId) {
            return openStatusId == null ? null : (root, query, cb) -> cb.equal(root.get("openStatus"), openStatusId);
        }
        
        public static Specification<IssueLedger> isPublicSpecified(Boolean isPublic) {
            return isPublic == null ? null : (root, query, cb) -> cb.equal(root.get("publicLedger"), isPublic);
        }
    }
}
