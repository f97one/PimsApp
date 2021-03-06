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
                categoryMasterRepo.saveAll(catList);
                break;
            case MASTER_TYPE_PROCESS:
                List<ProcessMaster> pmList = new ArrayList<>();
                for (MasterDomain md : masterDomains) {
                    pmList.add((ProcessMaster) md);
                }
                processMasterRepo.saveAll(pmList);
                break;
            case MASTER_TYPE_SEVERE_LEVEL:
                List<SevereLevelMaster> slmList = new ArrayList<>();
                for (MasterDomain md : masterDomains) {
                    slmList.add((SevereLevelMaster) md);
                }
                severeLevelMasterRepo.saveAll(slmList);
                break;
            case MASTER_TYPE_STATUS:
                List<StatusMaster> smList = new ArrayList<>();
                for (MasterDomain md : masterDomains) {
                    smList.add((StatusMaster) md);
                }
                statusMasterRepo.saveAll(smList);
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

        if (masterType == null) {
            throw new IllegalArgumentException("Argument masterType must be specified valid value.");
        }

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

            case MASTER_TYPE_SEVERE_LEVEL:
                // 最大の表示オーダーを取得
                List<SevereLevelMaster> beforeSlmList = severeLevelMasterRepo.findAllOrderByDispOrder();
                Optional<SevereLevelMaster> latestSlm1 = beforeSlmList.stream().max(Comparator.comparing(SevereLevelMaster::getSevereLevelId));
                Optional<SevereLevelMaster> latestSlm2 = beforeSlmList.stream().max(Comparator.comparing(SevereLevelMaster::getDispOrder));

                latestId = latestSlm1.get().getSevereLevelId() + 1;
                latestDispOrder = latestSlm2.get().getDispOrder() + 1;

                SevereLevelMaster slm = new SevereLevelMaster(latestId, masterName, latestDispOrder);

                severeLevelMasterRepo.save(slm);
                break;

            case MASTER_TYPE_STATUS:
                List<StatusMaster> beforeSmList = statusMasterRepo.findAllOrderByDispOrder();
                Optional<StatusMaster> latestSm1 = beforeSmList.stream().max(Comparator.comparing(StatusMaster::getStatusId));
                Optional<StatusMaster> latestSm2 = beforeSmList.stream().max(Comparator.comparing(StatusMaster::getDispOrder));

                latestId = latestSm1.get().getStatusId() + 1;
                latestDispOrder = latestSm2.get().getDispOrder() + 1;

                StatusMaster sm = new StatusMaster(latestId, masterName, latestDispOrder, asFinished);

                statusMasterRepo.save(sm);
                break;

            default:
                throw new IllegalArgumentException("Argument masterType must be specified valid value.");

        }
    }

    /**
     * タイプコードに対応する表示名を取得する。
     *
     * @param typeCode 変換対象のタイプコード
     * @return 変換後の表示名、指定外のコードの場合は空文字を返す
     */
    public String getDisplayNameByCode(String typeCode) {
        if (typeCode == null) {
            return "";
        }

        String ret;
        switch (typeCode) {
            case MASTER_TYPE_CATEGORY:
                ret = "カテゴリー";
                break;
            case MASTER_TYPE_PROCESS:
                ret = "工程";
                break;
            case MASTER_TYPE_SEVERE_LEVEL:
                ret = "緊急度";
                break;
            case MASTER_TYPE_STATUS:
                ret = "ステータス";
                break;
            default:
                ret = "";
                break;
        }

        return ret;
    }

    /**
     * タイプコードに対応する入力時の最大桁数を取得する。
     *
     * @param typeCode 変換対象のタイプコード
     * @return 変換後の入力最大桁数、指定外のコードの場合は8を返す
     */
    public int getInputLengthByType(String typeCode) {
        int ret = 8;

        if (typeCode != null) {
            switch (typeCode) {
                case MASTER_TYPE_CATEGORY:
                    ret = 128;
                    break;
                case MASTER_TYPE_PROCESS:
                    ret = 16;
                    break;
                case MASTER_TYPE_SEVERE_LEVEL:
                    // 初期値のままなので何もしない
                    break;
                case MASTER_TYPE_STATUS:
                    ret = 16;
                    break;
                default:
                    // 初期値のままなので何もしない
                    break;
            }
        }

        return ret;
    }
}
