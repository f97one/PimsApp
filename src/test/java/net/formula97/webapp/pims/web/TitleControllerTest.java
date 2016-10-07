/**
 * 
 */
package net.formula97.webapp.pims.web;

import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.jpa.domain.Specifications;
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

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import net.formula97.webapp.pims.repository.UserRepository;
import net.formula97.webapp.pims.service.IssueLedgerService.IssueLedgerSpecifications;

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
        user1.setUserId("user1");
        user1.setEncodedPasswd(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setDisplayName("JUnit test");
        user1.setMailAddress("test@example.com");
        userRepo.save(user1);
        
        IssueLedger l1 = new IssueLedger();
        l1.setIsPublic(true);
        l1.setLedgerName("テスト用台帳１");
        l1.setOpenStatus(1);
        issueLedgerRepo.save(l1);
        
        IssueLedger l2 = new IssueLedger();
        l2.setIsPublic(false);
        l2.setLedgerName("テスト用非公開台帳１");
        l2.setOpenStatus(1);
        issueLedgerRepo.save(l2);
        
        IssueLedger ledger = issueLedgerRepo.findOne(Specifications.where(IssueLedgerSpecifications.nameContains(l1.getLedgerName())));
        LedgerRefUser lru1 = new LedgerRefUser();
        lru1.setUserId(user1.getUserId());
        lru1.setLedgerId(ledger.getLedgerId());
        
        IssueLedger ledger2 = issueLedgerRepo.findOne(Specifications.where(IssueLedgerSpecifications.nameContains(l2.getLedgerName())));
        LedgerRefUser lru2 = new LedgerRefUser();
        lru2.setUserId(user1.getUserId());
        lru2.setLedgerId(ledger2.getLedgerId());
        
        ledgerRefUserRepo.save(lru1);
        ledgerRefUserRepo.save(lru2);
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
    }

    @Test
    public void 普通に起動すると公開データだけ表示される() throws Exception {
        List<IssueLedger> currList = issueLedgerRepo.findByPublicLedger();
        assertThat("取得できる台帳は1個", currList.size(), Matchers.is(1));

        URI uri = new URI(apiEndpoint);
        this.mMvcMock.perform(MockMvcRequestBuilders.get(uri))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("title"))
            .andExpect(MockMvcResultMatchers.model().attribute("title", "PIMS Beta"));
    }

    @Test
    @WithMockUser(username = "user1")
    public void ログインしているとそのユーザーの台帳が表示される() throws Exception {
        List<IssueLedger> currList = issueLedgerRepo.findForUser("user1");
        assertThat("取得できる台帳は2個", currList.size(), Matchers.is(2));
        
        URI uri = new URI(apiEndpoint);
        this.mMvcMock.perform(MockMvcRequestBuilders.get(uri))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("title"))
            .andExpect(MockMvcResultMatchers.model().attribute("title", "PIMS Beta"));
    }
    
}
