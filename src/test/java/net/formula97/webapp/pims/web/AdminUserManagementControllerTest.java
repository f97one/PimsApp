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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by f97one on 2016/12/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class AdminUserManagementControllerTest extends BaseTestCase {

    @InjectMocks
    private AdminController mockController;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepo;

    private Validator validator;
    private MockMvc mMvcMock;

    private final String userManagementUrlTemplate = "/admin/userManagement";

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

        Users user2 = new Users();
        user2.setUsername("user2");
        user2.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user2.setDisplayName("JUnit test2");
        user2.setMailAddress("test2@example.com");
        user2.setAuthority(AppConstants.ROLE_USER);
        user2.setEnabled(false);
        userRepo.save(user2);

        Users admin1 = new Users();
        admin1.setUsername("kanrisha1");
        admin1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        admin1.setDisplayName("JUnit kanrisha1");
        admin1.setMailAddress("kanrisha1@example.net");
        admin1.setAuthority(AppConstants.ROLE_ADMIN);
        admin1.setEnabled(true);
        userRepo.save(admin1);
    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
    }


    @Test
    @WithAnonymousUser
    public void ログインしていないときはページを表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate)).andDo(print());

        actions.andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"USER"})
    public void 一般ユーザーでログインしているときもページを表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate)).andDo(print());

        actions.andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void 管理者ユーザーでログインしているときならページを表示できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate)).andDo(print());

        actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_list")))
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void 何も指定しないときはすべてのユーザーが検索できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser")
                /*
                   「何も指定していないとき」とはしているが、
                   1. limitEnabledUserは空にできないため、チェックを外している状態をfalseと仮定
                   2. searchUserBtnは、ボタンを押すと「検索」が必ず付加される
                   以下同様。
                 */
                .param("username", "")
                .param("displayName", "")
                .param("mailAddress", "")
                .param("limitEnabledUser", "false")
                .param("searchUserBtn", "検索"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_list")))
                .andExpect(model().attributeExists("dispUserList"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        Object obj = modelMap.get("dispUserList");

        assertThat("List<>が入っている", obj instanceof List, is(true));
        List<Users> searchResult = (List<Users>) obj;
        assertThat("3ユーザー検索できている", searchResult.size(), is(3));
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void 有効ユーザーだけ検索できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser")
                .param("username", "")
                .param("displayName", "")
                .param("mailAddress", "")
                .param("limitEnabledUser", "true")
                .param("searchUserBtn", "検索"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_list")))
                .andExpect(model().attributeExists("dispUserList"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        Object obj = modelMap.get("dispUserList");

        assertThat("List<>が入っている", obj instanceof List, is(true));
        List<Users> searchResult = (List<Users>) obj;
        assertThat("2ユーザー検索できている", searchResult.size(), is(2));

        Optional<Users> resultUserOpt = searchResult.stream().filter((r) -> r.getUsername().equals("user1")).findFirst();
        assertThat("user1が取得できている", resultUserOpt.isPresent(), is(true));
        resultUserOpt = searchResult.stream().filter((r) -> r.getUsername().equals("kanrisha1")).findFirst();
        assertThat("kanrisha1が取得できている", resultUserOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void ユーザーIDで検索できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser")
                .param("username", "user")
                .param("displayName", "")
                .param("mailAddress", "")
                .param("limitEnabledUser", "false")
                .param("searchUserBtn", "検索"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_list")))
                .andExpect(model().attributeExists("dispUserList"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        Object obj = modelMap.get("dispUserList");

        assertThat("List<>が入っている", obj instanceof List, is(true));
        List<Users> searchResult = (List<Users>) obj;
        assertThat("2ユーザー検索できている", searchResult.size(), is(2));

        Optional<Users> resultUserOpt = searchResult.stream().filter((r) -> r.getUsername().equals("user1")).findFirst();
        assertThat("user1が取得できている", resultUserOpt.isPresent(), is(true));
        resultUserOpt = searchResult.stream().filter((r) -> r.getUsername().equals("user2")).findFirst();
        assertThat("user2が取得できている", resultUserOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void 表示名で検索できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser")
                .param("username", "")
                .param("displayName", "kanrisha")
                .param("mailAddress", "")
                .param("limitEnabledUser", "false")
                .param("searchUserBtn", "検索"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_list")))
                .andExpect(model().attributeExists("dispUserList"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        Object obj = modelMap.get("dispUserList");

        assertThat("List<>が入っている", obj instanceof List, is(true));
        List<Users> searchResult = (List<Users>) obj;
        assertThat("1ユーザー検索できている", searchResult.size(), is(1));

        Optional<Users> resultUserOpt = searchResult.stream().filter((r) -> r.getUsername().equals("kanrisha1")).findFirst();
        assertThat("kanrisha1が取得できている", resultUserOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void メアドで検索できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser")
                .param("username", "")
                .param("displayName", "")
                .param("mailAddress", ".com")
                .param("limitEnabledUser", "false")
                .param("searchUserBtn", "検索"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_list")))
                .andExpect(model().attributeExists("dispUserList"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        Object obj = modelMap.get("dispUserList");

        assertThat("List<>が入っている", obj instanceof List, is(true));
        List<Users> searchResult = (List<Users>) obj;
        assertThat("2ユーザー検索できている", searchResult.size(), is(2));

        Optional<Users> resultUserOpt = searchResult.stream().filter((r) -> r.getUsername().equals("user1")).findFirst();
        assertThat("user1が取得できている", resultUserOpt.isPresent(), is(true));
        resultUserOpt = searchResult.stream().filter((r) -> r.getUsername().equals("user2")).findFirst();
        assertThat("user2が取得できている", resultUserOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void 条件に外れた検索だと結果欄がない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser")
                .param("username", "kanri")
                .param("displayName", "")
                .param("mailAddress", ".com")
                .param("limitEnabledUser", "false")
                .param("searchUserBtn", "検索"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_list")))
                .andExpect(model().attributeExists("dispUserList"))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        List<Users> searchResult = (List<Users>) modelMap.get("dispUserList");

        assertThat("検索できたユーザーは0", searchResult.size(), is(0));
    }

    @Test
    @WithAnonymousUser
    public void 非ログイン時はユーザー追加画面を表示できない() throws Exception {
        mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser").param("addUserBtn", "ユーザー追加"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"USER"})
    public void 一般ユーザーでもユーザー追加画面を表示できない() throws Exception {
        mMvcMock.perform(get(userManagementUrlTemplate + "/searchUser").param("addUserBtn", "ユーザー追加"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"ADMIN"})
    public void 管理者ユーザーならユーザー追加画面を表示できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(userManagementUrlTemplate + "/add")
                .param("addUserBtn", "ユーザー追加")).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/user_detail")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        String modeTag = (String) modelMap.get("modeTag");
        assertThat("新規追加モードで表示している", modeTag, is(AppConstants.EDIT_MODE_ADD));
    }
}
