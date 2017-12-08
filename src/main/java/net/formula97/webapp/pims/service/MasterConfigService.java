/**
 *
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author f97one
 */
@Service
public class MasterConfigService extends BaseService {

    public static final String MASTER_TYPE_STATUS = "status";
    public static final String MASTER_TYPE_SEVERE_LEVEL = "severelevel";
    public static final String MASTER_TYPE_PROCESS = "process";
    public static final String MASTER_TYPE_CATEGORY = "category";

    @SuppressWarnings("unchecked")
    public List<MasterDomain> getWholeMasterByType(String masterType) {
        if (masterType == null) {
            throw new IllegalArgumentException("Argument masterType must be specified valid value.");
        }

        List<MasterDomain> results = new ArrayList<>();
        switch (masterType) {
            case MASTER_TYPE_CATEGORY:
                List<CategoryMaster> categoryMasterList = categoryMasterRepo.findAllOrderByDispOrder();
                results.addAll(categoryMasterList);
                break;
            case MASTER_TYPE_PROCESS:
                List<ProcessMaster> processMasterList = processMasterRepo.findAllOrderByDispOrder();
                results.addAll(processMasterList);
                break;
            case MASTER_TYPE_SEVERE_LEVEL:
                List<SevereLevelMaster> severeLevelMasterList = severeLevelMasterRepo.findAllOrderByDispOrder();
                results.addAll(severeLevelMasterList);
                break;
            case MASTER_TYPE_STATUS:
                List<StatusMaster> statusMasterList = statusMasterRepo.findAllOrderByDispOrder();
                results.addAll(statusMasterList);
                break;
            default:
                // それ以外は例外を投げる
                throw new IllegalArgumentException("Argument masterType must be specified valid value.");
        }

        return results;
    }
}
