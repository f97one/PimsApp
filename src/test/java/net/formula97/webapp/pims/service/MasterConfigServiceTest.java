package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import net.formula97.webapp.pims.repository.CategoryMasterRepository;
import net.formula97.webapp.pims.repository.ProcessMasterRepository;
import net.formula97.webapp.pims.repository.SevereLevelMasterRepository;
import net.formula97.webapp.pims.repository.StatusMasterRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class MasterConfigServiceTest {

    @Autowired
    private CategoryMasterRepository categoryMasterRepo;
    @Autowired
    private ProcessMasterRepository processMasterRepo;
    @Autowired
    private SevereLevelMasterRepository severeLevelMasterRepo;
    @Autowired
    private StatusMasterRepository statusMasterRepo;
    @Autowired
    private MasterConfigService masterConfigService;

    @Before
    public void setUp() {
        // デフォのマスタを投入
        //   カテゴリー
        resetCategory();

        //   工程
        resetProcess();

        //   緊急度
        resetSevereLevel();

        //   ステータス
        resetStatus();
    }

    private void resetStatus() {
        StatusMaster stm1 = new StatusMaster(1, "新規", 0, false);
        statusMasterRepo.save(stm1);

        StatusMaster stm2 = new StatusMaster(2, "進行中", 1, false);
        statusMasterRepo.save(stm2);

        StatusMaster stm3 = new StatusMaster(3, "解決", 2, false);
        statusMasterRepo.save(stm3);

        StatusMaster stm4 = new StatusMaster(4, "終了", 3, true);
        statusMasterRepo.save(stm4);

        StatusMaster stm5 = new StatusMaster(5, "却下", 4, true);
        statusMasterRepo.save(stm5);
    }

    private void resetSevereLevel() {
        SevereLevelMaster slm1 = new SevereLevelMaster(1, "低", 0);
        severeLevelMasterRepo.save(slm1);

        SevereLevelMaster slm2 = new SevereLevelMaster(2, "中", 1);
        severeLevelMasterRepo.save(slm2);

        SevereLevelMaster slm3 = new SevereLevelMaster(3, "高", 2);
        severeLevelMasterRepo.save(slm3);

        SevereLevelMaster slm4 = new SevereLevelMaster(4, "非常に高", 3);
        severeLevelMasterRepo.save(slm4);
    }

    private void resetProcess() {
        ProcessMaster pm1 = new ProcessMaster(1, "基本設計", 0);
        processMasterRepo.save(pm1);

        ProcessMaster pm2 = new ProcessMaster(2, "詳細設計", 1);
        processMasterRepo.save(pm2);

        ProcessMaster pm3 = new ProcessMaster(3, "PG", 2);
        processMasterRepo.save(pm3);

        ProcessMaster pm4 = new ProcessMaster(4, "単体テスト", 3);
        processMasterRepo.save(pm4);

        ProcessMaster pm5 = new ProcessMaster(5, "結合テスト", 4);
        processMasterRepo.save(pm5);

        ProcessMaster pm6 = new ProcessMaster(6, "受入テスト", 5);
        processMasterRepo.save(pm6);
    }

    private void resetCategory() {
        CategoryMaster cm1 = new CategoryMaster(1, "Webアプリケーション", 0);
        categoryMasterRepo.save(cm1);

        CategoryMaster cm2 = new CategoryMaster(2, "Web API", 1);
        categoryMasterRepo.save(cm2);

        CategoryMaster cm3 = new CategoryMaster(3, "端末用フロントエンド", 2);
        categoryMasterRepo.save(cm3);

        CategoryMaster cm4 = new CategoryMaster(4, "バッチ", 3);
        categoryMasterRepo.save(cm4);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void マスタータイプに空を渡すとIllegalArgumentExceptionを投げる() {
        try {
            List<MasterDomain> results = masterConfigService.getWholeMasterByType("");

            fail("例外は発生しなかった。");
        } catch (Exception e) {
            assertThat("IllegalArgumentExceptionが発生している", e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void マスタータイプにnullを渡すとIllegalArgumentExceptionを投げる() {
        try {
            List<MasterDomain> results = masterConfigService.getWholeMasterByType(null);

            fail("例外は発生しなかった。");
        } catch (Exception e) {
            assertThat("IllegalArgumentExceptionが発生している", e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void マスタータイプに半角スペースを渡すとIllegalArgumentExceptionを投げる() {
        try {
            List<MasterDomain> results = masterConfigService.getWholeMasterByType(" ");

            fail("例外は発生しなかった。");
        } catch (Exception e) {
            assertThat("IllegalArgumentExceptionが発生している", e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void マスタータイプにcategoryを渡すとカテゴリーマスタのリストを取得する() {
        List<MasterDomain> masterDomainList = masterConfigService.getWholeMasterByType(MasterConfigService.MASTER_TYPE_CATEGORY);

        for (MasterDomain md : masterDomainList) {
            assertThat(md, is(instanceOf(CategoryMaster.class)));
        }

        assertThat(masterDomainList.size(), is(4));
        CategoryMaster cm1 = (CategoryMaster) masterDomainList.get(0);
        assertThat(cm1.getDispOrder(), is(0));

        CategoryMaster cm2 = (CategoryMaster) masterDomainList.get(1);
        assertThat(cm2.getDispOrder(), is(1));

        CategoryMaster cm3 = (CategoryMaster) masterDomainList.get(2);
        assertThat(cm3.getDispOrder(), is(2));

        CategoryMaster cm4 = (CategoryMaster) masterDomainList.get(3);
        assertThat(cm4.getDispOrder(), is(3));

    }

    @Test
    public void マスタータイプにseverelevelを渡すと緊急度マスタのリストを取得する() {
        List<MasterDomain> masterDomainList = masterConfigService.getWholeMasterByType(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL);

        for (MasterDomain md : masterDomainList) {
            assertThat(md, is(instanceOf(SevereLevelMaster.class)));
        }

        assertThat(masterDomainList.size(), is(4));

        SevereLevelMaster slm1 = (SevereLevelMaster) masterDomainList.get(0);
        assertThat(slm1.getDispOrder(), is(0));

        SevereLevelMaster slm2 = (SevereLevelMaster) masterDomainList.get(1);
        assertThat(slm2.getDispOrder(), is(1));

        SevereLevelMaster slm3 = (SevereLevelMaster) masterDomainList.get(2);
        assertThat(slm3.getDispOrder(), is(2));

        SevereLevelMaster slm4 = (SevereLevelMaster) masterDomainList.get(3);
        assertThat(slm4.getDispOrder(), is(3));

    }

    @Test
    public void マスタータイプにprocessを渡すと工程マスタのリストを取得する() {
        List<MasterDomain> masterDomainList = masterConfigService.getWholeMasterByType(MasterConfigService.MASTER_TYPE_PROCESS);

        for (MasterDomain md : masterDomainList) {
            assertThat(md, is(instanceOf(ProcessMaster.class)));
        }

        assertThat(masterDomainList.size(), is(6));

        ProcessMaster pm1 = (ProcessMaster) masterDomainList.get(0);
        assertThat(pm1.getDispOrder(), is(0));

        ProcessMaster pm2 = (ProcessMaster) masterDomainList.get(1);
        assertThat(pm2.getDispOrder(), is(1));

        ProcessMaster pm3 = (ProcessMaster) masterDomainList.get(2);
        assertThat(pm3.getDispOrder(), is(2));

        ProcessMaster pm4 = (ProcessMaster) masterDomainList.get(3);
        assertThat(pm4.getDispOrder(), is(3));

        ProcessMaster pm5 = (ProcessMaster) masterDomainList.get(4);
        assertThat(pm5.getDispOrder(), is(4));

        ProcessMaster pm6 = (ProcessMaster) masterDomainList.get(5);
        assertThat(pm6.getDispOrder(), is(5));
    }

    @Test
    public void マスタータイプにstatusを渡すとステータスマスタのリストを取得する() {
        List<MasterDomain> masterDomainList = masterConfigService.getWholeMasterByType(MasterConfigService.MASTER_TYPE_STATUS);

        for (MasterDomain md : masterDomainList) {
            assertThat(md, is(instanceOf(StatusMaster.class)));
        }

        assertThat(masterDomainList.size(), is(5));

        StatusMaster sm1 = (StatusMaster) masterDomainList.get(0);
        assertThat(sm1.getDispOrder(), is(0));
        assertThat(sm1.getTreatAsFinished(), is(false));

        StatusMaster sm2 = (StatusMaster) masterDomainList.get(1);
        assertThat(sm2.getDispOrder(), is(1));
        assertThat(sm2.getTreatAsFinished(), is(false));

        StatusMaster sm3 = (StatusMaster) masterDomainList.get(2);
        assertThat(sm3.getDispOrder(), is(2));
        assertThat(sm3.getTreatAsFinished(), is(false));

        StatusMaster sm4 = (StatusMaster) masterDomainList.get(3);
        assertThat(sm4.getDispOrder(), is(3));
        assertThat(sm4.getTreatAsFinished(), is(true));

        StatusMaster sm5 = (StatusMaster) masterDomainList.get(4);
        assertThat(sm5.getDispOrder(), is(4));
        assertThat(sm5.getTreatAsFinished(), is(true));
    }

    @Test
    public void マスタータイプにあを渡すとIllegalArgumentExceptionを投げる() {
        try {
            List<MasterDomain> results = masterConfigService.getWholeMasterByType("あ");

            fail("例外は発生しなかった。");
        } catch (Exception e) {
            assertThat("IllegalArgumentExceptionが発生している", e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }
}