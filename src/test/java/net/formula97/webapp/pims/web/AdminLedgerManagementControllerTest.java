package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.web.forms.LedgerDetailForm;
import net.formula97.webapp.pims.web.forms.LedgerRemovalDetailForm;
import net.formula97.webapp.pims.web.forms.LedgerSearchConditionForm;
import net.formula97.webapp.pims.web.forms.RefUserItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by f97one on 17/01/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class AdminLedgerManagementControllerTest extends BaseTestCase {

    @InjectMocks
    private AdminLedgerManagementController mController;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    IssueLedgerRepository issueLedgerRepo;
    @Autowired
    IssueItemsRepository issueItemsRepo;
    @Autowired
    LedgerRefUserRepository ledgerRefUserRepo;
    @Autowired
    UserRepository userRepo;

    private MockMvc mMvcMock;
    private Validator validator;

    private int existingLedgerId;
    private int existingIssueId;

    @Before
    public void setUp() throws Exception {
        if (mMvcMock == null) {
            mMvcMock = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        apiEndpoint = "/admin/ledgerManagement";
        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        Users admin1 = new Users();
        admin1.setUsername("kanrisha1");
        admin1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        admin1.setAuthority("ADMIN");
        admin1.setEnabled(true);
        admin1.setDisplayName("管理者１");
        admin1.setMailAddress("kanrisha1@example.com");
        userRepo.save(admin1);

        Users user1 = new Users();
        user1.setUsername("user1");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setAuthority("USER");
        user1.setEnabled(true);
        user1.setDisplayName("ユーザー１");
        user1.setMailAddress("user1@example.com");
        userRepo.save(user1);

        Users disabledUser = new Users();
        disabledUser.setUsername("disabled1");
        disabledUser.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        disabledUser.setAuthority("USER");
        disabledUser.setEnabled(false);
        disabledUser.setDisplayName("無効ユーザー1");
        disabledUser.setMailAddress("disabled1@example.com");
        userRepo.save(disabledUser);

        IssueLedger ledger = new IssueLedger();
        ledger.setLedgerName("非公開台帳１");
        ledger.setPublicLedger(false);
        ledger.setOpenStatus(1);
        issueLedgerRepo.save(ledger);

        MySpecificationAdapter<IssueLedger> issueLedgerSpec = new MySpecificationAdapter<>(IssueLedger.class);
        IssueLedger savedLedger = issueLedgerRepo.findOne(Specification.where(issueLedgerSpec.eq("ledgerName", "非公開台帳１"))).get();
        this.existingLedgerId = savedLedger.getLedgerId();

        IssueLedger ledger1 = new IssueLedger();
        ledger1.setLedgerName("公開台帳1");
        ledger1.setPublicLedger(true);
        ledger1.setOpenStatus(2);
        issueLedgerRepo.save(ledger1);

        IssueLedger ledger2 = new IssueLedger();
        ledger2.setLedgerName("Ledger1");
        ledger2.setPublicLedger(true);
        ledger2.setOpenStatus(3);
        issueLedgerRepo.save(ledger2);

        LedgerRefUser ledgerRefUser = new LedgerRefUser(this.existingLedgerId, user1.getUsername());
        ledgerRefUserRepo.save(ledgerRefUser);

        IssueItems items = new IssueItems();
        items.setActionStatusId(1);
        items.setSevereLevelId(2);
        items.setLedgerId(this.existingLedgerId);
        items.setFoundUser("user1");
        items.setFoundDate(new Date());
        items.setCategoryId(3);
        items.setFoundProcessId(3);
        items.setIssueDetail("Hoge処理が動かない。なんとかしろ");
        items.setRowUpdatedAt(new Date());
        issueItemsRepo.save(items);

    }

    @After
    public void tearDown() throws Exception {
        ledgerRefUserRepo.deleteAll();
        userRepo.deleteAll();
        issueLedgerRepo.deleteAll();
    }

    @Test
    public void 台帳検索Formにバリデーションが働いている() throws Exception {
        LedgerSearchConditionForm form = new LedgerSearchConditionForm();
        form.setLedgerName("1234567890123456789012345678901234567890123456789012345678901234");
        List<Integer> statusList = new ArrayList<>();
        form.setLedgerStatus(statusList);

        Set<ConstraintViolation<LedgerSearchConditionForm>> violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        Optional<ConstraintViolation<LedgerSearchConditionForm>> violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof NotNull).findFirst();
        assertThat("NotNullエラーが検知されている", violationOptional.isPresent(), is(true));

        form.setLedgerName(form.getLedgerName() + "5");
        form.setPublicStatus(1);
        violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof Size).findFirst();
        assertThat("Sizeエラーが検知されている", violationOptional.isPresent(), is(true));

        form.setLedgerName("1234567890123456789012345678901234567890123456789012345678901234");
        form.setPublicStatus(0);
        violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof Min).findFirst();
        assertThat("Minエラーが検知されている", violationOptional.isPresent(), is(true));

        form.setPublicStatus(4);
        violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof Max).findFirst();
        assertThat("Maxエラーが検知されている", violationOptional.isPresent(), is(true));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと台帳検索画面を表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(apiEndpoint)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection()).andReturn();
    }

    @Test
    @WithMockUser(username = "user11", roles = "USER")
    public void 一般ユーザーだと台帳検索画面を表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(apiEndpoint)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 管理者ユーザーなら台帳検索画面を表示できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(apiEndpoint)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/admin/ledger_list")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        Map<Integer, String> statusMap = (Map<Integer, String>) modelMap.get("statusMap");

        assertThat(statusMap.size(), is(3));

        assertThat(statusMap.containsValue("公開"), is(true));
        assertThat(statusMap.containsValue("ブロック中"), is(true));
        assertThat(statusMap.containsValue("終了"), is(true));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 台帳名で検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "公開台帳")
                .param("ledgerStatus", "")
                .param("publicStatus", "3"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        boolean searchExecuted = (boolean) modelMap.get("searchExecuted");
        assertTrue("検索実行フラグが立っている", searchExecuted);

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("２件ヒットしている", ledgerList.size(), is(2));

        Optional<IssueLedger> issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("非公開台帳１")).findFirst();
        assertThat("非公開台帳１が見つかっている", issueLedgerOpt.isPresent(), is(true));
        issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("公開台帳1")).findFirst();
        assertThat("公開台帳1が見つかっている", issueLedgerOpt.isPresent(), is(true));

        Map<Integer, String> statusMap = (Map<Integer, String>) modelMap.get("statusMap");

        assertThat(statusMap.size(), is(3));

        assertThat(statusMap.containsValue("公開"), is(true));
        assertThat(statusMap.containsValue("ブロック中"), is(true));
        assertThat(statusMap.containsValue("終了"), is(true));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 公開台帳だけ検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "")
                .param("ledgerStatus", "")
                .param("publicStatus", "1"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        boolean searchExecuted = (boolean) modelMap.get("searchExecuted");
        assertTrue("検索実行フラグが立っている", searchExecuted);

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("２件ヒットしている", ledgerList.size(), is(2));

        Optional<IssueLedger> issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("公開台帳1")).findFirst();
        assertThat("公開台帳1が見つかっている", issueLedgerOpt.isPresent(), is(true));
        issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("Ledger1")).findFirst();
        assertThat("Ledger1が見つかっている", issueLedgerOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 非公開台帳だけを検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "")
                .param("ledgerStatus", "")
                .param("publicStatus", "2"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        boolean searchExecuted = (boolean) modelMap.get("searchExecuted");
        assertTrue("検索実行フラグが立っている", searchExecuted);

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("１件ヒットしている", ledgerList.size(), is(1));
        assertThat("非公開台帳１が見つかっている", ledgerList.get(0).getLedgerName(), is("非公開台帳１"));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void ステータスで検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "")
                .param("ledgerStatus", "1", "2")
                .param("publicStatus", "3"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        boolean searchExecuted = (boolean) modelMap.get("searchExecuted");
        assertTrue("検索実行フラグが立っている", searchExecuted);

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("2件ヒットしている", ledgerList.size(), is(2));
        Optional<IssueLedger> issueLedgerOpt = ledgerList.stream().filter(r -> r.getOpenStatus() == 1).findFirst();
        assertThat("ステータス1の台帳が見つかっている", issueLedgerOpt.isPresent(), is(true));
        issueLedgerOpt = ledgerList.stream().filter(r -> r.getOpenStatus() == 2).findFirst();
        assertThat("ステータス2の台帳が見つかっている", issueLedgerOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 条件に外れた検索では結果が表示されない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "だいちょう")
                .param("ledgerStatus", "4", "5")
                .param("publicStatus", "3"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        boolean searchExecuted = (boolean) modelMap.get("searchExecuted");
        assertTrue("検索実行フラグが立っている", searchExecuted);

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("0件ヒットしている", ledgerList.size(), is(0));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインでは台帳詳細画面を表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        actions.andExpect(status().is3xxRedirection()).andReturn();
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void 一般ユーザーだと台帳詳細画面を表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        actions.andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 存在しない台帳の詳細は表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d", apiEndpoint, existingLedgerId + 10);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk()).andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 管理者ユーザーなら台帳詳細画面を表示できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk()).andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        LedgerDetailForm ledgerDetailForm = (LedgerDetailForm) modelMap.get("ledgerDetailForm");
        IssueLedger actualLedger = ledgerDetailForm.exportLedger();
        assertThat("台帳名は非公開台帳１", actualLedger.getLedgerName(), is("非公開台帳１"));
        assertThat("非公開台帳扱い", actualLedger.getPublicLedger(), is(false));

        List<RefUserItem> actualRefUserItems = ledgerDetailForm.getRefUserItemList();
        assertThat("有効ユーザーすべてが取得できている", actualRefUserItems.size(), is(2));

        Optional<RefUserItem> refUserItemOpt = actualRefUserItems.stream().filter(r -> r.getUserId().equals("kanrisha1")).findFirst();
        assertThat("kanrisha1を含む", refUserItemOpt.isPresent(), is(true));
        assertThat("台帳に含まれない", refUserItemOpt.get().getUserJoined(), is(false));

        refUserItemOpt = actualRefUserItems.stream().filter(r -> r.getUserId().equals("user1")).findFirst();
        assertThat("user1を含む", refUserItemOpt.isPresent(), is(true));
        assertThat("台帳に含まれる", refUserItemOpt.get().getUserJoined(), is(true));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと台帳詳細を更新できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(post(template)
                    .with(csrf())
                .param("updateItemBtn", "更新")
                .param("ledgerId", String.valueOf(existingLedgerId))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "checked")
                ).andDo(print());
        actions.andExpect(status().is3xxRedirection()).andReturn();

    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void 一般ユーザーだと台帳詳細を更新できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("ledgerId", String.valueOf(existingLedgerId))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "checked")
        ).andDo(print());
        actions.andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 存在しない台帳は更新できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId + 10);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("ledgerId", String.valueOf(existingLedgerId + 10))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("refUserItemList[0].userJoined", "false")
                .param("refUserItemList[0].userId", "kanrisha1")
                .param("refUserItemList[0].displayName", "管理者１")
                .param("refUserItemList[1].userJoined", "true")
                .param("refUserItemList[1].userId", "user1")
                .param("refUserItemList[1].displayName", "ユーザー１")
            ).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk()).andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 台帳詳細を更新できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("ledgerId", String.valueOf(existingLedgerId))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("refUserItemList[0].userJoined", "false")
                .param("refUserItemList[0].userId", "kanrisha1")
                .param("refUserItemList[0].displayName", "管理者１")
                .param("refUserItemList[1].userJoined", "true")
                .param("refUserItemList[1].userId", "user1")
                .param("refUserItemList[1].displayName", "ユーザー１")
            ).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk()).andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        String infoMsg = (String) modelMap.get("infoMsg");
        assertThat(infoMsg, is("台帳を更新しました。"));

        IssueLedger actualLedger = issueLedgerRepo.findById(existingLedgerId).get();

        assertThat("台帳名は非公開台帳１−１", actualLedger.getLedgerName(), is("非公開台帳１−１"));
        assertThat("公開台帳扱い", actualLedger.getPublicLedger(), is(true));

        LedgerDetailForm ledgerDetailForm = (LedgerDetailForm) modelMap.get("ledgerDetailForm");
        List<RefUserItem> actualRefUserItems = ledgerDetailForm.getRefUserItemList();
        assertThat("有効ユーザーすべてが取得できている", actualRefUserItems.size(), is(2));

        Optional<RefUserItem> refUserItemOpt = actualRefUserItems.stream().filter(r -> r.getUserId().equals("kanrisha1")).findFirst();
        assertThat("kanrisha1を含む", refUserItemOpt.isPresent(), is(true));
        assertThat("台帳に含まれない", refUserItemOpt.get().getUserJoined(), is(false));

        refUserItemOpt = actualRefUserItems.stream().filter(r -> r.getUserId().equals("user1")).findFirst();
        assertThat("user1を含む", refUserItemOpt.isPresent(), is(true));
        assertThat("台帳に含まれる", refUserItemOpt.get().getUserJoined(), is(true));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインでは台帳参加ユーザーを編集できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("registerRefUserBtn", "登録")
                .param("ledgerId", String.valueOf(existingLedgerId))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("refUserItemList[0].userJoined", "true")
                .param("refUserItemList[0].userId", "kanrisha1")
                .param("refUserItemList[0].displayName", "管理者１")
                .param("refUserItemList[1].userJoined", "false")
                .param("refUserItemList[1].userId", "user1")
                .param("refUserItemList[1].displayName", "ユーザー１")
        ).andDo(print());

        actions.andExpect(status().is3xxRedirection()).andReturn();

    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void 一般ユーザーだと台帳参加ユーザーを編集できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("registerRefUserBtn", "登録")
                .param("ledgerId", String.valueOf(existingLedgerId))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("refUserItemList[0].userJoined", "true")
                .param("refUserItemList[0].userId", "kanrisha1")
                .param("refUserItemList[0].displayName", "管理者１")
                .param("refUserItemList[1].userJoined", "false")
                .param("refUserItemList[1].userId", "user1")
                .param("refUserItemList[1].displayName", "ユーザー１")
        ).andDo(print());

        actions.andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 存在しない台帳の参加ユーザーは編集できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId + 10);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("registerRefUserBtn", "登録")
                .param("ledgerId", String.valueOf(existingLedgerId + 10))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("refUserItemList[0].userJoined", "true")
                .param("refUserItemList[0].userId", "kanrisha1")
                .param("refUserItemList[0].displayName", "管理者１")
                .param("refUserItemList[1].userJoined", "false")
                .param("refUserItemList[1].userId", "user1")
                .param("refUserItemList[1].displayName", "ユーザー１")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk()).andReturn();
        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));

        MySpecificationAdapter<LedgerRefUser> refUserMySpec = new MySpecificationAdapter<>(LedgerRefUser.class);
        List<LedgerRefUser> existingparticipants = ledgerRefUserRepo.findAll(Specification.where(refUserMySpec.eq("ledgerId", existingLedgerId)));

        Optional<LedgerRefUser> kanrisha1LRUOpt = existingparticipants.stream().filter(r -> r.getUserId().equals("kanrisha1")).findFirst();
        assertThat("kanrisha1は非参加のまま", kanrisha1LRUOpt.isPresent(), is(false));

        Optional<LedgerRefUser> user1LRUOpt = existingparticipants.stream().filter(r -> r.getUserId().equals("user1")).findFirst();
        assertThat("user1は参加のまま", user1LRUOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 管理者ユーザーなら台帳参加ユーザーを編集できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/%d", apiEndpoint, existingLedgerId);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("registerRefUserBtn", "登録")
                .param("ledgerId", String.valueOf(existingLedgerId))
                .param("ledgerName", "非公開台帳１−１")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("refUserItemList[0].userJoined", "true")
                .param("refUserItemList[0].userId", "kanrisha1")
                .param("refUserItemList[0].displayName", "管理者１")
                .param("refUserItemList[1].userJoined", "false")
                .param("refUserItemList[1].userId", "user1")
                .param("refUserItemList[1].displayName", "ユーザー１")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk()).andReturn();
        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        String infoMsg = (String) modelMap.get("infoMsg");
        assertThat(infoMsg, is("台帳参加ユーザーを更新しました。"));

        MySpecificationAdapter<LedgerRefUser> refUserMySpec = new MySpecificationAdapter<>(LedgerRefUser.class);
        List<LedgerRefUser> existingparticipants = ledgerRefUserRepo.findAll(Specification.where(refUserMySpec.eq("ledgerId", existingLedgerId)));

        Optional<LedgerRefUser> kanrisha1LRUOpt = existingparticipants.stream().filter(r -> r.getUserId().equals("kanrisha1")).findFirst();
        assertThat("kanrisha1は参加", kanrisha1LRUOpt.isPresent(), is(true));

        Optional<LedgerRefUser> user1LRUOpt = existingparticipants.stream().filter(r -> r.getUserId().equals("user1")).findFirst();
        assertThat("user1は非参加", user1LRUOpt.isPresent(), is(false));

        IssueLedger existingLedger = issueLedgerRepo.findById(existingLedgerId).get();
        assertThat("台帳名は非公開台帳１のまま", existingLedger.getLedgerName(), is("非公開台帳１"));
        assertThat("公開範囲は非公開のまま", existingLedger.getPublicLedger(), is(false));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 削除ボタンで確認画面を表示する() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/remove/%d", apiEndpoint, existingLedgerId);

        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/admin/ledger_removal"))).andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        LedgerRemovalDetailForm frm = (LedgerRemovalDetailForm) modelMap.get("ledgerRemovalDetailForm");

        assertThat(frm.getLedgerName(), is("非公開台帳１"));
        assertThat(frm.getPublicLedger(), is(false));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 台帳追加画面を表示できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/add", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/admin/ledger_add")))
                .andReturn();

    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 管理者機能で台帳を追加できる() throws Exception {
        List<IssueLedger> before = issueLedgerRepo.findAll();

        String template = String.format(Locale.getDefault(), "%s/create", apiEndpoint);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("ledgerName", "追加台帳１")
                .param("openStatus", "1")
                .param("publicLedger", "false")
                .param("addLedgerBtn", "追加")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(view().name(is("redirect:/admin/ledgerManagement")))
                .andReturn();

        List<IssueLedger> results = issueLedgerRepo.findAll();
        assertThat("1件増えている", results.size(), is(before.size() + 1));

        MySpecificationAdapter<IssueLedger> specAdapter = new MySpecificationAdapter<>(IssueLedger.class);
        Optional<IssueLedger> ledger = issueLedgerRepo.findOne(Specification.where(specAdapter.eq("ledgerName", "追加台帳１")));
        assertThat("追加した台帳が見つかる", ledger, is(notNullValue()));

        String infoMsg = (String) mvcResult.getFlashMap().get("infoMsg");
        assertThat(infoMsg, is("台帳 追加台帳１ を追加しました。"));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 台帳名を空にすると登録できない() throws Exception {
        List<IssueLedger> before = issueLedgerRepo.findAll();

        String template = String.format(Locale.getDefault(), "%s/create", apiEndpoint);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("ledgerName", "")
                .param("openStatus", "1")
                .param("publicLedger", "false")
                .param("addLedgerBtn", "追加")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/admin/ledger_add")))
                .andReturn();

        List<IssueLedger> results = issueLedgerRepo.findAll();
        assertThat("件数は同じ", results.size(), is(before.size()));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 削除確認画面で台帳を削除できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/remove/%d", apiEndpoint, existingLedgerId);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("removeBtn", "削除")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(view().name(is("redirect:/admin/ledgerManagement")))
                .andReturn();

        String infoMsg = (String) mvcResult.getFlashMap().get("infoMsg");
        assertThat(infoMsg, is("台帳 非公開台帳１ を削除しました。"));

        MySpecificationAdapter<IssueLedger> issueLedgerSpec = new MySpecificationAdapter<>(IssueLedger.class);
        Optional<IssueLedger> ledgerOpt = issueLedgerRepo.findOne(Specification.where(issueLedgerSpec.eq("ledgerName", "非公開台帳１")));
        assertFalse("非公開台帳１は削除されている", ledgerOpt.isPresent());

        MySpecificationAdapter<LedgerRefUser> ledgerRefUserSpec = new MySpecificationAdapter<>(LedgerRefUser.class);
        List<LedgerRefUser> ledgerRefUserList = ledgerRefUserRepo.findAll(Specification.where(ledgerRefUserSpec.eq("ledgerId", existingLedgerId)));
        assertThat(ledgerRefUserList.size(), is(0));

        MySpecificationAdapter<IssueItems> issueItemsSpec = new MySpecificationAdapter<>(IssueItems.class);
        List<IssueItems> issueItemsList = issueItemsRepo.findAll(Specification.where(issueItemsSpec.eq("ledgerId", existingLedgerId)));
        assertThat(issueItemsList.size(), is(0));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void ない台帳を指定しても削除できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/ledgerDetail/remove/%d", apiEndpoint, Integer.MAX_VALUE);

        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("removeBtn", "削除")
        ).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection())
                .andExpect(view().name(is("redirect:/admin/ledgerManagement")))
                .andReturn();

        String infoMsg = (String) mvcResult.getFlashMap().get("errMsg");
        assertThat(infoMsg, is("指定された台帳は見つかりません。"));

        MySpecificationAdapter<IssueLedger> issueLedgerSpec = new MySpecificationAdapter<>(IssueLedger.class);
        Optional<IssueLedger> ledgerOpt = issueLedgerRepo.findOne(Specification.where(issueLedgerSpec.eq("ledgerName", "非公開台帳１")));
        assertTrue("非公開台帳１は残っている", ledgerOpt.isPresent());

        MySpecificationAdapter<LedgerRefUser> ledgerRefUserSpec = new MySpecificationAdapter<>(LedgerRefUser.class);
        List<LedgerRefUser> ledgerRefUserList = ledgerRefUserRepo.findAll(Specification.where(ledgerRefUserSpec.eq("ledgerId", existingLedgerId)));
        assertThat(ledgerRefUserList.size(), is(1));

        MySpecificationAdapter<IssueItems> issueItemsSpec = new MySpecificationAdapter<>(IssueItems.class);
        List<IssueItems> issueItemsList = issueItemsRepo.findAll(Specification.where(issueItemsSpec.eq("ledgerId", existingLedgerId)));
        assertThat(issueItemsList.size(), is(1));
    }
}