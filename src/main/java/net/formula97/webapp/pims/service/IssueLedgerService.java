/**
 * 
 */
package net.formula97.webapp.pims.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;

/**
 * @author f97one
 *
 */
@Service
public class IssueLedgerService {

    @Autowired
    IssueLedgerRepository issueLedgerRepository;
    
    public List<IssueLedger> getPublicLedgers() {
        return issueLedgerRepository.findAll(Specifications.where(IssueLedgerSpecifications.isPublicSpecified(true)));
    }
    
    public List<IssueLedger> getLedgersForUser(String userId) {
        return issueLedgerRepository.findForUser(userId);
    }
    
    public static class IssueLedgerSpecifications {
        
        public static Specification<IssueLedger> nameContains(String ledgerName) {
            return StringUtils.isEmpty(ledgerName) ? null : (root, query, cb) -> cb.like(root.get("ledgerName"), "%" + ledgerName + "%");
        }
        
        public static Specification<IssueLedger> openStatusSpecified(Integer openStatusId) {
            return openStatusId == null ? null : (root, query, cb) -> cb.equal(root.get("openStatus"), openStatusId);
        }
        
        public static Specification<IssueLedger> isPublicSpecified(Boolean isPublic) {
            return isPublic == null ? null : (root, query, cb) -> cb.equal(root.get("isPublic"), isPublic);
        }
    }
}
