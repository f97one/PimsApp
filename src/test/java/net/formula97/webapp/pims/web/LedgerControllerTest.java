package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.*;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.misc.CommonsStringUtils;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.web.forms.IssueItemForm;
import net.formula97.webapp.pims.web.forms.NewLedgerForm;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private IssueItemsRepository issueItemsRepo;
    @Autowired
    private StatusMasterRepository statusMasterRepo;

    private Validator validator;
    private MockMvc mMvcMock;
    private int existingLedgerId;
    private int existingIssueId;
    private String existingIssueTimestamp;

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

        IssueLedger ledger = issueLedgerRepo.findOne(issueLedgerSpecification.eq("ledgerName", l1.getLedgerName())).get();
        this.existingLedgerId = ledger.getLedgerId();

        LedgerRefUser lru1 = new LedgerRefUser();
        lru1.setUserId(user1.getUsername());
        lru1.setLedgerId(ledger.getLedgerId());
        ledgerRefUserRepo.save(lru1);

        IssueItems items = new IssueItems();
        items.setActionStatusId(1);
        items.setSevereLevelId(2);
        items.setLedgerId(this.existingLedgerId);
        items.setFoundUser("user11");
        items.setFoundDate(new Date());
        items.setCategoryId(3);
        items.setFoundProcessId(3);
        items.setIssueDetail("Hoge処理が動かない。なんとかしろ");
        items.setRowUpdatedAt(new Date());
        issueItemsRepo.save(items);

        MySpecificationAdapter<IssueItems> iiSpec = new MySpecificationAdapter<>(IssueItems.class);
        Optional<IssueItems> itemsOpt = issueItemsRepo.findOne(Specification.where(iiSpec.eq("ledgerId", existingLedgerId))
                        .and(iiSpec.eq("foundUser", "user11"))
                .and(iiSpec.eq("issueDetail", "Hoge処理が動かない。なんとかしろ")));
        itemsOpt.ifPresent(items1 -> this.existingIssueId = items1.getIssueId());
        this.existingIssueTimestamp = items.getRowUpdatedAt().toString();
    }

    @After
    public void tearDown() throws Exception {
        issueItemsRepo.deleteAll();
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

        ResultActions actions = mMvcMock.perform(post("/ledger/create")
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

        ResultActions actions = mMvcMock.perform(post("/ledger/create")
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

        String currentLedgerName = (String) modelMap.get("currentLedgerName");
        String issueNumberLabel = (String) modelMap.get("issueNumberLabel");
        assertThat("台帳名は「LCTest用台帳１」", currentLedgerName, is("LCTest用台帳１"));
        assertThat("課題番号は「新規」", issueNumberLabel, is("新規"));
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

        String currentLedgerName = (String) modelMap.get("currentLedgerName");
        String issueNumberLabel = (String) modelMap.get("issueNumberLabel");
        assertThat("台帳名は「※不明※」", currentLedgerName, is("※不明※"));
        assertThat("課題番号は「新規」", issueNumberLabel, is("新規"));
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

        String currentLedgerName = (String) modelMap.get("currentLedgerName");
        String issueNumberLabel = (String) modelMap.get("issueNumberLabel");
        assertThat("台帳名は「LCTest用台帳１」", currentLedgerName, is("LCTest用台帳１"));
        assertThat("課題番号は「新規」", issueNumberLabel, is("新規"));
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

        String currentLedgerName = (String) modelMap.get("currentLedgerName");
        String issueNumberLabel = (String) modelMap.get("issueNumberLabel");
        assertThat("台帳名は「LCTest用台帳１」", currentLedgerName, is("LCTest用台帳１"));
        assertThat("課題番号は「新規」", issueNumberLabel, is("新規"));
    }

    @Test
    public void 課題追加のフォームバリデーションがはたらいている() {
        IssueItemForm form = new IssueItemForm();
        form.setFoundDate("20161229");
        form.setSeverity(-1);
        form.setFoundUserId("");
        form.setCategoryId(-1);
        form.setProcessId(-1);
        form.setIssueDetail("");
        form.setCaused("");
        form.setCountermeasures("");
        form.setConfirmedUserId("");
        form.setCorrespondingTime(null);
        form.setConfirmedUserId("");
        form.setConfirmedDate("");
        form.setActionStatusId(1);

        Set<ConstraintViolation<IssueItemForm>> violationsSet = validator.validate(form);
        assertThat("エラーは3件", violationsSet.size(), is(3));
        Optional<ConstraintViolation<IssueItemForm>> violationOpt = violationsSet.stream().filter((r) -> r.getConstraintDescriptor().getAnnotation() instanceof Min).findFirst();
        if (violationOpt.isPresent()) {
            ConstraintViolation<IssueItemForm> violation = violationOpt.get();
            assertNotNull("最低値エラーが検知されている", violation);
            assertThat("Severityのエラーが検知されている", violation.getPropertyPath().toString().toLowerCase(), is("severity"));
        } else {
            fail("最低値エラーが検知されていない");
        }

        violationOpt = violationsSet.stream().filter((r) -> r.getConstraintDescriptor().getAnnotation() instanceof Size).findFirst();
        if (violationOpt.isPresent()) {
            ConstraintViolation<IssueItemForm> violation = violationOpt.get();
            assertNotNull("Sizeエラーが検知されている", violation);
            assertThat("FoundUserIdのエラーが検知されている", violation.getPropertyPath().toString().toLowerCase(), is("founduserid"));
        } else {
            fail("Sizeエラーが検知されていない");
        }

        violationOpt = violationsSet.stream().filter((r) -> r.getConstraintDescriptor().getAnnotation() instanceof Pattern).findFirst();
        if (violationOpt.isPresent()) {
            ConstraintViolation<IssueItemForm> violation = violationOpt.get();
            assertNotNull("Patternエラーが検知されている", violation);
            assertThat("FoundDateのエラーが検知されている", violation.getPropertyPath().toString().toLowerCase(), is("founddate"));
        } else {
            fail("Patternエラーが検知されていない");
        }
    }

    @Test
    @WithMockUser("user11")
    public void 課題を追加できる() throws Exception {
        int beforeIssueCount = issueItemsRepo.findAll().size();

        String template = String.format(Locale.getDefault(), "%s/%d/add", apiEndpoint, existingLedgerId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("addItemBtn", "追加")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", "新規")
                .param("recordTimestamp", String.valueOf(Calendar.getInstance().getTimeInMillis()))
                .param("foundDate", "2017/01/01")
                .param("severity", "1")
                .param("foundUserId", "user11")
                .param("categoryId", String.valueOf(AppConstants.SELECTION_NOT_SELECTED))
                .param("processId", String.valueOf(AppConstants.SELECTION_NOT_SELECTED))
                .param("issueDetail", "いきなり落ちた")
                .param("caused", "")
                .param("countermeasures", "")
                .param("correspondingUserId", "")
                .param("correspondingTime", "")
                .param("correspondingEndDate", "")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        List<IssueItems> issueItemsList = issueItemsRepo.findAll();
        assertThat("課題が１増えている", issueItemsList.size(), is(beforeIssueCount + 1));

        // ステータスが2になっているか確認
        IssueItems i = issueItemsList.get(issueItemsList.size() - 1);
        assertThat("投入された課題のStatusIdは2", i.getActionStatusId(), is(2));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String infoMsg = (String) modelMap.get("infoMsg");
        assertThat(infoMsg, is("課題を追加しました。"));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと課題を更新できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        IssueItems resultItem = issueItemsRepo.findById(pk).get();
        assertThat("レコードは更新されていない", resultItem.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()), is(0));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題の更新にはログインが必要です。"));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない台帳の課題は更新できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId + 1, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        IssueItems resultItem = issueItemsRepo.findById(pk).get();
        assertThat("レコードは更新されていない", resultItem.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()), is(0));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない課題は更新できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId + 1);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        IssueItems resultItem = issueItemsRepo.findById(pk).get();
        assertThat("レコードは更新されていない", resultItem.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()), is(0));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));
    }

    @Test
    @WithMockUser("user22")
    public void 台帳に関係ないユーザーでは課題を更新できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        IssueItems resultItem = issueItemsRepo.findById(pk).get();
        assertThat("レコードは更新されていない", resultItem.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()), is(0));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題を更新する権限がありません。"));
    }

    @Test
    @WithMockUser("user11")
    public void 先に更新されていると更新が拒否される() throws Exception {
        // アイテムの更新日時を未来日付に書き換えておく
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        String baseTimestamp = String.valueOf(baseItem.getRowUpdatedAt().getTime());

        Calendar cal = Calendar.getInstance();
        cal.setTime(baseItem.getRowUpdatedAt());
        cal.add(Calendar.DAY_OF_MONTH, 1);

        baseItem.setRowUpdatedAt(cal.getTime());
        baseItem.setLedgerId(existingLedgerId);
        baseItem.setIssueId(existingIssueId);
        issueItemsRepo.save(baseItem);

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", baseTimestamp)
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        IssueItems resultItem = issueItemsRepo.findById(pk).get();
        assertThat("レコードは更新されていない", resultItem.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()), is(0));

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("別のユーザーが課題を更新しました。リロードしてください。"));

    }

    @Test
    @WithMockUser("user11")
    public void 課題を更新できる() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("updateItemBtn", "更新")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String infoMsg = (String) modelMap.get("infoMsg");
        assertThat(infoMsg, is("課題を更新しました。"));

        IssueItems resultItem = issueItemsRepo.findById(pk).get();
        assertThat("レコードは更新されている", resultItem.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()) >= 0, is(true));
        assertThat(resultItem.getCaused(), is("null判断が不十分だったため"));
        assertThat(resultItem.getCountermeasures(), is("null判断を追加"));

        assertThat("対応ステータスIDは2", resultItem.getActionStatusId(), is(2));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない台帳の課題の編集画面は表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId + 1, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertNull("障害内容がnull", form.getIssueDetail());

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない課題の編集画面は表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId + 1);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertNull("障害内容がnull", form.getIssueDetail());

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));
    }

    @Test
    @WithMockUser("user22")
    public void 公開台帳に関係ないユーザーだと課題を参照だけできる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat(form.getIssueDetail(), is("Hoge処理が動かない。なんとかしろ"));
    }

    @Test
    @WithMockUser("user22")
    public void 非公開台帳に関係ないユーザーだと課題を表示できない() throws Exception {
        // 非公開台帳に書き換える
        IssueLedger l1 = issueLedgerRepo.findById(existingLedgerId).get();
        l1.setPublicLedger(false);
        issueLedgerRepo.save(l1);

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertNull("障害内容がnull", form.getIssueDetail());

    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと非公開台帳の課題も参照できない() throws Exception {
        // 非公開台帳に書き換える
        IssueLedger l1 = issueLedgerRepo.findById(existingLedgerId).get();
        l1.setPublicLedger(false);
        issueLedgerRepo.save(l1);

        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertNull("障害内容がnull", form.getIssueDetail());

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと公開台帳の課題は参照だけできる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));
    }

    @Test
    @WithMockUser("user11")
    public void 課題の編集画面を表示できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("更新モードで表示している", modeTag, is(AppConstants.EDIT_MODE_MODIFY));

        String issueNumberLabel = (String) modelMap.get("issueNumberLabel");
        String acturalLabel = String.format(Locale.getDefault(), "#%d", existingIssueId);
        assertThat("課題番号が表示できている", issueNumberLabel, is(acturalLabel));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない台帳の課題は削除画面を表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId + 1, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("発見者IDは空", CommonsStringUtils.isNullOrEmpty(form.getFoundUserId()), is(true));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない課題の削除画面は表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId + 1);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("発見者IDは空", CommonsStringUtils.isNullOrEmpty(form.getFoundUserId()), is(true));
    }

    @Test
    @WithMockUser("user22")
    public void 公開非公開かかわらず台帳に関係ないユーザーだと削除画面は表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("発見者IDは空", CommonsStringUtils.isNullOrEmpty(form.getFoundUserId()), is(true));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと削除画面を表示できない() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("リードオンリーモードで表示している", modeTag, is(AppConstants.EDIT_MODE_READONLY));

        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("発見者IDは空", CommonsStringUtils.isNullOrEmpty(form.getFoundUserId()), is(true));
    }

    @Test
    @WithMockUser("user11")
    public void 課題の削除画面を表示できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(get(template)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("削除モードで表示している", modeTag, is(AppConstants.EDIT_MODE_REMOVE));

        String infoMsg = (String) modelMap.get("infoMsg");
        assertThat(infoMsg, is("この課題を削除します。削除したら元に戻せません。よろしいですか？"));

        IssueItemForm form = (IssueItemForm) modelMap.get("issueItem");
        assertThat("発見者IDはuser11", form.getFoundUserId(), is("user11"));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない台帳の課題は削除できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId + 1, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("removeItemBtn", "削除")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("台帳が見つかりません。"));

//        IssueItems resultItem = issueItemsRepo.findOne(pk);
//        assertThat("レコードは更新されている", resultItem.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()) >= 0, is(true));
//        assertThat(resultItem.getCaused(), is("null判断が不十分だったため"));
//        assertThat(resultItem.getCountermeasures(), is("null判断を追加"));
    }

    @Test
    @WithMockUser("user11")
    public void 存在しない課題は削除できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId + 1);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("removeItemBtn", "削除")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題が見つかりません。"));

    }

    @Test
    @WithMockUser("user22")
    public void 公開非公開にかかわらず台帳に関係ないユーザーだと課題を削除できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("removeItemBtn", "削除")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題を削除する権限がありません。"));

        IssueItems resultItem = null;
        try {
            resultItem = issueItemsRepo.findById(pk).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull("課題は削除されずに残っている", resultItem);
        assertThat("原因は空のまま", CommonsStringUtils.isNullOrEmpty(resultItem.getCaused()), is(true));
        assertThat("更新処理を通っていない", baseItem.getRowUpdatedAt().compareTo(resultItem.getRowUpdatedAt()), is(0));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと課題を削除できない() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("removeItemBtn", "削除")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String errMsg = (String) modelMap.get("errMsg");
        assertThat(errMsg, is("課題の削除にはログインが必要です。"));

        IssueItems resultItem = issueItemsRepo.findById(pk).get();
        assertNotNull("課題は削除されずに残っている", resultItem);
        assertThat("原因は空のまま", CommonsStringUtils.isNullOrEmpty(resultItem.getCaused()), is(true));
        assertThat("更新処理を通っていない", baseItem.getRowUpdatedAt().compareTo(resultItem.getRowUpdatedAt()), is(0));
    }

    @Test
    @WithMockUser("user11")
    public void 課題を削除できる() throws Exception {
        IssueItemsPK pk = new IssueItemsPK(existingLedgerId, existingIssueId);

        IssueItems baseItem = issueItemsRepo.findById(pk).get();
        int baseItemCount = issueItemsRepo.findAll().size();

        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.STD_DATE_FORMAT, Locale.getDefault());

        String template = String.format(Locale.getDefault(), "%s/%d/remove/%d", apiEndpoint, existingLedgerId, existingIssueId);
        ResultActions actions = mMvcMock.perform(post(template)
                .with(csrf())
                .param("removeItemBtn", "削除")
                .param("currentLedgerName", "LCTest用台帳１")
                .param("issueNumberLabel", String.format(Locale.getDefault(), "#%d", existingIssueId))
                .param("recordTimestamp", String.valueOf(baseItem.getRowUpdatedAt().getTime()))
                .param("foundDate", sdf.format(baseItem.getFoundDate()))
                .param("severity", String.valueOf(baseItem.getSevereLevelId()))
                .param("foundUserId", baseItem.getFoundUser())
                .param("categoryId", String.valueOf(baseItem.getCategoryId()))
                .param("processId", String.valueOf(baseItem.getFoundProcessId()))
                .param("issueDetail", baseItem.getIssueDetail())
                .param("caused", "null判断が不十分だったため")
                .param("countermeasures", "null判断を追加")
                .param("correspondingUserId", "user11")
                .param("correspondingTime", "1.5")
                .param("correspondingEndDate", "2017/01/07")
                .param("confirmedUserId", "")
                .param("confirmedDate", "")
                .param("actionStatusId", "2")
        ).andDo(print());

        MvcResult mvcResult = actions
                .andExpect(status().isOk())
                .andExpect(view().name(is("/ledger/issueItem")))
                .andExpect(model().hasNoErrors())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String infoMsg = (String) modelMap.get("infoMsg");
        assertThat(infoMsg, is("課題を削除しました。"));

        Optional<IssueItems> resultItemOpt = issueItemsRepo.findById(pk);
        assertThat("課題は削除されている", resultItemOpt.isPresent(), is(false));

        int resultItemCount = issueItemsRepo.findAll().size();
        assertThat("1レコードだけ減少している", resultItemCount, is(baseItemCount - 1));

        String modeTag = (String) modelMap.get("modeTag");
        assertThat("新規追加モードになっている", modeTag, is(AppConstants.EDIT_MODE_ADD));
        String issueNumberLabel = (String) modelMap.get("issueNumberLabel");
        assertThat(issueNumberLabel, is("新規"));
    }

    /**
     * StatusMasterを初期値に戻す。
     */
    private void resetStatusMaster() {
        statusMasterRepo.deleteAll();

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
}