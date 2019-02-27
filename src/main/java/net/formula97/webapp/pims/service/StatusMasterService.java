/**
 * 
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.StatusMaster;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.repository.StatusMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author f97one
 *
 */
@Service
public class StatusMasterService {

    @Autowired
    StatusMasterRepository statusRepo;
    
    public Map<Integer, String> getStatusMap() {
        List<StatusMaster> smList = statusRepo.findAll();
        Map<Integer, String> smMap = new HashMap<>();
        
        for (StatusMaster m : smList) {
            smMap.put(m.getStatusId(), m.getStatusName());
        }
        
        return smMap;
    }

    public Map<Integer, String> getOpenStatus() {
        Map<Integer, String> ret = new HashMap<>();
        ret.put(AppConstants.LEDGER_OPEN, "公開");
        ret.put(AppConstants.LEDGER_BLOCKING, "ブロック中");
        ret.put(AppConstants.LEDGER_CLOSED, "終了");

        return ret;
    }

    /**
     * ステータスマスタ内の、最小のステータスIDを取得する。
     *
     * @return ステータスID最小値
     */
    public int getMinimumStatus() {
        Optional<StatusMaster> smOpt = statusRepo.findAllOrderByDispOrder().stream().min(Comparator.comparing(StatusMaster::getStatusId));

        return smOpt.isPresent() ? smOpt.get().getStatusId() : Integer.MIN_VALUE;
    }
}
