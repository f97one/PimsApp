package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import net.formula97.webapp.pims.repository.UserRepository;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.web.forms.NewLedgerForm;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
        apiEndpoint = String.format(Locale.getDefault(), "http://localhost:%d/ledger", port);

        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        Users user1 = new Users();
        user1.setUsername("user11");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setDisplayName("JUnit test11");
        user1.setMailAddress("test@example.com");
        user1.setAuthority(AppConstants.ROLE_USER);
        userRepo.save(user1);

        IssueLedger l1 = new IssueLedger();
        l1.setPublicLedger(true);
        l1.setLedgerName("LCTest用台帳１");
        l1.setOpenStatus(2);
        issueLedgerRepo.save(l1);

        IssueLedger ledger = issueLedgerRepo.findOne(Specifications.where(IssueLedgerService.IssueLedgerSpecifications.nameEquals(l1.getLedgerName())));
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
                .param("newLedgerForm.ledgerName", "ユーザーあり追加テスト用台帳")
                .param("newLedgerForm.publicLedger", "true")
                .param("newLedgerForm.openStatus", "1")
                .param("addLedgerBtn", "")
        );

        actions.andExpect(MockMvcResultMatchers.status().isOk());

        int afterLedgerCount = issueLedgerRepo.findAll().size();
        int afterRefCount = ledgerRefUserRepo.findAll().size();

        assertThat("台帳の数は同じ", beforeLedgerCount, Matchers.is(afterLedgerCount));
        assertThat("台帳割り当て数も同じ", beforeRefCount, Matchers.is(afterRefCount));
    }

    @Ignore
    @Test
    @WithUserDetails(value = "user11", userDetailsServiceBeanName = "authorizedUsersService")
    public void ログインしている場合台帳が作成できる() throws Exception {
        int beforeLedgerCount = issueLedgerRepo.findAll().size();
        int beforeRefCount = ledgerRefUserRepo.findAll().size();

        ResultActions actions = mMvcMock.perform(MockMvcRequestBuilders.post("/ledger/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .with(csrf())
                .param("newLedgerForm.ledgerName", "ユーザーあり追加テスト用台帳")
                .param("newLedgerForm.publicLedger", "true")
                .param("newLedgerForm.openStatus", "1")
                .param("addLedgerBtn", "")

        );

        int afterLedgerCount = issueLedgerRepo.findAll().size();
        int afterRefCount = ledgerRefUserRepo.findAll().size();

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().hasNoErrors());

        ModelMap modelMap = actions.andReturn().getModelAndView().getModelMap();
        NewLedgerForm resultForm = (NewLedgerForm) modelMap.get("newLedgerForm");
        assertThat("台帳名は「ユーザーあり追加テスト用台帳」", resultForm.getLedgerName(), is("ユーザーあり追加テスト用台帳"));
        assertThat("公開台帳になっている", resultForm.isPublicLedger(), is(true));

        assertThat("台帳の数は1増えている", beforeLedgerCount, Matchers.is(afterLedgerCount - 1));
        assertThat("台帳割り当て数も1増えている", beforeRefCount, Matchers.is(afterRefCount - 1));
    }
}