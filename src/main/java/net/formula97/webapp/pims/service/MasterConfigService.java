/**
 *
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public void updateDisplayOrder(String masterType, List<MasterDomain> masterDomains) {
        // masterTypeがnullだと例外を投げる
        if (masterType == null) {
            throw new IllegalArgumentException("Argument masterType must be specified valid value.");
        }

        // masterTypeとdomainの型が一致しない場合、例外を投げる
        String baseExMsg = "Incompatible masterType, Type %s was pass.";
        switch (masterType) {
            case MASTER_TYPE_CATEGORY:
                for (MasterDomain md : masterDomains) {
                    if (!(md instanceof CategoryMaster)) {
                        String exMsg = String.format(Locale.getDefault(), baseExMsg, md.getClass().getSimpleName());

                        throw new IllegalArgumentException(exMsg);
                    }
                }
                break;
            case MASTER_TYPE_PROCESS:
                for (MasterDomain md : masterDomains) {
                    if (!(md instanceof ProcessMaster)) {
                        String exMsg = String.format(Locale.getDefault(), baseExMsg, md.getClass().getSimpleName());

                        throw new IllegalArgumentException(exMsg);
                    }
                }
                break;
            case MASTER_TYPE_SEVERE_LEVEL:
                for (MasterDomain md : masterDomains) {
                    if (!(md instanceof SevereLevelMaster)) {
                        String exMsg = String.format(Locale.getDefault(), baseExMsg, md.getClass().getSimpleName());

                        throw new IllegalArgumentException(exMsg);
                    }
                }
                break;
            case MASTER_TYPE_STATUS:
                for (MasterDomain md : masterDomains) {
                    if (!(md instanceof StatusMaster)) {
                        String exMsg = String.format(Locale.getDefault(), baseExMsg, md.getClass().getSimpleName());

                        throw new IllegalArgumentException(exMsg);
                    }
                }
                break;
            default:
                // それ以外は例外を投げる
                throw new IllegalArgumentException("Argument masterType must be specified valid value.");
        }

        // Listの中がすべて同じことは上記で確認済み
        switch (masterType) {

        }
    }

}
