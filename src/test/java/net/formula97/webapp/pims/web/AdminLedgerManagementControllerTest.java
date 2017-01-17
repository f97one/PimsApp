package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.web.forms.LedgerSearchConditionForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specifications;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by f97one on 17/01/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class AdminLedgerManagementControllerTest extends BaseTestCase {

    @InjectMocks
    private AdminLedgerManagementController mController;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    IssueLedgerRepository issueLedgerRepo;
    @Autowired
    IssueItemsRepository issueItemsRepo;
    @Autowired
    LedgerRefUserRepository ledgerRefUserRepo;
    @Autowired
    UserRepository userRepo;

    private MockMvc mMvcMock;
    private Validator validator;

    private int existingLedgerId;
    private int existingIssueId;

    @Before
    public void setUp() throws Exception {
        if (mMvcMock == null) {
            mMvcMock = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
        apiEndpoint = "/admin/ledgerManagement";
        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }

        Users admin1 = new Users();
        admin1.setUsername("kanrisha1");
        admin1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        admin1.setAuthority("ADMIN");
        admin1.setEnabled(true);
        admin1.setDisplayName("管理者１");
        admin1.setMailAddress("kanrisha1@example.com");
        userRepo.save(admin1);

        Users user1 = new Users();
        user1.setUsername("user1");
        user1.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        user1.setAuthority("USER");
        user1.setEnabled(true);
        user1.setDisplayName("ユーザー１");
        user1.setMailAddress("user1@example.com");
        userRepo.save(user1);

        IssueLedger ledger = new IssueLedger();
        ledger.setLedgerName("非公開台帳１");
        ledger.setPublicLedger(false);
        ledger.setOpenStatus(1);
        issueLedgerRepo.save(ledger);

        MySpecificationAdapter<IssueLedger> issueLedgerSpec = new MySpecificationAdapter<>(IssueLedger.class);
        IssueLedger savedLedger = issueLedgerRepo.findOne(Specifications.where(issueLedgerSpec.eq("ledgerName", "非公開台帳１")));
        this.existingLedgerId = savedLedger.getLedgerId();

        IssueLedger ledger1 = new IssueLedger();
        ledger1.setLedgerName("公開台帳1");
        ledger1.setPublicLedger(true);
        ledger1.setOpenStatus(2);
        issueLedgerRepo.save(ledger1);

        IssueLedger ledger2 = new IssueLedger();
        ledger2.setLedgerName("Ledger1");
        ledger2.setPublicLedger(true);
        ledger2.setOpenStatus(3);
        issueLedgerRepo.save(ledger2);

    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
        issueLedgerRepo.deleteAll();
    }

    @Test
    public void 台帳検索Formにバリデーションが働いている() throws Exception {
        LedgerSearchConditionForm form = new LedgerSearchConditionForm();
        form.setLedgerName("1234567890123456789012345678901234567890123456789012345678901234");
        List<Integer> statusList = new ArrayList<>();
        form.setLedgerStatus(statusList);

        Set<ConstraintViolation<LedgerSearchConditionForm>> violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        Optional<ConstraintViolation<LedgerSearchConditionForm>> violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof NotNull).findFirst();
        assertThat("NotNullエラーが検知されている", violationOptional.isPresent(), is(true));

        form.setLedgerName(form.getLedgerName() + "5");
        form.setPublicStatus(1);
        violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof Size).findFirst();
        assertThat("Sizeエラーが検知されている", violationOptional.isPresent(), is(true));

        form.setLedgerName("1234567890123456789012345678901234567890123456789012345678901234");
        form.setPublicStatus(0);
        violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof Min).findFirst();
        assertThat("Minエラーが検知されている", violationOptional.isPresent(), is(true));

        form.setPublicStatus(4);
        violationSet = validator.validate(form);

        assertThat("エラーは1件", violationSet.size(), is(1));
        violationOptional =
                violationSet.stream().filter(r -> r.getConstraintDescriptor().getAnnotation() instanceof Max).findFirst();
        assertThat("Maxエラーが検知されている", violationOptional.isPresent(), is(true));
    }

    @Test
    @WithAnonymousUser
    public void 非ログインだと台帳検索画面を表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(apiEndpoint)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().is3xxRedirection()).andReturn();
    }

    @Test
    @WithMockUser(username = "user11", roles = "USER")
    public void 一般ユーザーだと台帳検索画面を表示できない() throws Exception {
        ResultActions actions = mMvcMock.perform(get(apiEndpoint)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isForbidden()).andReturn();
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    public void 管理者ユーザーなら台帳検索画面を表示できる() throws Exception {
        ResultActions actions = mMvcMock.perform(get(apiEndpoint)).andDo(print());
        MvcResult mvcResult = actions.andExpect(status().isOk())
                .andExpect(view().name(is("/admin/ledger_list")))
                .andReturn();

    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 台帳名で検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "公開台帳")
                .param("ledgerStatus", "")
                .param("publicStatus", "3"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("２件ヒットしている", ledgerList.size(), is(2));

        Optional<IssueLedger> issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("非公開台帳１")).findFirst();
        assertThat("非公開台帳１が見つかっている", issueLedgerOpt.isPresent(), is(true));
        issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("公開台帳1")).findFirst();
        assertThat("公開台帳1が見つかっている", issueLedgerOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 公開台帳だけ検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "")
                .param("ledgerStatus", "")
                .param("publicStatus", "1"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("２件ヒットしている", ledgerList.size(), is(2));

        Optional<IssueLedger> issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("公開台帳1")).findFirst();
        assertThat("公開台帳1が見つかっている", issueLedgerOpt.isPresent(), is(true));
        issueLedgerOpt = ledgerList.stream().filter(r -> r.getLedgerName().equals("Ledger1")).findFirst();
        assertThat("Ledger1が見つかっている", issueLedgerOpt.isPresent(), is(true));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void 非公開台帳だけを検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "")
                .param("ledgerStatus", "")
                .param("publicStatus", "2"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("１件ヒットしている", ledgerList.size(), is(1));
        assertThat("非公開台帳１が見つかっている", ledgerList.get(0).getLedgerName(), is("非公開台帳１"));
    }

    @Test
    @WithMockUser(username = "kanrisha1", roles = "ADMIN")
    @SuppressWarnings("unchecked")
    public void ステータスで検索できる() throws Exception {
        String template = String.format(Locale.getDefault(), "%s/search", apiEndpoint);
        ResultActions actions = mMvcMock.perform(get(template)
                .param("searchLedgerBtn", "検索")
                .param("ledgerName", "")
                .param("ledgerStatus", "1", "2")
                .param("publicStatus", "3"))
                .andDo(print());

        MvcResult mvcResult = actions.andExpect(view().name(is("/admin/ledger_list")))
                .andExpect(status().isOk())
                .andReturn();

        ModelMap modelMap = mvcResult.getModelAndView().getModelMap();

        List<IssueLedger> ledgerList = (List<IssueLedger>) modelMap.get("ledgerList");
        assertThat("2件ヒットしている", ledgerList.size(), is(2));
        Optional<IssueLedger> issueLedgerOpt = ledgerList.stream().filter(r -> r.getOpenStatus() == 1).findFirst();
        assertThat("ステータス1の台帳が見つかっている", issueLedgerOpt.isPresent(), is(true));
        issueLedgerOpt = ledgerList.stream().filter(r -> r.getOpenStatus() == 2).findFirst();
        assertThat("ステータス2の台帳が見つかっている", issueLedgerOpt.isPresent(), is(true));
    }


}