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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.junit.Assert.*;

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

        Users user1 = new Users();
        user1.setUsername("user11");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setDisplayName("JUnit test11");
        user1.setMailAddress("test@example.com");
        user1.setAuthority(AppConstants.AUTHORITY_USER);
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
    public void ログインしていないユーザーには台帳が追加できない() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("ledgerName", "ユーザーなし追加テスト用台帳");
        params.add("public", "true");

        ResultActions actions = mMvcMock.perform(MockMvcRequestBuilders.post(apiEndpoint + "/create")
                .params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        actions.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user11")
    public void ログインしている場合台帳が作成できる() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("ledgerName", "ユーザーあり追加テスト用台帳");
        params.add("public", "true");

        ResultActions actions = mMvcMock.perform(MockMvcRequestBuilders.post(apiEndpoint + "/create")
                .params(params)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().hasNoErrors());

        ModelMap modelMap = actions.andReturn().getModelAndView().getModelMap();
        NewLedgerForm resultForm = (NewLedgerForm) modelMap.get("newLedgerForm");
        assertThat("台帳名は「ユーザーあり追加テスト用台帳」", resultForm.getLedgerName(), Matchers.is("ユーザーあり追加テスト用台帳"));
        assertThat("公開台帳になっている", resultForm.isPublicLedger(), Matchers.is(true));
    }
}