package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.repository.UserRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by f97one on 2016/12/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class AdminControllerTest extends BaseTestCase {

    @InjectMocks
    private AdminController mockController;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepo;

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
        apiEndpoint = String.format(Locale.getDefault(), "http://localhost:%d/admin", port);

        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        Optional<Users> adminOptional = userRepo.findById("admin");
        adminOptional.ifPresent(users -> userRepo.deleteById("admin"));

        Users user1 = new Users();
        user1.setUsername("user1");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setDisplayName("JUnit test1");
        user1.setMailAddress("test1@example.com");
        user1.setAuthority(AppConstants.CANONICAL_ROLE_USER);
        userRepo.save(user1);

        Users user2 = new Users();
        user2.setUsername("user2");
        user2.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user2.setDisplayName("JUnit test2");
        user2.setMailAddress("test2@example.com");
        user2.setAuthority(AppConstants.CANONICAL_ROLE_USER);
        userRepo.save(user2);

        Users admin1 = new Users();
        admin1.setUsername("kanrisha1");
        admin1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        admin1.setDisplayName("JUnit kanrisha1");
        admin1.setMailAddress("kanrisha1@example.net");
        admin1.setAuthority(AppConstants.CANONICAL_ROLE_ADMIN);
        userRepo.save(admin1);
    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
    }

    @Test
    @WithAnonymousUser
    public void ログインしていないときはページを表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get("/admin")).andDo(print());

        actions.andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"USER"})
    public void 一般ユーザーでログインしているときもページを表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get("/admin")).andDo(print());

        actions.andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void 管理者ユーザーでログインしているときならページを表示できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get("/admin")).andDo(print());

        actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/adminMenu")))
                .andReturn();
    }

}
