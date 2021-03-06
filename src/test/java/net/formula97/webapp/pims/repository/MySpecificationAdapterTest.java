package net.formula97.webapp.pims.repository;

import net.formula97.webapp.pims.domain.StatusMaster;
import net.formula97.webapp.pims.domain.SystemConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by f97one on 2016/11/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class MySpecificationAdapterTest {

    @Autowired
    SystemConfigRepository sysConfigRepo;
    @Autowired
    StatusMasterRepository statusMasterRepo;

    @Before
    public void setUp() {
        sysConfigRepo.save(new SystemConfig("RegHoge1", "KeyHoge1"));
        sysConfigRepo.save(new SystemConfig("RegHoge2", "KeyHoge2"));

        sysConfigRepo.save(new SystemConfig("FugaItem1", "FugaItem1"));
        sysConfigRepo.save(new SystemConfig("HogeItem1", "HogeItem1"));
    }

    @After
    public void tearDown() {
        sysConfigRepo.deleteAll();
        sysConfigRepo.save(new SystemConfig("AppTitle", "PIMS Beta"));
    }

    @Test
    public void containsのテスト() throws Exception {
        MySpecificationAdapter<SystemConfig> sysConfigSpecification = new MySpecificationAdapter<>(SystemConfig.class);
        List<SystemConfig> scList1 = sysConfigRepo.findAll(sysConfigSpecification.contains("configKey", "Hoge"));

        assertThat("３件取得できる", scList1.size(), is(3));
        Optional<SystemConfig> scOpt1 = scList1.stream().filter((r) -> r.getConfigKey().equals("RegHoge1")).findFirst();
        assertTrue("RegHoge1が取得できる", scOpt1.isPresent());
        Optional<SystemConfig> scOpt2 = scList1.stream().filter((r) -> r.getConfigKey().equals("RegHoge2")).findFirst();
        assertTrue("RegHoge2が取得できる", scOpt2.isPresent());
        Optional<SystemConfig> scOpt3 = scList1.stream().filter((r) -> r.getConfigKey().equals("HogeItem1")).findFirst();
        assertTrue("HogeItem1が取得できる", scOpt3.isPresent());

        List<SystemConfig> scList2 = sysConfigRepo.findAll(sysConfigSpecification.contains("configKey", "Piyo"));
        assertThat("ヒットするアイテムはない", scList2.size(), is(0));
    }

    @Test
    public void notContainsのテスト() throws Exception {
        MySpecificationAdapter<SystemConfig> sysConfigSpecification = new MySpecificationAdapter<>(SystemConfig.class);
        List<SystemConfig> scList1 = sysConfigRepo.findAll(sysConfigSpecification.notContains("configKey", "Hoge"));

        assertThat("１件取得できる", scList1.size(), is(2));
        Optional<SystemConfig> scOpt1 = scList1.stream().filter((r) -> r.getConfigKey().equals("FugaItem1")).findFirst();
        assertTrue("FugaItem1が取得できる", scOpt1.isPresent());
        Optional<SystemConfig> scOpt3 = scList1.stream().filter((r) -> r.getConfigKey().equals("AppTitle")).findFirst();
        assertTrue("はじめから入っているAppTitleが取得できる", scOpt3.isPresent());
    }

    @Test
    public void eqのテスト() throws Exception {
        MySpecificationAdapter<SystemConfig> sysConfigSpecification = new MySpecificationAdapter<>(SystemConfig.class);
        List<SystemConfig> scList1 = sysConfigRepo.findAll(sysConfigSpecification.eq("configKey", "RegHoge1"));

        assertThat("１件取得できる", scList1.size(), is(1));
        Optional<SystemConfig> scOpt1 = scList1.stream().filter((r) -> r.getConfigKey().equals("RegHoge1")).findFirst();
        assertTrue("RegHoge1が取得できる", scOpt1.isPresent());
    }

    @Test
    public void neqのテスト() throws Exception {
        MySpecificationAdapter<SystemConfig> sysConfigSpecification = new MySpecificationAdapter<>(SystemConfig.class);
        List<SystemConfig> scList1 = sysConfigRepo.findAll(sysConfigSpecification.neq("configKey", "RegHoge1"));

        assertThat("４件取得できる", scList1.size(), is(4));
        Optional<SystemConfig> scOpt1 = scList1.stream().filter((r) -> r.getConfigKey().equals("RegHoge2")).findFirst();
        assertTrue("RegHoge2が取得できる", scOpt1.isPresent());
        Optional<SystemConfig> scOpt2 = scList1.stream().filter((r) -> r.getConfigKey().equals("FugaItem1")).findFirst();
        assertTrue("FugaItem1が取得できる", scOpt2.isPresent());
        Optional<SystemConfig> scOpt3 = scList1.stream().filter((r) -> r.getConfigKey().equals("HogeItem1")).findFirst();
        assertTrue("HogeItem1が取得できる", scOpt3.isPresent());
        Optional<SystemConfig> scOpt4 = scList1.stream().filter((r) -> r.getConfigKey().equals("AppTitle")).findFirst();
        assertTrue("はじめから入っているAppTitleが取得できる", scOpt4.isPresent());
    }

    @Test
    public void startsWithのテスト() throws Exception {
        MySpecificationAdapter<SystemConfig> sysConfigSpecification = new MySpecificationAdapter<>(SystemConfig.class);
        List<SystemConfig> scList1 = sysConfigRepo.findAll(sysConfigSpecification.startsWith("configKey", "Reg"));

        assertThat("２件取得できる", scList1.size(), is(2));
        Optional<SystemConfig> scOpt1 = scList1.stream().filter((r) -> r.getConfigKey().equals("RegHoge1")).findFirst();
        assertTrue("RegHoge1が取得できる", scOpt1.isPresent());
        Optional<SystemConfig> scOpt2 = scList1.stream().filter((r) -> r.getConfigKey().equals("RegHoge2")).findFirst();
        assertTrue("RegHoge2が取得できる", scOpt2.isPresent());
    }

    @Test
    public void endsWithのテスト() throws Exception {
        MySpecificationAdapter<SystemConfig> sysConfigSpecification = new MySpecificationAdapter<>(SystemConfig.class);
        List<SystemConfig> scList1 = sysConfigRepo.findAll(sysConfigSpecification.endsWith("configKey", "2"));

        assertThat("１件取得できる", scList1.size(), is(1));
        Optional<SystemConfig> scOpt1 = scList1.stream().filter((r) -> r.getConfigKey().equals("RegHoge2")).findFirst();
        assertTrue("RegHoge2が取得できる", scOpt1.isPresent());
    }

    @Test
    public void gtのテスト() throws Exception {
        MySpecificationAdapter<StatusMaster> statusMasterSpecificationFactory = new MySpecificationAdapter<>(StatusMaster.class);
        List<StatusMaster> smList1 = statusMasterRepo.findAll(statusMasterSpecificationFactory.gt("statusId", 3));

        assertThat("2件取得できる", smList1.size(), is(2));
//        Optional<StatusMaster> smOpt1 = smList1.stream().filter((r) -> r.getStatusId() == 3).findFirst();
//        assertTrue("「解決」のアイテムが取れる", smOpt1.isPresent());
        Optional<StatusMaster> smOpt2 = smList1.stream().filter((r) -> r.getStatusId() == 4).findFirst();
        assertTrue("「終了」のアイテムが取れる", smOpt2.isPresent());
        Optional<StatusMaster> smOpt3 = smList1.stream().filter((r) -> r.getStatusId() == 5).findFirst();
        assertTrue("「却下」のアイテムが取れる", smOpt3.isPresent());
    }

    @Test
    public void geのテスト() throws Exception {
        MySpecificationAdapter<StatusMaster> statusMasterSpecificationFactory = new MySpecificationAdapter<>(StatusMaster.class);
        List<StatusMaster> smList1 = statusMasterRepo.findAll(statusMasterSpecificationFactory.ge("statusId", 3));

        assertThat("3件取得できる", smList1.size(), is(3));
        Optional<StatusMaster> smOpt1 = smList1.stream().filter((r) -> r.getStatusId() == 3).findFirst();
        assertTrue("「解決」のアイテムが取れる", smOpt1.isPresent());
        Optional<StatusMaster> smOpt2 = smList1.stream().filter((r) -> r.getStatusId() == 4).findFirst();
        assertTrue("「終了」のアイテムが取れる", smOpt2.isPresent());
        Optional<StatusMaster> smOpt3 = smList1.stream().filter((r) -> r.getStatusId() == 5).findFirst();
        assertTrue("「却下」のアイテムが取れる", smOpt3.isPresent());
    }

    @Test
    public void ltのテスト() throws Exception {
        MySpecificationAdapter<StatusMaster> statusMasterSpecificationFactory = new MySpecificationAdapter<>(StatusMaster.class);
        List<StatusMaster> smList1 = statusMasterRepo.findAll(statusMasterSpecificationFactory.lt("statusId", 3));

        assertThat("2件取得できる", smList1.size(), is(2));
        Optional<StatusMaster> smOpt1 = smList1.stream().filter((r) -> r.getStatusId() == 1).findFirst();
        assertTrue("「新規」のアイテムが取れる", smOpt1.isPresent());
        Optional<StatusMaster> smOpt2 = smList1.stream().filter((r) -> r.getStatusId() == 2).findFirst();
        assertTrue("「進行中」のアイテムが取れる", smOpt2.isPresent());
//        Optional<StatusMaster> smOpt3 = smList1.stream().filter((r) -> r.getStatusId() == 3).findFirst();
//        assertTrue("「解決」のアイテムが取れる", smOpt3.isPresent());
    }

    @Test
    public void leのテスト() throws Exception {
        MySpecificationAdapter<StatusMaster> statusMasterSpecificationFactory = new MySpecificationAdapter<>(StatusMaster.class);
        List<StatusMaster> smList1 = statusMasterRepo.findAll(statusMasterSpecificationFactory.le("statusId", 3));

        assertThat("3件取得できる", smList1.size(), is(3));
        Optional<StatusMaster> smOpt1 = smList1.stream().filter((r) -> r.getStatusId() == 1).findFirst();
        assertTrue("「新規」のアイテムが取れる", smOpt1.isPresent());
        Optional<StatusMaster> smOpt2 = smList1.stream().filter((r) -> r.getStatusId() == 2).findFirst();
        assertTrue("「進行中」のアイテムが取れる", smOpt2.isPresent());
        Optional<StatusMaster> smOpt3 = smList1.stream().filter((r) -> r.getStatusId() == 3).findFirst();
        assertTrue("「解決」のアイテムが取れる", smOpt3.isPresent());
    }
}
