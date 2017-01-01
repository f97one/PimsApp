package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import net.formula97.webapp.pims.repository.MySpecificationAdapter;
import net.formula97.webapp.pims.repository.UserRepository;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.web.forms.IssueItemForm;
import net.formula97.webapp.pims.web.forms.NewLedgerForm;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Created by f97one on 2016/10/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class LedgerControllerTest extends BaseTestCase {

    @InjectMocks
    private LedgerController mController;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private LedgerRefUserRepository ledgerRefUserRepo;
    @Autowired
    private IssueLedgerRepository issueLedgerRepo;
    @Autowired
    private IssueLedgerService issueLedgerSvc;

    private Validator validator;
    private MockMvc mMvcMock;
    private int existingLedgerId;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        if (mMvcMock == null) {
            mMvcMock = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        apiEndpoint = "/ledger";

        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        Users user1 = new Users();
        user1.setUsername("user11");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setDisplayName("JUnit test11");
        user1.setMailAddress("user11@example.com");
        user1.setAuthority(AppConstants.ROLE_USER);
        userRepo.save(user1);

        Users user2 = new Users();
        user2.setUsername("user22");
        user2.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user2.setDisplayName("JUnit test22");
        user2.setMailAddress("user22@example.com");
        user2.setAuthority(AppConstants.ROLE_USER);
        userRepo.save(user2);

        IssueLedger l1 = new IssueLedger();
        l1.setPublicLedger(true);
        l1.setLedgerName("LCTest用台帳１");
        l1.setOpenStatus(2);
        issueLedgerRepo.save(l1);

        MySpecificationAdapter<IssueLedger> issueLedgerSpecification = new MySpecificationAdapter<>(IssueLedger.class);

        IssueLedger ledger = issueLedgerRepo.findOne(issueLedgerSpecification.eq("ledgerName", l1.getLedgerName()));
        this.existingLedgerId = ledger.getLedgerId();

        LedgerRefUser lru1 = new LedgerRefUser();
        lru1.setUserId(user1.getUsername());
        lru1.setLedgerId(ledger.getLedgerId());
        ledgerRefUserRepo.save(lru1);

    }

    @After
    public void tearDown() throws Exception {
        ledgerRefUserRepo.deleteAll();
        issueLedgerRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void フォームバリデーションが働いている() throws Exception {
        NewLedgerForm frm = new NewLedgerForm();
        frm.setOpenStatus(1);
        frm.setPublicLedger(true);
        frm.setLedgerName("");

        Set<ConstraintViolation<NewLedgerForm>> violationsSet = validator.validate(frm);

        assertThat("エラーは1件", violationsSet.size(), is(1));
        for (ConstraintViolation<NewLedgerForm> cv : violationsSet) {
            boolean isLengthErr = cv.getConstraintDescriptor().getAnnotation() instanceof Size;

            assertThat("長さのエラーが検知されている", isLengthErr, is(true));
        }

        NewLedgerForm frm2 = new NewLedgerForm();
        frm2.setOpenStatus(1);
        frm2.setPublicLedger(true);
        frm2.setLedgerName("ユーザーあり追加テスト用台帳");

        violationsSet = validator.validate(frm2);

        assertThat("エラーは検知されていない", violationsSet.size(), is(0));

        NewLedgerForm frm3 = new NewLedgerForm();
        frm3.setOpenStatus(1);
        frm3.setPublicLedger(true);
        frm3.setLedgerName("12345678901234567890123456789012345678901234567890123456789012345");

        violationsSet = validator.validate(frm3);

        assertThat("エラーは1件", violationsSet.size(), is(1));
        for (ConstraintViolation<NewLedgerForm> cv : violationsSet) {
            boolean isLengthErr = cv.getConstraintDescriptor().getAnnotation() instanceof Size;

            assertThat("長さのエラーが検知されている", isLengthErr, is(true));
        }
    }

    @Test
    @WithAnonymousUser
    public void ログインしていないユーザーには台帳が追加できない() throws Exception {
        int beforeLedgerCount = issueLedgerRepo.findAll().size();
        int beforeRefCount = ledgerRefUserRepo.findAll().size();

        ResultActions actions = mMvcMock.perform(MockMvcRequestBuilders.post("/ledger/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf())
                .param("ledgerName", "ユーザーあり追加テスト用台帳")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("addLedgerBtn", "追加")
        );

        actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/addLedger")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        int afterLedgerCount = issueLedgerRepo.findAll().size();
        int afterRefCount = ledgerRefUserRepo.findAll().size();

        assertThat("台帳の数は同じ", beforeLedgerCount, Matchers.is(afterLedgerCount));
        assertThat("台帳割り当て数も同じ", beforeRefCount, Matchers.is(afterRefCount));
    }

    @Test
    @WithMockUser(value = "user11")
    public void ログインしている場合台帳が作成できる() throws Exception {
        int beforeLedgerCount = issueLedgerRepo.findAll().size();
        int beforeRefCount = ledgerRefUserRepo.findAll().size();

        NewLedgerForm frm = new NewLedgerForm("ユーザーあり追加テスト用台帳", 1, true);

        ResultActions actions = mMvcMock.perform(MockMvcRequestBuilders.post("/ledger/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf())
                .param("ledgerName", "ユーザーあり追加テスト用台帳")
                .param("publicLedger", "true")
                .param("openStatus", "1")
                .param("addLedgerBtn", "追加")
        );

        int afterLedgerCount = issueLedgerRepo.findAll().size();
        int afterRefCount = ledgerRefUserRepo.findAll().size();

        MvcResult mvcResult = actions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(is("redirect:/")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        assertThat("台帳の数は1増えている", afterLedgerCount, is(beforeLedgerCount + 1));
        assertThat("台帳割り当て数も1増えている", afterRefCount, is(beforeRefCount + 1));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        Optional<IssueLedger> ledgerOptional = issueLedgerRepo.findAll().stream().filter((r) -> r.getLedgerName().equals("ユーザーあり追加テスト用台帳")).findFirst();

        assertThat("追加した台帳が入っている", ledgerOptional.isPresent(), is(true));
        assertThat("台帳は公開台帳", ledgerOptional.get().getPublicLedger(), is(true));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと課題が追加できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/add", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(get(template));
        MvcResult mvcResult = actions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリー表示になっている", modeTag, is(AppConstants.EDIT_MODE_READONLY));
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題の追加にはログインが必要です。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("台帳名は「LCTest用台帳１」", form.getCurrentLedgerName(), is("LCTest用台帳１"));
        assertThat("課題番号は「新規」", form.getIssueNumberLabel(), is("新規"));
    }

    @Test
    @WithMockUser(value = "user11")
    public void 存在しない台帳を指定すると課題を追加できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/add", apiEndpoint, existingLedgerId + 1);
        ResultActions actions = mMvcMock.perform(get(template));
        MvcResult mvcResult = actions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリー表示になっている", modeTag, is(AppConstants.EDIT_MODE_READONLY));
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("台帳名は「※不明※」", form.getCurrentLedgerName(), is("※不明※"));
        assertThat("課題番号は「新規」", form.getIssueNumberLabel(), is("新規"));
    }

    @Test
    @WithMockUser(value = "user22")
    public void 台帳に関係ないユーザーでは課題を追加できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/add", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(get(template));
        MvcResult mvcResult = actions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリー表示になっている", modeTag, is(AppConstants.EDIT_MODE_READONLY));
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題を追加する権限がありません。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("台帳名は「LCTest用台帳１」", form.getCurrentLedgerName(), is("LCTest用台帳１"));
        assertThat("課題番号は「新規」", form.getIssueNumberLabel(), is("新規"));
    }

    @Test
    @WithMockUser(value = "user11")
    public void 台帳に関係あるユーザーだと課題追加フォームを操作できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/add", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(get(template));
        MvcResult mvcResult = actions.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("編集可能になっている", modeTag, is(AppConstants.EDIT_MODE_ADD));
        String errMsg = (String) modelMap.get("errMsg");
        assertNull(errMsg);
        String infoMsg = (String) modelMap.get("infoMsg");
        assertNull(infoMsg);

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("台帳名は「LCTest用台帳１」", form.getCurrentLedgerName(), is("LCTest用台帳１"));
        assertThat("課題番号は「新規」", form.getIssueNumberLabel(), is("新規"));
    }

    @Ignore
    @Test
    public void 課題追加のフォームバリデーションがはたらいている() {
        fail("まだ実装していない");

        IssueItemForm form = new IssueItemForm();
        form.setFoundDate("20161229");
        form.setSeverity(null);
        form.setFoundUserId("");
        form.setCategoryId("");
    }
}