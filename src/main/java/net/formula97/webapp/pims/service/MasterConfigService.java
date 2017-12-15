/**
 *
 */
package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Transactional
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
            case MASTER_TYPE_CATEGORY:
                List<CategoryMaster> catList = new ArrayList<>();
                for (MasterDomain md : masterDomains) {
                    catList.add((CategoryMaster) md);
                }
                categoryMasterRepo.save(catList);
                break;
            case MASTER_TYPE_PROCESS:
                List<ProcessMaster> pmList = new ArrayList<>();
                for (MasterDomain md : masterDomains) {
                    pmList.add((ProcessMaster) md);
                }
                processMasterRepo.save(pmList);
                break;
            case MASTER_TYPE_SEVERE_LEVEL:
                List<SevereLevelMaster> slmList = new ArrayList<>();
                for (MasterDomain md : masterDomains) {
                    slmList.add((SevereLevelMaster) md);
                }
                severeLevelMasterRepo.save(slmList);
                break;
            case MASTER_TYPE_STATUS:
                List<StatusMaster> smList = new ArrayList<>();
                for (MasterDomain md : masterDomains) {
                    smList.add((StatusMaster) md);
                }
                statusMasterRepo.save(smList);
                break;
        }
    }

    public void addMasterByType(String masterType, String masterName) {
        addMasterByType(masterType, masterName, false);
    }

    @Transactional
    public void addMasterByType(String masterType, String masterName, boolean asFinished) {
        int latestId;
        int latestDispOrder;

        switch (masterType) {
            case MASTER_TYPE_CATEGORY:
                // 最大の表示オーダーを取得
                List<CategoryMaster> beforeCmList = categoryMasterRepo.findAllOrderByDispOrder();
                Optional<CategoryMaster> latestOrder1 = beforeCmList.stream().max(Comparator.comparing(CategoryMaster::getCategoryId));
                Optional<CategoryMaster> latestOrder2 = beforeCmList.stream().max(Comparator.comparing(CategoryMaster::getDispOrder));

                latestId = latestOrder1.get().getCategoryId() + 1;
                latestDispOrder = latestOrder2.get().getDispOrder() + 1;

                CategoryMaster cm = new CategoryMaster(latestId, masterName, latestDispOrder);

                categoryMasterRepo.save(cm);
                break;

            case MASTER_TYPE_PROCESS:
                // 最大の表示オーダーを取得
                List<ProcessMaster> beforePmList = processMasterRepo.findAllOrderByDispOrder();
                Optional<ProcessMaster> latestPmOrder1 = beforePmList.stream().max(Comparator.comparing(ProcessMaster::getProcessId));
                Optional<ProcessMaster> latestPmOrder2 = beforePmList.stream().max(Comparator.comparing(ProcessMaster::getDispOrder));

                latestId = latestPmOrder1.get().getProcessId() + 1;
                latestDispOrder = latestPmOrder2.get().getDispOrder() + 1;

                ProcessMaster pm = new ProcessMaster(latestId, masterName, latestDispOrder);

                processMasterRepo.save(pm);
                break;

            default:

        }
    }

}
