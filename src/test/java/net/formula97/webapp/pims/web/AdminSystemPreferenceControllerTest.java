package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.SystemConfig;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.repository.SystemConfigRepository;
import net.formula97.webapp.pims.repository.UserRepository;
import net.formula97.webapp.pims.web.forms.SystemPreferenceForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class AdminSystemPreferenceControllerTest extends BaseTestCase {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SystemConfigRepository sysConfigRepo;

    private Validator validator;
    private MockMvc mMvcMock;

    private final String urlTemplate = "/admin/system";

    @Before
    public void setUp() throws Exception {
        if (mMvcMock == null) {
            mMvcMock = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        apiEndpoint = String.format(Locale.getDefault(), "http://localhost:%d/admin/system", port);

        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        // 初期状態で存在する管理者ユーザーをとりあえず消す
        Optional<Users> adminOptional = userRepo.findById("admin");
        adminOptional.ifPresent(users -> userRepo.deleteById("admin"));

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

        SystemConfig systemConfig = new SystemConfig(AppConstants.SysConfig.APP_TITLE, "PIMS Beta");
        sysConfigRepo.save(systemConfig);
    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
        sysConfigRepo.deleteAll();
    }

    @Test
    @WithAnonymousUser
    public void 非ログインではページを表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(urlTemplate)).andDo(print());

        actions.andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "user1", roles = {"USER"})
    public void 一般ユーザーだとページを表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(urlTemplate)).andDo(print());

        actions.andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void 管理者ユーザーだとページを表示できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(urlTemplate)).andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/system_preference")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        SystemPreferenceForm frm = (SystemPreferenceForm) modelMap.get("systemPreferenceForm");

        assertThat(frm, is(notNullValue()));

        assertThat(frm.getAppTitle(), is("PIMS Beta"));
    }

    @Test
    @WithMockUser(value = "kanrisha1", roles = {"ADMIN"})
    public void システム定数を更新できる() throws Exception {
        ResultActions actions = mMvcMock.perform(post(urlTemplate + "/update")
                .with(csrf())
                .param("updateBtn", "更新")
                .param("appTitle", "pims alpha"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(is("/admin/system_preference")))
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        SystemPreferenceForm frm = (SystemPreferenceForm) modelMap.get("systemPreferenceForm");

        assertThat(frm, is(notNullValue()));
        assertThat(frm.getAppTitle(), is("pims alpha"));

        SystemConfig sc = sysConfigRepo.findById(AppConstants.SysConfig.APP_TITLE).get();
        assertThat(sc.getConfigValue(), is("pims alpha"));

        String infoMsg = (String) modelMap.get("infoMsg");
        assertThat(infoMsg, is("システム定数を更新しました。"));
    }
}