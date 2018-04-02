package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.*;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.misc.CommonsStringUtils;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.service.MasterConfigService;
import net.formula97.webapp.pims.web.forms.CurrentItemForm;
import net.formula97.webapp.pims.web.forms.MasterItem;
import net.formula97.webapp.pims.web.forms.NewItemForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class AdminMasterConfigControllerTest extends BaseTestCase {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryMasterRepository categoryMasterRepo;
    @Autowired
    private ProcessMasterRepository processMasterRepo;
    @Autowired
    private SevereLevelMasterRepository severeLevelMasterRepo;
    @Autowired
    private StatusMasterRepository statusMasterRepo;

    private Validator validator;
    private MockMvc mMvcMock;
    private final String urlTemplate = "/admin/master";

    @Before
    public void setUp() throws Exception {
        if (mMvcMock == null) {
            mMvcMock = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        apiEndpoint = String.format(Locale.getDefault(), "http://localhost:%d/admin/master", port);

        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        // 初期状態で存在する管理者ユーザーをとりあえず消す
        Optional<Users> adminOptional = Optional.ofNullable(userRepo.findOne("admin"));
        adminOptional.ifPresent(users -> userRepo.delete("admin"));

        Users user1 = new Users();
        user1.setUsername("user1");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setDisplayName("JUnit test1");
        user1.setMailAddress("test1@example.com");
        user1.setAuthority(AppConstants.ROLE_USER);
        user1.setEnabled(true);
        userRepo.save(user1);

        Users admin1 = new Users();
        admin1.setUsername("kanrisha1");
        admin1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        admin1.setDisplayName("JUnit kanrisha1");
        admin1.setMailAddress("kanrisha1@example.net");
        admin1.setAuthority(AppConstants.ROLE_ADMIN);
        admin1.setEnabled(true);
        userRepo.save(admin1);

        // マスタのリセット
        categoryMasterRepo.deleteAll();
        processMasterRepo.deleteAll();
        severeLevelMasterRepo.deleteAll();
        statusMasterRepo.deleteAll();

        resetCategory();
        resetProcess();
        resetSevereLevel();
        resetStatus();
    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
    }

    /**
     * 指定マスタータイプのURLを作る。
     *
     * @param apiEndpoint 基礎となるAPIエンドポイントURL
     * @param masterType  付加するマスタータイプ
     * @return マスタータイプを合成したAPIエンドポイント
     */
    private String makeUrlByType(String apiEndpoint, String masterType) {
        return String.format(Locale.getDefault(), "%s?masterType=%s", apiEndpoint, masterType);
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

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプcategoryで初期画面を表示する() throws Exception {
        String url = makeUrlByType(urlTemplate, MasterConfigService.MASTER_TYPE_CATEGORY);
        ResultActions actions = mMvcMock.perform(get(url)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andDo(print())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        // 新規追加用Form
        NewItemForm newItemForm = (NewItemForm) modelMap.get("newItemForm");
        assertThat(newItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_CATEGORY));
        assertThat(newItemForm.getItemNameLength(), is(128));
        assertThat(CommonsStringUtils.isNullOrEmpty(newItemForm.getItemName()), is(true));

        // 編集用Form
        CurrentItemForm currentItemForm = (CurrentItemForm) modelMap.get("currentItemForm");
        assertThat(currentItemForm.getItemNameLength(), is(128));
        assertThat(currentItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_CATEGORY));

        List<MasterItem> masterItemList = currentItemForm.getMasterList();
        assertThat(masterItemList.size(), is(4));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプprocessで初期画面を表示する() throws Exception {
        String url = makeUrlByType(urlTemplate, MasterConfigService.MASTER_TYPE_PROCESS);
        ResultActions actions = mMvcMock.perform(get(url)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        // 新規追加用Form
        NewItemForm newItemForm = (NewItemForm) modelMap.get("newItemForm");
        assertThat(newItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_PROCESS));
        assertThat(newItemForm.getItemNameLength(), is(16));
        assertThat(CommonsStringUtils.isNullOrEmpty(newItemForm.getItemName()), is(true));

        // 編集用Form
        CurrentItemForm currentItemForm = (CurrentItemForm) modelMap.get("currentItemForm");
        assertThat(currentItemForm.getItemNameLength(), is(16));
        assertThat(currentItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_PROCESS));

        List<MasterItem> masterItemList = currentItemForm.getMasterList();
        assertThat(masterItemList.size(), is(6));

    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプseverelevelで初期画面を表示する() throws Exception {
        String url = makeUrlByType(urlTemplate, MasterConfigService.MASTER_TYPE_SEVERE_LEVEL);
        ResultActions actions = mMvcMock.perform(get(url)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andDo(print())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        // 新規追加用Form
        NewItemForm newItemForm = (NewItemForm) modelMap.get("newItemForm");
        assertThat(newItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL));
        assertThat(newItemForm.getItemNameLength(), is(8));
        assertThat(CommonsStringUtils.isNullOrEmpty(newItemForm.getItemName()), is(true));

        // 編集用Form
        CurrentItemForm currentItemForm = (CurrentItemForm) modelMap.get("currentItemForm");
        assertThat(currentItemForm.getItemNameLength(), is(8));
        assertThat(currentItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL));

        List<MasterItem> masterItemList = currentItemForm.getMasterList();
        assertThat(masterItemList.size(), is(4));

    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプstatusで初期画面を表示する() throws Exception {
        String url = makeUrlByType(urlTemplate, MasterConfigService.MASTER_TYPE_STATUS);
        ResultActions actions = mMvcMock.perform(get(url)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andDo(print())
                .andDo(print())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        // 新規追加用Form
        NewItemForm newItemForm = (NewItemForm) modelMap.get("newItemForm");
        assertThat(newItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_STATUS));
        assertThat(newItemForm.getItemNameLength(), is(16));
        assertThat(CommonsStringUtils.isNullOrEmpty(newItemForm.getItemName()), is(true));

        // 編集用Form
        CurrentItemForm currentItemForm = (CurrentItemForm) modelMap.get("currentItemForm");
        assertThat(currentItemForm.getItemNameLength(), is(16));
        assertThat(currentItemForm.getMasterType(), is(MasterConfigService.MASTER_TYPE_STATUS));

        List<MasterItem> masterItemList = currentItemForm.getMasterList();
        assertThat(masterItemList.size(), is(5));

        MasterItem mi0 = masterItemList.get(0);
        assertThat(mi0.getTreatAsFinished(), is(false));

        MasterItem mi1 = masterItemList.get(1);
        assertThat(mi1.getTreatAsFinished(), is(false));

        MasterItem mi2 = masterItemList.get(2);
        assertThat(mi2.getTreatAsFinished(), is(false));

        MasterItem mi3 = masterItemList.get(3);
        assertThat(mi3.getTreatAsFinished(), is(true));

        MasterItem mi4 = masterItemList.get(4);
        assertThat(mi4.getTreatAsFinished(), is(true));

    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void 規定外のマスタータイプを指定する場合エラーメッセージを出す() throws Exception {
        String url = makeUrlByType(urlTemplate, "a");
        ResultActions actions = mMvcMock.perform(get(url)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");

        assertThat(errMsg, is("指定されたマスタはありません。"));

        CurrentItemForm currentItemForm = (CurrentItemForm) modelMap.get("currentItemForm");
        List<MasterItem> masterItems = currentItemForm.getMasterList();
        assertThat(masterItems.size(), is(0));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプを空にした場合エラーメッセージを出す() throws Exception {
        String url = makeUrlByType(urlTemplate, "");
        ResultActions actions = mMvcMock.perform(get(url)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");

        assertThat(errMsg, is("指定されたマスタはありません。"));

        CurrentItemForm currentItemForm = (CurrentItemForm) modelMap.get("currentItemForm");
        List<MasterItem> masterItems = currentItemForm.getMasterList();
        assertThat(masterItems.size(), is(0));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプcategoryで1レコード追加できる() throws Exception {
        List<CategoryMaster> beforeList = categoryMasterRepo.findAllOrderByDispOrder();

        String additionalItem = "追加カテゴリー";
        int itemLimit = 128;

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_CATEGORY);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_CATEGORY);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=category")))
                .andReturn();

        List<CategoryMaster> afterList = categoryMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size() + 1));
        Optional<CategoryMaster> latestMi = afterList.stream().max(Comparator.comparing(CategoryMaster::getDispOrder));
        CategoryMaster cm = latestMi.get();
        assertThat(cm.getCategoryName(), is(additionalItem));

        // 追加したデータを消す
        categoryMasterRepo.delete(cm);
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプprocessで1レコード追加できる() throws Exception {
        List<ProcessMaster> beforeList = processMasterRepo.findAllOrderByDispOrder();

        String additionalItem = "追加工程";
        int itemLimit = 16;

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_PROCESS);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_PROCESS);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=process")))
                .andReturn();

        List<ProcessMaster> afterList = processMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size() + 1));
        Optional<ProcessMaster> latestMi = afterList.stream().max(Comparator.comparing(ProcessMaster::getDispOrder));
        ProcessMaster pm = latestMi.get();
        assertThat(pm.getProcessName(), is(additionalItem));

        // 追加したデータを消す
        processMasterRepo.delete(pm);
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプseverelevelで1レコード追加できる() throws Exception {
        List<SevereLevelMaster> beforeList = severeLevelMasterRepo.findAllOrderByDispOrder();

        String additionalItem = "追加緊急度";
        int itemLimit = 8;

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_SEVERE_LEVEL);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=severelevel")))
                .andReturn();

        List<SevereLevelMaster> afterList = severeLevelMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size() + 1));
        Optional<SevereLevelMaster> latestMi = afterList.stream().max(Comparator.comparing(SevereLevelMaster::getDispOrder));
        SevereLevelMaster slm = latestMi.get();
        assertThat(slm.getSevereLevel(), is(additionalItem));

        // 追加したデータを消す
        severeLevelMasterRepo.delete(slm);
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプstatusで1レコード追加できる() throws Exception {
        List<StatusMaster> beforeList = statusMasterRepo.findAllOrderByDispOrder();

        String additionalItem = "追加ステータス";
        int itemLimit = 16;

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_STATUS);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_STATUS);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
                .param("treatAsFinished", "true")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=status")))
                .andReturn();

        List<StatusMaster> afterList = statusMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size() + 1));
        Optional<StatusMaster> latestMi = afterList.stream().max(Comparator.comparing(StatusMaster::getDispOrder));
        StatusMaster sm = latestMi.get();
        assertThat(sm.getStatusName(), is(additionalItem));
        assertThat(sm.getTreatAsFinished(), is(true));

        // 追加したデータを消す
        statusMasterRepo.delete(sm);
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプcategoryで129文字のデータは追加できない() throws Exception {
        List<CategoryMaster> beforeList = categoryMasterRepo.findAllOrderByDispOrder();

        StringBuilder sb = new StringBuilder();
        int itemLimit = 128;

        for (int i = 0; i <= itemLimit; i++) {
            sb.append("あ");
        }

        String additionalItem = sb.toString();

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_CATEGORY);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_CATEGORY);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andReturn();

        List<CategoryMaster> afterList = categoryMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String itemError = (String) modelMap.get("itemError");

        assertThat(itemError, is("カテゴリーは128文字以内で入力してください。"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプprocessで17文字のデータは追加できない() throws Exception {
        List<ProcessMaster> beforeList = processMasterRepo.findAllOrderByDispOrder();

        StringBuilder sb = new StringBuilder();
        int itemLimit = 16;

        for (int i = 0; i <= itemLimit; i++) {
            sb.append("あ");
        }

        String additionalItem = sb.toString();

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_PROCESS);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_PROCESS);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andReturn();

        List<ProcessMaster> afterList = processMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String itemError = (String) modelMap.get("itemError");

        assertThat(itemError, is("工程は16文字以内で入力してください。"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプseverelevelで9文字のデータは追加できない() throws Exception {
        List<SevereLevelMaster> beforeList = severeLevelMasterRepo.findAllOrderByDispOrder();

        StringBuilder sb = new StringBuilder();
        int itemLimit = 8;

        for (int i = 0; i <= itemLimit; i++) {
            sb.append("あ");
        }

        String additionalItem = sb.toString();

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_SEVERE_LEVEL);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_SEVERE_LEVEL);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andReturn();

        List<SevereLevelMaster> afterList = severeLevelMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String itemError = (String) modelMap.get("itemError");

        assertThat(itemError, is("緊急度は8文字以内で入力してください。"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプstatusで17文字のデータは追加できない() throws Exception {
        List<StatusMaster> beforeList = statusMasterRepo.findAllOrderByDispOrder();

        StringBuilder sb = new StringBuilder();
        int itemLimit = 16;

        for (int i = 0; i <= itemLimit; i++) {
            sb.append("あ");
        }

        String additionalItem = sb.toString();

        NewItemForm newItemForm = new NewItemForm();
        newItemForm.setItemName(additionalItem);
        newItemForm.setItemNameLength(itemLimit);
        newItemForm.setMasterType(MasterConfigService.MASTER_TYPE_STATUS);

        String url = makeUrlByType(urlTemplate + "/add", MasterConfigService.MASTER_TYPE_STATUS);
        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("newItemAdd", "追加")
                .param("itemName", additionalItem)
                .param("itemNameLength", String.valueOf(itemLimit))
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/master_config")))
                .andReturn();

        List<StatusMaster> afterList = statusMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String itemError = (String) modelMap.get("itemError");

        assertThat(itemError, is("ステータスは16文字以内で入力してください。"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプcategoryを更新できる() throws Exception {
        List<CategoryMaster> beforeList = categoryMasterRepo.findAllOrderByDispOrder();

        String url = makeUrlByType(urlTemplate + "/edit", MasterConfigService.MASTER_TYPE_CATEGORY);

        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("updateBtn", "更新")
                .param("masterList[0].itemId", "1")
                .param("masterList[0].itemName", "Webアプリケーション")
                .param("masterList[0].displayOrder", "0")
                .param("masterList[1].itemId", "2")
                .param("masterList[1].itemName", "REST API")
                .param("masterList[1].displayOrder", "2")
                .param("masterList[2].itemId", "3")
                .param("masterList[2].itemName", "端末用フロントエンド")
                .param("masterList[2].displayOrder", "1")
                .param("masterList[3].itemId", "4")
                .param("masterList[3].itemName", "バッチ")
                .param("masterList[3].displayOrder", "3")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=category")))
                .andReturn();

        List<CategoryMaster> afterList = categoryMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        CategoryMaster cm1 = afterList.get(0);
        assertThat(cm1.getCategoryId(), is(1));
        assertThat(cm1.getCategoryName(), is("Webアプリケーション"));

        CategoryMaster cm2 = afterList.get(1);
        assertThat(cm2.getCategoryId(), is(3));
        assertThat(cm2.getCategoryName(), is("端末用フロントエンド"));

        CategoryMaster cm3 = afterList.get(2);
        assertThat(cm3.getCategoryId(), is(2));
        assertThat(cm3.getCategoryName(), is("REST API"));

        CategoryMaster cm4 = afterList.get(3);
        assertThat(cm4.getCategoryId(), is(4));
        assertThat(cm4.getCategoryName(), is("バッチ"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプprocessを更新できる() throws Exception {
        List<ProcessMaster> beforeList = processMasterRepo.findAllOrderByDispOrder();

        String url = makeUrlByType(urlTemplate + "/edit", MasterConfigService.MASTER_TYPE_PROCESS);

        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("updateBtn", "更新")
                .param("masterList[0].itemId", "1")
                .param("masterList[0].itemName", "基本設計")
                .param("masterList[0].displayOrder", "0")
                .param("masterList[1].itemId", "2")
                .param("masterList[1].itemName", "詳細設計")
                .param("masterList[1].displayOrder", "2")
                .param("masterList[2].itemId", "3")
                .param("masterList[2].itemName", "PG")
                .param("masterList[2].displayOrder", "1")
                .param("masterList[3].itemId", "4")
                .param("masterList[3].itemName", "単体テスト")
                .param("masterList[3].displayOrder", "3")
                .param("masterList[4].itemId", "5")
                .param("masterList[4].itemName", "結合テスト")
                .param("masterList[4].displayOrder", "5")
                .param("masterList[5].itemId", "6")
                .param("masterList[5].itemName", "受入テスト")
                .param("masterList[5].displayOrder", "4")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=process")))
                .andReturn();

        List<ProcessMaster> afterList = processMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        ProcessMaster pm0 = afterList.get(0);
        assertThat(pm0.getProcessId(), is(1));
        assertThat(pm0.getProcessName(), is("基本設計"));

        ProcessMaster pm1 = afterList.get(1);
        assertThat(pm1.getProcessId(), is(3));
        assertThat(pm1.getProcessName(), is("PG"));

        ProcessMaster pm2 = afterList.get(2);
        assertThat(pm2.getProcessId(), is(2));
        assertThat(pm2.getProcessName(), is("詳細設計"));

        ProcessMaster pm3 = afterList.get(3);
        assertThat(pm3.getProcessId(), is(4));
        assertThat(pm3.getProcessName(), is("単体テスト"));

        ProcessMaster pm4 = afterList.get(4);
        assertThat(pm4.getProcessId(), is(6));
        assertThat(pm4.getProcessName(), is("受入テスト"));

        ProcessMaster pm5 = afterList.get(5);
        assertThat(pm5.getProcessId(), is(5));
        assertThat(pm5.getProcessName(), is("結合テスト"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプseverelevelを更新できる() throws Exception {
        List<SevereLevelMaster> beforeList = severeLevelMasterRepo.findAllOrderByDispOrder();

        String url = makeUrlByType(urlTemplate + "/edit", MasterConfigService.MASTER_TYPE_SEVERE_LEVEL);

        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("updateBtn", "更新")
                .param("masterList[0].itemId", "1")
                .param("masterList[0].itemName", "低")
                .param("masterList[0].displayOrder", "0")
                .param("masterList[1].itemId", "2")
                .param("masterList[1].itemName", "中")
                .param("masterList[1].displayOrder", "2")
                .param("masterList[2].itemId", "4")
                .param("masterList[2].itemName", "とても高い")
                .param("masterList[2].displayOrder", "1")
                .param("masterList[3].itemId", "3")
                .param("masterList[3].itemName", "高")
                .param("masterList[3].displayOrder", "3")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=severelevel")))
                .andReturn();

        List<SevereLevelMaster> afterList = severeLevelMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        SevereLevelMaster slm0 = afterList.get(0);
        assertThat(slm0.getSevereLevelId(), is(1));
        assertThat(slm0.getSevereLevel(), is("低"));

        SevereLevelMaster slm1 = afterList.get(1);
        assertThat(slm1.getSevereLevelId(), is(4));
        assertThat(slm1.getSevereLevel(), is("とても高い"));

        SevereLevelMaster slm2 = afterList.get(2);
        assertThat(slm2.getSevereLevelId(), is(2));
        assertThat(slm2.getSevereLevel(), is("中"));

        SevereLevelMaster slm3 = afterList.get(3);
        assertThat(slm3.getSevereLevelId(), is(3));
        assertThat(slm3.getSevereLevel(), is("高"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void マスタータイプstatusを更新できる() throws Exception {
        List<StatusMaster> beforeList = statusMasterRepo.findAllOrderByDispOrder();

        String url = makeUrlByType(urlTemplate + "/edit", MasterConfigService.MASTER_TYPE_STATUS);

        ResultActions actions = mMvcMock.perform(post(url).with(csrf())
                .param("updateBtn", "更新")
                .param("masterList[0].itemId", "1")
                .param("masterList[0].itemName", "新規")
                .param("masterList[0].displayOrder", "0")
                .param("masterList[0].treatAsFinished", "false")
                .param("masterList[1].itemId", "2")
                .param("masterList[1].itemName", "進行中")
                .param("masterList[1].displayOrder", "2")
                .param("masterList[1].treatAsFinished", "false")
                .param("masterList[2].itemId", "4")
                .param("masterList[2].itemName", "解決")
                .param("masterList[2].displayOrder", "1")
                .param("masterList[2].treatAsFinished", "false")
                .param("masterList[3].itemId", "3")
                .param("masterList[3].itemName", "終了")
                .param("masterList[3].displayOrder", "3")
                .param("masterList[3].treatAsFinished", "true")
                .param("masterList[4].itemId", "5")
                .param("masterList[4].itemName", "却下")
                .param("masterList[4].displayOrder", "4")
                .param("masterList[4].treatAsFinished", "false")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("redirect:/admin/master?masterType=status")))
                .andReturn();

        List<StatusMaster> afterList = statusMasterRepo.findAllOrderByDispOrder();

        assertThat(afterList.size(), is(beforeList.size()));

        StatusMaster sm0 = afterList.get(0);
        assertThat(sm0.getStatusId(), is(1));
        assertThat(sm0.getStatusName(), is("新規"));
        assertThat(sm0.getTreatAsFinished(), is(false));

        StatusMaster sm1 = afterList.get(1);
        assertThat(sm1.getStatusId(), is(4));
        assertThat(sm1.getStatusName(), is("解決"));
        assertThat(sm1.getTreatAsFinished(), is(false));

        StatusMaster sm2 = afterList.get(2);
        assertThat(sm2.getStatusId(), is(2));
        assertThat(sm2.getStatusName(), is("進行中"));
        assertThat(sm2.getTreatAsFinished(), is(false));

        StatusMaster sm3 = afterList.get(3);
        assertThat(sm3.getStatusId(), is(3));
        assertThat(sm3.getStatusName(), is("終了"));
        assertThat(sm3.getTreatAsFinished(), is(true));

        StatusMaster sm4 = afterList.get(4);
        assertThat(sm4.getStatusId(), is(5));
        assertThat(sm4.getStatusName(), is("却下"));
        assertThat(sm4.getTreatAsFinished(), is(false));
    }
}
