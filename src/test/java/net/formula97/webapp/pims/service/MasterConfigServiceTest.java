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

import java.util.ArrayList;
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

    @Test
    public void 更新時にマスタータイプにnullを渡すと例外を投げる() {
        List<MasterDomain> mdl = new ArrayList<>();
        mdl.add(new CategoryMaster(1, "Webアプリケーション", 0));
        mdl.add(new CategoryMaster(2, "Web API", 1));

        try {
            masterConfigService.updateDisplayOrder(null, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void 更新時にマスタータイプに空文字を渡すと例外を投げる() {
        List<MasterDomain> mdl = new ArrayList<>();
        mdl.add(new CategoryMaster(1, "Webアプリケーション", 0));
        mdl.add(new CategoryMaster(2, "Web API", 1));

        try {
            masterConfigService.updateDisplayOrder("", mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void 更新時にマスタータイプにあを渡すと例外を投げる() {
        List<MasterDomain> mdl = new ArrayList<>();
        mdl.add(new CategoryMaster(1, "Webアプリケーション", 0));
        mdl.add(new CategoryMaster(2, "Web API", 1));

        try {
            masterConfigService.updateDisplayOrder("あ", mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void マスタータイプcategoryとdomainの型が一致しない場合例外を投げる() {
        List<MasterDomain> mdl = new ArrayList<>();

        // 2個目をProcessMasterにする
        mdl.add(new CategoryMaster(1, "Webアプリケーション", 0));
        mdl.add(new ProcessMaster(2, "詳細設計", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_CATEGORY, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type ProcessMaster was pass."));
        }

        // 2個目をSevereLevelMasterにする
        mdl.remove(1);
        mdl.add(new SevereLevelMaster(2, "中", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_CATEGORY, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type SevereLevelMaster was pass."));
        }

        // 2個目をStatusMasterにする
        mdl.remove(1);
        mdl.add(new StatusMaster(2, "進行中", 1, false));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_CATEGORY, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type StatusMaster was pass."));
        }

        // 1個目をProcessMasterにする
        mdl.remove(1);
        mdl.add(0, new ProcessMaster(2, "詳細設計", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_CATEGORY, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type ProcessMaster was pass."));
        }
    }

    @Test
    public void マスタータイプprocessとdomainの型が一致しない場合例外を投げる() {
        List<MasterDomain> mdl = new ArrayList<>();

        // 2個目をCategoryMasterにする
        mdl.add(new ProcessMaster(1, "基本設計", 0));
        mdl.add(new CategoryMaster(2, "Web API", 0));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_PROCESS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type CategoryMaster was pass."));
        }

        // 2個目をSevereLevelMasterにする
        mdl.remove(1);
        mdl.add(new SevereLevelMaster(2, "中", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_PROCESS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type SevereLevelMaster was pass."));
        }

        // 2個目をStatusMasterにする
        mdl.remove(1);
        mdl.add(new StatusMaster(2, "進行中", 1, false));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_PROCESS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type StatusMaster was pass."));
        }

        // 1個目をCategoryMasterにする
        mdl.remove(1);
        mdl.add(0, new CategoryMaster(2, "Web API", 0));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_PROCESS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type CategoryMaster was pass."));
        }
    }

    @Test
    public void マスタータイプseverelevelとdomainの型が一致しない場合例外を投げる() {
        List<MasterDomain> mdl = new ArrayList<>();

        // 2個目をProcessMasterにする
        mdl.add(new SevereLevelMaster(1, "低", 0));
        mdl.add(new ProcessMaster(2, "詳細設計", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type ProcessMaster was pass."));
        }

        // 2個目をCategoryMasterにする
        mdl.remove(1);
        mdl.add(new CategoryMaster(2, "Web API", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type CategoryMaster was pass."));
        }

        // 2個目をStatusMasterにする
        mdl.remove(1);
        mdl.add(new StatusMaster(2, "進行中", 1, false));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type StatusMaster was pass."));
        }

        // 1個目をProcessMasterにする
        mdl.remove(1);
        mdl.add(0, new ProcessMaster(2, "詳細設計", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type ProcessMaster was pass."));
        }

    }

    @Test
    public void マスタータイプstatusとdomainの型が一致しない場合例外を投げる() {
        List<MasterDomain> mdl = new ArrayList<>();

        // 2個目をProcessMasterにする
        mdl.add(new StatusMaster(1, "新規", 0, false));
        mdl.add(new ProcessMaster(2, "詳細設計", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_STATUS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type ProcessMaster was pass."));
        }

        // 2個目をCategoryMasterにする
        mdl.remove(1);
        mdl.add(new CategoryMaster(2, "Web API", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_STATUS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type CategoryMaster was pass."));
        }

        // 2個目をSevereLevelMasterにする
        mdl.remove(1);
        mdl.add(new SevereLevelMaster(2, "中", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_STATUS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type SevereLevelMaster was pass."));
        }

        // 1個目をProcessMasterにする
        mdl.remove(1);
        mdl.add(0, new ProcessMaster(2, "詳細設計", 1));

        try {
            masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_STATUS, mdl);
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Incompatible masterType, Type ProcessMaster was pass."));
        }
    }

    @Test
    public void マスタータイプにcategoryを指定した状態で入れ替えられた表示順を保存できる() {
        List<MasterDomain> masterDomainList = new ArrayList<>(4);

        masterDomainList.add(new CategoryMaster(1, "Webアプリ", 0));
        masterDomainList.add(new CategoryMaster(2, "Web API", 2));
        masterDomainList.add(new CategoryMaster(3, "端末用フロントエンド", 1));
        masterDomainList.add(new CategoryMaster(4, "バッチ", 3));

        masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_CATEGORY, masterDomainList);

        List<CategoryMaster> afterList = categoryMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(4));

        CategoryMaster cm0 = afterList.get(0);
        assertThat(cm0.getCategoryName(), is("Webアプリ"));
        CategoryMaster cm1 = afterList.get(1);
        assertThat(cm1.getCategoryName(), is("端末用フロントエンド"));
        CategoryMaster cm2 = afterList.get(2);
        assertThat(cm2.getCategoryName(), is("Web API"));
        CategoryMaster cm3 = afterList.get(3);
        assertThat(cm3.getCategoryName(), is("バッチ"));
    }

    @Test
    public void マスタータイプにprocessを指定した状態で入れ替えられた表示順を保存できる() {
        List<MasterDomain> masterDomainList = new ArrayList<>(6);

        masterDomainList.add(new ProcessMaster(1, "基本せっけい", 0));
        masterDomainList.add(new ProcessMaster(3, "PG", 1));
        masterDomainList.add(new ProcessMaster(2, "詳細設計", 2));
        masterDomainList.add(new ProcessMaster(4, "単体テスト", 3));
        masterDomainList.add(new ProcessMaster(5, "結合テスト", 4));
        masterDomainList.add(new ProcessMaster(6, "統合テスト", 5));

        masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_PROCESS, masterDomainList);

        List<ProcessMaster> afterList = processMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(6));

        ProcessMaster pm0 = afterList.get(0);
        assertThat(pm0.getProcessName(), is("基本せっけい"));
        ProcessMaster pm1 = afterList.get(1);
        assertThat(pm1.getProcessName(), is("PG"));
        ProcessMaster pm2 = afterList.get(2);
        assertThat(pm2.getProcessName(), is("詳細設計"));
        ProcessMaster pm3 = afterList.get(3);
        assertThat(pm3.getProcessName(), is("単体テスト"));
        ProcessMaster pm4 = afterList.get(4);
        assertThat(pm4.getProcessName(), is("結合テスト"));
        ProcessMaster pm5 = afterList.get(5);
        assertThat(pm5.getProcessName(), is("統合テスト"));
    }

    @Test
    public void マスタータイプにseverelevelを指定した状態で入れ替えられた表示順を保存できる() {
        List<MasterDomain> masterDomainList = new ArrayList<>(4);

        masterDomainList.add(new SevereLevelMaster(1, "低", 3));
        masterDomainList.add(new SevereLevelMaster(2, "中", 2));
        masterDomainList.add(new SevereLevelMaster(3, "高", 1));
        masterDomainList.add(new SevereLevelMaster(4, "非常に高", 0));

        masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL, masterDomainList);

        List<SevereLevelMaster> slmList = severeLevelMasterRepo.findAllOrderByDispOrder();

        assertThat(slmList.size(), is(4));

        SevereLevelMaster slm0 = slmList.get(0);
        assertThat(slm0.getSevereLevel(), is("非常に高"));
        SevereLevelMaster slm1 = slmList.get(1);
        assertThat(slm1.getSevereLevel(), is("高"));
        SevereLevelMaster slm2 = slmList.get(2);
        assertThat(slm2.getSevereLevel(), is("中"));
        SevereLevelMaster slm3 = slmList.get(3);
        assertThat(slm3.getSevereLevel(), is("低"));
    }

    @Test
    public void マスタータイプにstatusを指定した状態で入れ替えられた表示順を保存できる() {
        List<MasterDomain> masterDomainList = new ArrayList<>(4);

        masterDomainList.add(new StatusMaster(1, "new", 0, false));
        masterDomainList.add(new StatusMaster(2, "work in progress", 1, false));
        masterDomainList.add(new StatusMaster(3, "solved", 2, true));
        masterDomainList.add(new StatusMaster(4, "done", 3, true));
        masterDomainList.add(new StatusMaster(5, "discard", 4, true));

        masterConfigService.updateDisplayOrder(MasterConfigService.MASTER_TYPE_STATUS, masterDomainList);

        List<StatusMaster> smList = statusMasterRepo.findAllOrderByDispOrder();

        assertThat(smList.size(), is(5));

        StatusMaster sm0 = smList.get(0);
        assertThat(sm0.getStatusName(), is("new"));
        assertThat(sm0.getTreatAsFinished(), is(false));

        StatusMaster sm1 = smList.get(1);
        assertThat(sm1.getStatusName(), is("work in progress"));
        assertThat(sm1.getTreatAsFinished(), is(false));

        StatusMaster sm2 = smList.get(2);
        assertThat(sm2.getStatusName(), is("solved"));
        assertThat(sm2.getTreatAsFinished(), is(true));

        StatusMaster sm3 = smList.get(3);
        assertThat(sm3.getStatusName(), is("done"));
        assertThat(sm3.getTreatAsFinished(), is(true));

        StatusMaster sm4 = smList.get(4);
        assertThat(sm4.getStatusName(), is("discard"));
        assertThat(sm4.getTreatAsFinished(), is(true));
    }

    @Test
    public void マスタータイプにcategoryを渡してCategoryMasterのレコードを追加できる() {
        List<CategoryMaster> beforeCmList = categoryMasterRepo.findAllOrderByDispOrder();

        masterConfigService.addMasterByType(MasterConfigService.MASTER_TYPE_CATEGORY, "追加カテゴリー");

        List<CategoryMaster> afterCmList = categoryMasterRepo.findAllOrderByDispOrder();

        assertThat(afterCmList.size(), is(beforeCmList.size() + 1));

        CategoryMaster cm = afterCmList.get(afterCmList.size() - 1);
        assertThat(cm.getCategoryName(), is("追加カテゴリー"));
        assertThat(cm.getDispOrder(), is(afterCmList.size() - 1));

        // 追加したレコードを削除する
        categoryMasterRepo.delete(cm);
    }

    @Test
    public void マスタータイプにprocessを渡してProcessMasterのレコードを追加できる() {
        List<ProcessMaster> beforePmList = processMasterRepo.findAllOrderByDispOrder();

        masterConfigService.addMasterByType(MasterConfigService.MASTER_TYPE_PROCESS, "追加工程");

        List<ProcessMaster> afterPmList = processMasterRepo.findAllOrderByDispOrder();

        assertThat(afterPmList.size(), is(beforePmList.size() + 1));

        ProcessMaster pm = afterPmList.get(afterPmList.size() - 1);
        assertThat(pm.getProcessName(), is("追加工程"));
        assertThat(pm.getDispOrder(), is(afterPmList.size() - 1));

        // 追加したレコードを削除する
        processMasterRepo.delete(pm);
    }

    @Test
    public void マスタータイプにseverelevelを渡してSevereLevelMasterのレコードを追加できる() {
        List<SevereLevelMaster> beforeSlmList = severeLevelMasterRepo.findAllOrderByDispOrder();

        masterConfigService.addMasterByType(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL, "追加緊急度");

        List<SevereLevelMaster> afterSlmList = severeLevelMasterRepo.findAllOrderByDispOrder();

        assertThat(afterSlmList.size(), is(beforeSlmList.size() + 1));

        SevereLevelMaster slm = afterSlmList.get(afterSlmList.size() - 1);
        assertThat(slm.getSevereLevel(), is("追加緊急度"));
        assertThat(slm.getDispOrder(), is(afterSlmList.size() - 1));

        // 追加したレコードを削除する
        severeLevelMasterRepo.delete(slm);
    }

    @Test
    public void マスタータイプにstatusを渡してStatusMasterのレコードを追加できる() {
        List<StatusMaster> beforeSmList = statusMasterRepo.findAllOrderByDispOrder();

        masterConfigService.addMasterByType(MasterConfigService.MASTER_TYPE_STATUS, "追加ステータス", true);

        List<StatusMaster> afterSmList = statusMasterRepo.findAllOrderByDispOrder();

        assertThat(afterSmList.size(), is(beforeSmList.size() + 1));

        StatusMaster sm = afterSmList.get(afterSmList.size() - 1);
        assertThat(sm.getStatusName(), is("追加ステータス"));
        assertThat(sm.getDispOrder(), is(afterSmList.size() - 1));
        assertThat(sm.getTreatAsFinished(), is(true));

        // 追加したレコードを削除する
        statusMasterRepo.delete(sm);
    }

    @Test
    public void マスタータイプにnullを渡すとマスタ追加時に例外を投げる() {
        try {
            masterConfigService.addMasterByType(null, "nullステータス");
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void マスタータイプに空文字を渡すとマスタ追加時に例外を投げる() {
        try {
            masterConfigService.addMasterByType("", "空文字ステータス");
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }

    @Test
    public void マスタータイプにあを渡すとマスタ追加時に例外を投げる() {
        try {
            masterConfigService.addMasterByType("あ", "あステータス");
            fail("例外は投げられなかった");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(IllegalArgumentException.class)));
            assertThat(e.getMessage(), is("Argument masterType must be specified valid value."));
        }
    }
}