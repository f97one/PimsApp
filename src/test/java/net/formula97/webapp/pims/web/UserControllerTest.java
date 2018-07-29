package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.repository.UserRepository;
import net.formula97.webapp.pims.web.forms.UserConfigForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class UserControllerTest extends BaseTestCase {
    private MockMvc mMvcMock;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
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
        user1.setEnabled(true);
        userRepo.save(user1);
    }

    @After
    public void tearDown() {
        userRepo.deleteAll();
    }


    @Test
    @WithMockUser(username = "user1")
    public void 自身のユーザー詳細を表示できる() throws Exception {
        String endPoint = apiEndpoint + "user/config";
        URI uri = new URI(endPoint);
        ResultActions actions = mMvcMock.perform(get(uri))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name("/user/userdetail"))
                .andExpect(model().hasNoErrors())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();
        UserConfigForm userConfigForm = (UserConfigForm) modelMap.get("userConfigForm");

        assertThat("ユーザーIDはuser1", userConfigForm.getUsername(), is("user1"));
        assertThat("表示名はJUnit test", userConfigForm.getDisplayName(), is("JUnit test"));
        assertThat("RoleはUSER", userConfigForm.getAssignedRole(), is(AppConstants.ROLE_USER));
    }

}
