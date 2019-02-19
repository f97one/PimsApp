/**
 * 
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.CommonsStringUtils;
import net.formula97.webapp.pims.repository.IssueItemsRepository;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import net.formula97.webapp.pims.repository.MySpecificationAdapter;
import net.formula97.webapp.pims.web.forms.LedgerSearchConditionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    @Autowired
    IssueItemsRepository issueItemsRepo;
    
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

    @Transactional
    public void saveLedger(IssueLedger ledger, Users users) {
        issueLedgerRepo.save(ledger);
        MySpecificationAdapter<IssueLedger> issueLedgerSpecification = new MySpecificationAdapter<>(IssueLedger.class);

        IssueLedger ledger1;
        if (ledger.getLedgerId() == null) {
            Optional<IssueLedger> issueLedgerOpt = issueLedgerRepo.findOne(issueLedgerSpecification.eq("ledgerName", ledger.getLedgerName()));
            ledger1 = issueLedgerOpt.orElse(ledger);
        } else {
            ledger1 = ledger;
        }
        LedgerRefUser lru = new LedgerRefUser();
        lru.setLedgerId(ledger1.getLedgerId());
        lru.setUserId(users.getUsername());

        ledgerRefUserRepo.save(lru);
    }

    @Transactional
    public void saveLedger(IssueLedger ledger) {
        issueLedgerRepo.save(ledger);
    }

    public IssueLedger getLedgerById(int ledgerId) {
        return issueLedgerRepo.findById(ledgerId).orElse(null);
    }

    public List<IssueLedger> getLedgerByList(LedgerSearchConditionForm form) {
        MySpecificationAdapter<IssueLedger> issueLedgerSpec = new MySpecificationAdapter<>(IssueLedger.class);

        String ledgerName = CommonsStringUtils.isNullOrEmpty(form.getLedgerName()) ? null : form.getLedgerName();
        Boolean publicLedger;
        if (form.getPublicStatus() == null) {
            publicLedger = null;
        } else {
            switch (form.getPublicStatus()) {
                case 1:
                    publicLedger = true;
                    break;
                case 2:
                    publicLedger = false;
                    break;
                default:
                    publicLedger = null;
                    break;
            }
        }

        List<IssueLedger> ledgerBaseList = issueLedgerRepo.findAll(
                Specification.where(issueLedgerSpec.contains("ledgerName", ledgerName)).
                        and(issueLedgerSpec.eq("publicLedger", publicLedger)));

        final List<Integer> statusList = form.getLedgerStatus() == null ?
                new ArrayList<>() : new ArrayList<>(form.getLedgerStatus());

        List<IssueLedger> ledgerList;
        if (statusList.size() == 0) {
            ledgerList = new ArrayList<>(ledgerBaseList);
        } else {
            ledgerList = ledgerBaseList.stream().filter(r -> statusList.contains(r.getOpenStatus())).collect(Collectors.toList());
        }

        return ledgerList;
    }

    @Transactional
    public void removeLedgerById(int ledgerId) {
        // LedgerRefUser → IssueItems → IssueLedger の順でレコードを消す
        Optional<IssueLedger> issueLedgerOpt = issueLedgerRepo.findById(ledgerId);

        if (issueLedgerOpt.isPresent()) {
            // 複数あるものは、先に削除対象を取得して deleteInBatch に流す
            MySpecificationAdapter<LedgerRefUser> ledgerRefUserAdapter = new MySpecificationAdapter<>(LedgerRefUser.class);
            List<LedgerRefUser> delLRU = ledgerRefUserRepo.findAll(Specification.where(ledgerRefUserAdapter.eq("ledgerId", ledgerId)));
            if (delLRU.size() > 0) {
                ledgerRefUserRepo.deleteInBatch(delLRU);
            }

            MySpecificationAdapter<IssueItems> issueItemsAdapter = new MySpecificationAdapter<>(IssueItems.class);
            List<IssueItems> delIssueItemsList = issueItemsRepo.findAll(Specification.where(issueItemsAdapter.eq("ledgerId", ledgerId)));
            if (delIssueItemsList.size() > 0) {
                issueItemsRepo.deleteInBatch(delIssueItemsList);
            }

            issueLedgerRepo.delete(issueLedgerOpt.get());

        } else {
            String msg = String.format(Locale.getDefault(), "LedgerID %d was not found on this system.", ledgerId);
            throw new IllegalArgumentException(msg);
        }
    }
}
