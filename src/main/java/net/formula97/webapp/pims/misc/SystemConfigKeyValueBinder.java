package net.formula97.webapp.pims.misc;

import net.formula97.webapp.pims.domain.SystemConfig;
import net.formula97.webapp.pims.misc.annotations.ConfigKey;
import net.formula97.webapp.pims.web.forms.PreferenceForm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by f97one on 2017/06/25.
 */
public class SystemConfigKeyValueBinder {

    public SystemConfigKeyValueBinder() {
    }

    /**
     * Formの内容をエンティティのコレクションに格納する。
     *
     * @param form 入力に使うForm
     * @return Formの値をエンティティのコレクションにしたもの
     * @throws IllegalArgumentException エンティティの ConfigKey アノテーションに重複した値を設定しているとき
     */
    public List<SystemConfig> exportToList(PreferenceForm form) {
        List<SystemConfig> ret = new ArrayList<>();

        Field[] fields = form.getClass().getDeclaredFields();

        for (Field f : fields) {
            f.setAccessible(true);
            ConfigKey configKey = f.getAnnotation(ConfigKey.class);

            if (hasConfigKey(ret, configKey.value())) {
//                throw new IllegalArgumentException("Configuration Key annotations duplicated.");
            } else {
                try {
                    SystemConfig sysConfItem = new SystemConfig(configKey.value(), (String) f.get(form));
                    ret.add(sysConfItem);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * Formの値をMapに格納する。
     *
     * @param form 入力に使うForm
     * @return Formの値をMapにしたもの
     * @throws IllegalArgumentException エンティティの ConfigKey アノテーションに重複した値を設定しているとき
     */
    public Map<String, String> exportToMap(PreferenceForm form) {

        return null;
    }

    /**
     * エンティティのコレクションに格納されている設定値をFormに格納する。
     *
     * @param entityList 設定値を格納しているエンティティのコレクション
     * @return 画面用Form
     */
    public PreferenceForm convertToEntity(List<SystemConfig> entityList) {

        return null;
    }

    /**
     * Mapに格納されている設定値をFormに格納する。
     *
     * @param entityMap 設定値を格納しているMap
     * @return 画面用Form
     */
    public PreferenceForm convertToEntity(Map<String, String> entityMap) {

        return null;
    }

    private boolean hasConfigKey(List<SystemConfig> systemConfigList, String configKey) {
        Optional<SystemConfig> sysConfigOpt = systemConfigList.stream().filter(r -> r.getConfigKey().equals(configKey)).findFirst();
        return sysConfigOpt.isPresent();
    }
}
