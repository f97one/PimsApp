package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.web.forms.LedgerSearchConditionForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class IssueLedgerServiceTest {

    @Autowired
    private IssueLedgerRepository issueLedgerRepo;
    @Autowired
    private IssueItemsRepository issueItemsRepo;
    @Autowired
    private LedgerRefUserRepository ledgerRefUserRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private IssueLedgerService issueLedgerService;

    private int existingLedgerId;

    @Before
    public void setUp() {
        // テスト用ユーザーの追加
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

        Users disabledUser = new Users();
        disabledUser.setUsername("disabled1");
        disabledUser.setPassword(BCrypt.hashpw("P@ssw0rd", BCrypt.gensalt()));
        disabledUser.setAuthority("USER");
        disabledUser.setEnabled(false);
        disabledUser.setDisplayName("無効ユーザー1");
        disabledUser.setMailAddress("disabled1@example.com");
        userRepo.save(disabledUser);

        // テスト用台帳の追加
        IssueLedger ledger = new IssueLedger();
        ledger.setLedgerName("非公開台帳１");
        ledger.setPublicLedger(false);
        ledger.setOpenStatus(1);
        issueLedgerRepo.save(ledger);

        MySpecificationAdapter<IssueLedger> issueLedgerSpec = new MySpecificationAdapter<>(IssueLedger.class);
        IssueLedger savedLedger = issueLedgerRepo.findOne(Specifications.where(issueLedgerSpec.eq("ledgerName", "非公開台帳１"))).get();
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

        LedgerRefUser ledgerRefUser = new LedgerRefUser(this.existingLedgerId, user1.getUsername());
        ledgerRefUserRepo.save(ledgerRefUser);

        IssueItems items = new IssueItems();
        items.setActionStatusId(1);
        items.setSevereLevelId(2);
        items.setLedgerId(this.existingLedgerId);
        items.setFoundUser("user1");
        items.setFoundDate(new Date());
        items.setCategoryId(3);
        items.setFoundProcessId(3);
        items.setIssueDetail("Hoge処理が動かない。なんとかしろ");
        items.setRowUpdatedAt(new Date());
        issueItemsRepo.save(items);
    }

    @After
    public void tearDown() {
        ledgerRefUserRepo.deleteAll();
        userRepo.deleteAll();
        issueLedgerRepo.deleteAll();
    }

    @Test
    public void 条件空だとすべての結果が返る() {
        LedgerSearchConditionForm frm1 = new LedgerSearchConditionForm();
        frm1.setLedgerName("");
        frm1.setLedgerStatus(new ArrayList<>());

        List<IssueLedger> resultList1 = issueLedgerService.getLedgerByList(frm1);
        assertThat("結果は3件", resultList1.size(), is(3));

        List<IssueLedger> resultList2 = issueLedgerService.getLedgerByList(new LedgerSearchConditionForm());
        assertThat("結果はかわらず3件", resultList2.size(), is(3));
    }

    @Test
    public void 台帳名だけを指定した場合見合った結果が返る() {
        LedgerSearchConditionForm frm1 = new LedgerSearchConditionForm();
        frm1.setLedgerName("非公開");

        List<IssueLedger> resultList1 = issueLedgerService.getLedgerByList(frm1);
        assertThat("結果は1件", resultList1.size(), is(1));
    }

    @Test
    public void 台帳ステータスだけを指定した場合見合った結果が返る() {
        List<Integer> stList = new ArrayList<>();
        stList.add(1);
        stList.add(3);
        LedgerSearchConditionForm frm1 = new LedgerSearchConditionForm();
        frm1.setLedgerStatus(stList);

        List<IssueLedger> resultList1 = issueLedgerService.getLedgerByList(frm1);
        assertThat("結果は2件", resultList1.size(), is(2));

        Optional<IssueLedger> opt1 = resultList1.stream().filter((r) -> r.getOpenStatus().equals(1)).findFirst();
        assertTrue("公開ステータス1の台帳がある", opt1.isPresent());

        Optional<IssueLedger> opt2 = resultList1.stream().filter((r) -> r.getOpenStatus().equals(3)).findFirst();
        assertTrue("公開ステータス2の台帳がある", opt2.isPresent());
    }

    @Test
    public void 公開ステータスだけを指定した場合見合った結果が返る() {
        LedgerSearchConditionForm frm1 = new LedgerSearchConditionForm();
        // 公開のみ
        frm1.setPublicStatus(1);

        List<IssueLedger> resultList1 = issueLedgerService.getLedgerByList(frm1);
        assertThat("結果は2件", resultList1.size(), is(2));

        Optional<IssueLedger> opt1 = resultList1.stream().filter((r) -> r.getLedgerName().equals("公開台帳1")).findFirst();
        assertTrue("公開台帳1が見つかった", opt1.isPresent());

        Optional<IssueLedger> opt2 = resultList1.stream().filter((r) -> r.getLedgerName().equals("Ledger1")).findFirst();
        assertTrue("Ledger1が見つかった", opt2.isPresent());

        // 非公開のみ
        frm1.setPublicStatus(2);

        List<IssueLedger> resultList2 = issueLedgerService.getLedgerByList(frm1);
        assertThat("結果は1件", resultList2.size(), is(1));

        Optional<IssueLedger> opt3 = resultList2.stream().filter((r) -> r.getLedgerName().equals("非公開台帳１")).findFirst();
        assertTrue("非公開台帳１が見つかった", opt3.isPresent());

        // 公開／非公開あわせて
        frm1.setPublicStatus(3);

        List<IssueLedger> resultList3 = issueLedgerService.getLedgerByList(frm1);
        assertThat("結果は3件", resultList3.size(), is(3));
    }
}