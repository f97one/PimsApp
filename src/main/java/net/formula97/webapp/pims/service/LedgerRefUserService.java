package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.LedgerRefUserPK;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by f97one on 2016/11/14.
 */
@Service
public class LedgerRefUserService {
    @Autowired
    LedgerRefUserRepository ledgerRefUserRepo;

    public LedgerRefUser findReferenceForUser(String userId, int ledgerId) {
        return ledgerRefUserRepo.findOne(new LedgerRefUserPK(ledgerId, userId));
    }
}
