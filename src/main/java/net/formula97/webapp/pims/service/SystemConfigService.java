/**
 * 
 */
package net.formula97.webapp.pims.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.formula97.webapp.pims.misc.SystemConfigKeyValueBinder;
import net.formula97.webapp.pims.web.forms.SystemPreferenceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.formula97.webapp.pims.domain.SystemConfig;
import net.formula97.webapp.pims.repository.SystemConfigRepository;

/**
 * @author f97one
 *
 */
@Service
public class SystemConfigService {

    @Autowired
    SystemConfigRepository sysConfigRepos;
    
    /**
     * 現在設定されている設定値をすべて取得する。
     * 
     * @return すべての設定値
     */
    public Map<String, String> getConfigMap() {
        Map<String, String> configmap = new HashMap<>();
        List<SystemConfig> configs = sysConfigRepos.findAll();
        
        for (SystemConfig c : configs) {
            configmap.put(c.getConfigKey(), c.getConfigValue());
        }
        
        return configmap;
    }
    
    /**
     * 指定するキーに対応する設定値を探す。
     * 
     * @param configKey 検索対象の設定キー
     * @return キーに対応する設定値
     */
    public String getConfig(String configKey) {
        SystemConfig il = sysConfigRepos.findByConfigKey(configKey);
        
        return il == null ? "" : il.getConfigValue();
    }

    static class SysConfigSpecifications {
        
        static Specification<SystemConfig> containsKey(String configKey) {
            return StringUtils.isEmpty(configKey) ? null : (root, query, cb) -> cb.like(root.get("configKey"), "%" + configKey + "%");
        }
    }

    public SystemPreferenceForm getViewForm() {
        Map<String, String> sysConfigMap = getConfigMap();

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        return binder.convertToEntity(sysConfigMap, SystemPreferenceForm.class);
    }
}
