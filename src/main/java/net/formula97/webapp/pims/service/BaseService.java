package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import net.formula97.webapp.pims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by f97one on 2016/11/13.
 */
@Service
public abstract class BaseService {

    @Autowired
    CategoryMasterRepository categoryMasterRepo;
    @Autowired
    ProcessMasterRepository processMasterRepo;
    @Autowired
    SevereLevelMasterRepository severeLevelMasterRepo;
    @Autowired
    StatusMasterRepository statusMasterRepo;
    @Autowired
    SystemConfigRepository sysConfigRepo;
    @Autowired
    UserRepository userRepo;

    private Map<String, String> sysConfigMap;

    protected String findCategoryById(List<CategoryMaster> categoryMasterList, int categoryId) {
        Optional<CategoryMaster> categoryMasterOptional = categoryMasterList.stream().filter((r) -> r.getCategoryId() == categoryId).findFirst();

        return categoryMasterOptional.isPresent() ? categoryMasterOptional.get().getCategoryName() : "";
    }

    protected String findProcessById(List<ProcessMaster> processMasterList, int processId) {
        Optional<ProcessMaster> processMasterOptional = processMasterList.stream().filter((r) -> r.getProcessId() == processId).findFirst();

        return processMasterOptional.isPresent() ? processMasterOptional.get().getProcessName() : "";
    }

    protected String findSeverityById(List<SevereLevelMaster> severeLevelMasterList, int severeLevelId) {
        Optional<SevereLevelMaster> severeLevelMasterOptional = severeLevelMasterList.stream().filter((r) -> r.getSevereLevelId() == severeLevelId).findFirst();

        return severeLevelMasterOptional.isPresent() ? severeLevelMasterOptional.get().getSevereLevel() : "";
    }

    protected String findStatusById(List<StatusMaster> statusMasterList, int statusId) {
        Optional<StatusMaster> statusMasterOptional = statusMasterList.stream().filter((r) -> r.getStatusId() == statusId).findFirst();

        return statusMasterOptional.isPresent() ? statusMasterOptional.get().getStatusName() : "";
    }

    protected Map<String, String> getSystemConfig() {
        if (sysConfigMap == null) {
            List<SystemConfig> systemConfigs = sysConfigRepo.findAll();

            // Stream APIで書く方法を学ばなければ....
            sysConfigMap = new HashMap<>();
            for (SystemConfig sc : systemConfigs) {
                sysConfigMap.put(sc.getConfigKey(), sc.getConfigValue());
            }
        }

        return sysConfigMap;
    }

    /**
     * ユーザーリストから指定ユーザーIDの表示名を取得する。
     *
     * @param usersList ユーザーリスト
     * @param userId    検索対象のユーザーID
     * @return 表示名、見つからない場合は空文字
     */
    protected String findUserById(List<Users> usersList, String userId) {
        Optional<Users> usersOpt = usersList.stream().filter((r) -> r.getUsername().equals(userId)).findFirst();

        return usersOpt.isPresent() ? usersOpt.get().getDisplayName() : "";
    }

}
