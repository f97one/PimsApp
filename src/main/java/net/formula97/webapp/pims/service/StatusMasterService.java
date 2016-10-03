/**
 * 
 */
package net.formula97.webapp.pims.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.formula97.webapp.pims.domain.StatusMaster;
import net.formula97.webapp.pims.repository.StatusMasterRepository;

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
}
