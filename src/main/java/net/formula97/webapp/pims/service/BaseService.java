package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import net.formula97.webapp.pims.misc.CommonsStringUtils;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.web.forms.UserSearchConditionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    protected String findCategoryById(List<CategoryMaster> categoryMasterList, Integer categoryId) {
        if (categoryId == null) {
            return "";
        }

        Optional<CategoryMaster> categoryMasterOptional = categoryMasterList.stream().filter((r) -> r.getCategoryId() == categoryId).findFirst();

        return categoryMasterOptional.isPresent() ? categoryMasterOptional.get().getCategoryName() : "";
    }

    protected String findProcessById(List<ProcessMaster> processMasterList, Integer processId) {
        if (processId == null) {
            return "";
        }

        Optional<ProcessMaster> processMasterOptional = processMasterList.stream().filter((r) -> r.getProcessId() == processId).findFirst();

        return processMasterOptional.isPresent() ? processMasterOptional.get().getProcessName() : "";
    }

    protected String findSeverityById(List<SevereLevelMaster> severeLevelMasterList, Integer severeLevelId) {
        if (severeLevelId == null) {
            return "";
        }

        Optional<SevereLevelMaster> severeLevelMasterOptional = severeLevelMasterList.stream().filter((r) -> r.getSevereLevelId() == severeLevelId).findFirst();

        return severeLevelMasterOptional.isPresent() ? severeLevelMasterOptional.get().getSevereLevel() : "";
    }

    protected String findStatusById(List<StatusMaster> statusMasterList, Integer statusId) {
        if (statusId == null) {
            return "";
        }

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

    /**
     * @author f97one
     */
    @Service
    public static class AuthorizedUsersService implements UserDetailsService {

        @Autowired
        UserRepository userRepo;

        /**
         * @see UserDetailsService#loadUserByUsername(String)
         */
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Users users = userRepo.findById(username).orElse(null);

            if (users == null || username.length() == 0) {
                throw new UsernameNotFoundException(String.format(Locale.getDefault(), "Requested username ( %s ) not found.", username));
            }

            return new AuthorizedUsers(users);
        }

        @Transactional
        public void saveUsers(Users users) {
            userRepo.save(users);
        }

        public List<Users> findUsers(UserSearchConditionForm searchConditions) {
            MySpecificationAdapter<Users> saUsers = new MySpecificationAdapter<>(Users.class);

            String username = CommonsStringUtils.isNullOrWhiteSpace(searchConditions.getUsername()) ? null : searchConditions.getUsername();
            String displayName = CommonsStringUtils.isNullOrWhiteSpace(searchConditions.getDisplayName()) ? null : searchConditions.getDisplayName();
            String mailAddress = CommonsStringUtils.isNullOrWhiteSpace(searchConditions.getMailAddress()) ? null : searchConditions.getMailAddress();
            Boolean enabledUser = searchConditions.getLimitEnabledUser() ? Boolean.TRUE : null;

            return userRepo.findAll(Specifications.where(saUsers.contains("username", username))
                    .and(saUsers.contains("displayName", displayName))
                    .and(saUsers.contains("mailAddress", mailAddress))
                    .and(saUsers.eq("enabled", enabledUser)));
        }

        public Users findUserById(String username) {
            return userRepo.findById(username).orElse(null);
        }
    }
}
