package net.formula97.webapp.pims.misc;

import net.formula97.webapp.pims.domain.SystemConfig;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * Created by f97one on 2017/06/25.
 */
public class SystemConfigKeyValueBinderTest {

    @Test
    public void formからListをつくることができる() throws Exception {
        SimDto dto = new SimDto();
        dto.setMember1("メンバー1");
        dto.setMember2("メンバー2");

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        List<SystemConfig> systemConfigList = binder.exportToList(dto);

        assertThat("エントリーは2", systemConfigList.size(), is(2));

        SystemConfig e1 = systemConfigList.get(0);
        assertThat(e1.getConfigKey(), is("Member1"));
        assertThat(e1.getConfigValue(), is("メンバー1"));

        SystemConfig e2 = systemConfigList.get(1);
        assertThat(e2.getConfigKey(), is("Member2"));
        assertThat(e2.getConfigValue(), is("メンバー2"));
    }

    @Test
    public void 重複キーでは例外を投げる1() throws Exception {
        SimDto2 dto = new SimDto2();
        dto.setMember1("メンバー1");
        dto.setMember2("メンバー2");

        try {
            SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
            List<SystemConfig> systemConfigList = binder.exportToList(dto);

            fail("処理が正常終了した、エントリーは " + String.valueOf(systemConfigList.size()));
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }
    }

    @Test
    public void formからMapをつくることができる() throws Exception {
        SimDto dto = new SimDto();
        dto.setMember1("メンバー1");
        dto.setMember2("メンバー2");

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        Map<String, String> sysConfigMap = binder.exportToMap(dto);


        assertThat("エントリーは2", sysConfigMap.size(), is(2));

        assertThat(sysConfigMap.containsKey("Member1"), is(true));
        assertThat(sysConfigMap.get("Member1"), is("メンバー1"));

        assertThat(sysConfigMap.containsKey("Member2"), is(true));
        assertThat(sysConfigMap.get("Member2"), is("メンバー2"));

    }

    @Test
    public void 重複キーでは例外を投げる2() throws Exception {
        SimDto2 dto = new SimDto2();
        dto.setMember1("メンバー1");
        dto.setMember2("メンバー2");

        try {
            SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
            Map<String, String> sysConfigMap = binder.exportToMap(dto);

            fail("処理が正常終了した、エントリーは " + String.valueOf(sysConfigMap.size()));
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }
    }
}