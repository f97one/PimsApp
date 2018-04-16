/**
 * 
 */
package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.SystemConfig;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.service.IssueLedgerService;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertThat;

/**
 * タイトル画面Controllerのテストケース。
 * 
 * @author f97one
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
                properties = {"server.port:0", "spring.profiles.active:test"})
public class TitleControllerTest extends BaseTestCase {

    @InjectMocks
    private TitleController mController;
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
    @Autowired
    private SystemConfigRepository sysConfigRepo;
    
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

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        if (mMvcMock == null) {
            mMvcMock = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        apiEndpoint = String.format(Locale.getDefault(), "http://localhost:%d/", port);
        
        Users user1 = new Users();
        user1.setUsername("user1");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setDisplayName("JUnit test");
        user1.setMailAddress("test@example.com");
        user1.setAuthority(AppConstants.ROLE_USER);
        userRepo.save(user1);
        
        Users user2 = new Users();
        user2.setUsername("user2");
        user2.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user2.setDisplayName("JUnit test 2");
        user2.setMailAddress("test2@example.com");
        user2.setAuthority(AppConstants.ROLE_ADMIN);
        userRepo.save(user2);
        
        IssueLedger l1 = new IssueLedger();
        l1.setPublicLedger(true);
        l1.setLedgerName("テスト用台帳１");
        l1.setOpenStatus(1);
        issueLedgerRepo.save(l1);
        
        IssueLedger l2 = new IssueLedger();
        l2.setPublicLedger(false);
        l2.setLedgerName("テスト用非公開台帳１");
        l2.setOpenStatus(1);
        issueLedgerRepo.save(l2);
        
        IssueLedger l3 = new IssueLedger();
        l3.setPublicLedger(false);
        l3.setLedgerName("テスト用非公開台帳２");
        l3.setOpenStatus(1);
        issueLedgerRepo.save(l3);
        
        IssueLedger l4 = new IssueLedger();
        l4.setPublicLedger(true);
        l4.setLedgerName("テスト用台帳２");
        l4.setOpenStatus(1);
        issueLedgerRepo.save(l4);

        MySpecificationAdapter<IssueLedger> issueLedgerSpecification = new MySpecificationAdapter<>(IssueLedger.class);

        IssueLedger ledger = issueLedgerRepo.findOne(issueLedgerSpecification.contains("ledgerName", l1.getLedgerName())).orElse(null);
        LedgerRefUser lru1 = new LedgerRefUser();
        lru1.setUserId(user1.getUsername());
        lru1.setLedgerId(ledger.getLedgerId());

        IssueLedger ledger2 = issueLedgerRepo.findOne(issueLedgerSpecification.contains("ledgerName", l2.getLedgerName())).orElse(null);
        LedgerRefUser lru2 = new LedgerRefUser();
        lru2.setUserId(user1.getUsername());
        lru2.setLedgerId(ledger2.getLedgerId());

        IssueLedger ledger3 = issueLedgerRepo.findOne(issueLedgerSpecification.contains("ledgerName", l3.getLedgerName())).orElse(null);
        LedgerRefUser lru3 = new LedgerRefUser();
        lru3.setUserId(user2.getUsername());
        lru3.setLedgerId(ledger3.getLedgerId());
        
        ledgerRefUserRepo.save(lru1);
        ledgerRefUserRepo.save(lru2);
        ledgerRefUserRepo.save(lru3);

        SystemConfig systemConfig = new SystemConfig(AppConstants.SysConfig.APP_TITLE, "PIMS Beta");
        sysConfigRepo.save(systemConfig);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // レコードを全部消す
        ledgerRefUserRepo.deleteAll();
        issueLedgerRepo.deleteAll();
        userRepo.deleteAll();
        sysConfigRepo.deleteAll();
    }

    @Test
    public void 普通に起動すると公開データだけ表示される() throws Exception {
        List<IssueLedger> currList = issueLedgerSvc.getPublicLedgers();
        assertThat("取得できる台帳は2個", currList.size(), Matchers.is(2));

        URI uri = new URI(apiEndpoint);
        this.mMvcMock.perform(MockMvcRequestBuilders.get(uri))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/title"))
            .andExpect(MockMvcResultMatchers.model().attribute("title", "PIMS Beta"));
    }

    @Test
    @WithMockUser(username = "user1")
    public void ログインしているとそのユーザーの台帳が表示される() throws Exception {
        List<IssueLedger> currList = issueLedgerSvc.getLedgersForUser("user1");
        assertThat("取得できる台帳は3個", currList.size(), Matchers.is(3));
        
        URI uri = new URI(apiEndpoint);
        this.mMvcMock.perform(MockMvcRequestBuilders.get(uri))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/title"))
            .andExpect(MockMvcResultMatchers.model().attribute("title", "PIMS Beta"));
    }
    
}
