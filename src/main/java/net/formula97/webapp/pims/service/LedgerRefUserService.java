package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.LedgerRefUserPK;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import net.formula97.webapp.pims.repository.MySpecificationAdapter;
import net.formula97.webapp.pims.repository.UserRepository;
import net.formula97.webapp.pims.web.forms.RefUserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by f97one on 2016/11/14.
 */
@Service
public class LedgerRefUserService {
    @Autowired
    LedgerRefUserRepository ledgerRefUserRepo;
    @Autowired
    UserRepository userRepo;

    public LedgerRefUser findReferenceForUser(String userId, int ledgerId) {
        return ledgerRefUserRepo.findOne(new LedgerRefUserPK(ledgerId, userId));
    }

    public List<RefUserItem> getReferenceConditionById(int ledgerId) {
        MySpecificationAdapter<Users> usersSpec = new MySpecificationAdapter<>(Users.class);
        List<Users> usersList = userRepo.findAll(Specifications.where(usersSpec.eq("enabled", true)));

        if (usersList.size() == 0) {
            return new ArrayList<>();
        } else {
            MySpecificationAdapter<LedgerRefUser> ledgerRefUserSpec = new MySpecificationAdapter<>(LedgerRefUser.class);
            List<LedgerRefUser> refUsersList = ledgerRefUserRepo.findAll(Specifications.where(ledgerRefUserSpec.eq("ledgerId", ledgerId)));

            List<RefUserItem> refUserItemList = new ArrayList<>();

            for (Users users : usersList) {
                RefUserItem item = new RefUserItem();
                item.setUserId(users.getUsername());
                item.setDisplayName(users.getDisplayName());

                Optional<LedgerRefUser> ledgerRefUserOptional = refUsersList.stream().filter(r -> r.getUserId().equals(users.getUsername())).findFirst();

                item.setUserJoined(ledgerRefUserOptional.isPresent());

                refUserItemList.add(item);
            }

            return refUserItemList;
        }
    }
}
