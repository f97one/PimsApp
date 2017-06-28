package net.formula97.webapp.pims.misc;

import net.formula97.webapp.pims.domain.SystemConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
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

    @Test
    public void formのキー設定が不足している場合は無視される1() throws Exception {
        SimDto3 simDto3 = new SimDto3();
        simDto3.setMember1("member1");
        simDto3.setMember2("member2");
        simDto3.setMember3("member3");

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        List<SystemConfig> sysConfigList = binder.exportToList(simDto3);

        assertThat("エントリーは2", sysConfigList.size(), is(2));

        SystemConfig e1 = sysConfigList.get(0);
        assertThat(e1.getConfigKey(), is("Member1"));
        assertThat(e1.getConfigValue(), is("member1"));

        SystemConfig e2 = sysConfigList.get(1);
        assertThat(e2.getConfigKey(), is("Member2"));
        assertThat(e2.getConfigValue(), is("member2"));
    }

    @Test
    public void formのキー設定が不足している場合は無視される2() throws Exception {
        SimDto3 simDto3 = new SimDto3();
        simDto3.setMember1("member1");
        simDto3.setMember2("member2");
        simDto3.setMember3("member3");

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        Map<String, String> sysConfigMap = binder.exportToMap(simDto3);

        assertThat("エントリーは2", sysConfigMap.size(), is(2));

        assertThat(sysConfigMap.containsKey("Member1"), is(true));
        assertThat(sysConfigMap.get("Member1"), is("member1"));

        assertThat(sysConfigMap.containsKey("Member2"), is(true));
        assertThat(sysConfigMap.get("Member2"), is("member2"));
    }

    @Test
    public void エンティティのコレクションからFormに戻せる1() throws Exception {
        List<SystemConfig> sysConfigList = new ArrayList<>();
        SystemConfig item1 = new SystemConfig("Member1", "メンバー１");
        SystemConfig item2 = new SystemConfig("Member2", "メンバー2");
        sysConfigList.add(item1);
        sysConfigList.add(item2);

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        SimDto dto = binder.convertToEntity(sysConfigList, SimDto.class);

        assertThat(dto, is(notNullValue()));

        assertThat(dto.getMember1(), is("メンバー１"));
        assertThat(dto.getMember2(), is("メンバー2"));
    }

    @Test
    public void 設定値のMapからFormに戻せる1() throws Exception {
        Map<String, String> sysConfigMap = new HashMap<>(2);
        sysConfigMap.put("Member1", "メンバー１");
        sysConfigMap.put("Member2", "メンバー2");

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        SimDto dto = binder.convertToEntity(sysConfigMap, SimDto.class);

        assertThat(dto, is(notNullValue()));

        assertThat(dto.getMember1(), is("メンバー１"));
        assertThat(dto.getMember2(), is("メンバー2"));
    }

    @Test
    public void 重複キーを持っていると例外を投げる1() throws Exception {
        List<SystemConfig> sysConfigList = new ArrayList<>();
        SystemConfig item1 = new SystemConfig("Member1", "メンバー１");
        SystemConfig item2 = new SystemConfig("Member2", "メンバー2");
        sysConfigList.add(item1);
        sysConfigList.add(item2);

        try {
            SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
            SimDto2 dto = binder.convertToEntity(sysConfigList, SimDto2.class);

            fail("処理が例外が発生することなく進んだ");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }
    }

    @Test
    public void 重複キーを持っていると例外を投げる2() throws Exception {
        Map<String, String> sysConfigMap = new HashMap<>(2);
        sysConfigMap.put("Member1", "メンバー１");
        sysConfigMap.put("Member2", "メンバー2");

        try {
            SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
            SimDto2 dto = binder.convertToEntity(sysConfigMap, SimDto2.class);

            fail("処理が例外が発生することなく進んだ");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
        }

    }

    @Test
    public void formのキー設定が不足している場合は無視される3() throws Exception {
        List<SystemConfig> sysConfigList = new ArrayList<>();
        SystemConfig item1 = new SystemConfig("Member1", "メンバー１");
        SystemConfig item2 = new SystemConfig("Member2", "メンバー2");
        sysConfigList.add(item1);
        sysConfigList.add(item2);

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        SimDto3 dto = binder.convertToEntity(sysConfigList, SimDto3.class);

        assertThat(dto, is(notNullValue()));

        assertThat(dto.getMember1(), is("メンバー１"));
        assertThat(dto.getMember2(), is("メンバー2"));
        assertThat(dto.getMember3(), is(nullValue()));
    }

    @Test
    public void formのキー設定が不足している場合は無視される4() throws Exception {
        Map<String, String> sysConfigMap = new HashMap<>(2);
        sysConfigMap.put("Member1", "メンバー１");
        sysConfigMap.put("Member2", "メンバー2");

        SystemConfigKeyValueBinder binder = new SystemConfigKeyValueBinder();
        SimDto3 dto = binder.convertToEntity(sysConfigMap, SimDto3.class);

        assertThat(dto, is(notNullValue()));

        assertThat(dto.getMember1(), is("メンバー１"));
        assertThat(dto.getMember2(), is("メンバー2"));
        assertThat(dto.getMember3(), is(nullValue()));
    }
}