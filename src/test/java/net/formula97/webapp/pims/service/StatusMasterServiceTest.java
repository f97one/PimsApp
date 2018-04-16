package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.StatusMaster;
import net.formula97.webapp.pims.repository.StatusMasterRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.port:0", "spring.profiles.active:test"})
public class StatusMasterServiceTest {

    @Autowired
    private StatusMasterRepository statusMasterRepo;
    @Autowired
    private StatusMasterService statusMasterService;

    @Before
    public void setUp() throws Exception {
        resetStatusMaster();
    }

    /**
     * ステータスマスタを初期化する。
     */
    private void resetStatusMaster() {
        statusMasterRepo.deleteAll();

        StatusMaster stm1 = new StatusMaster(1, "新規", 0, false);
        statusMasterRepo.save(stm1);

        StatusMaster stm2 = new StatusMaster(2, "進行中", 1, false);
        statusMasterRepo.save(stm2);

        StatusMaster stm3 = new StatusMaster(3, "解決", 2, false);
        statusMasterRepo.save(stm3);

        StatusMaster stm4 = new StatusMaster(4, "終了", 3, true);
        statusMasterRepo.save(stm4);

        StatusMaster stm5 = new StatusMaster(5, "却下", 4, true);
        statusMasterRepo.save(stm5);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void 初期状態のステータスIDの最小値は1() {
        assertThat(statusMasterService.getMinimumStatus(), is(1));
    }

    @Test
    public void データがないときはMIN_VALUEを返される() {
        // いったん全消し
        statusMasterRepo.deleteAll();

        assertThat(statusMasterService.getMinimumStatus(), is(Integer.MIN_VALUE));

        // 元に戻す
        resetStatusMaster();
    }

    @Test
    public void 最小値が変わったらその値になる() {
        // ステータスID = 1 をいったん削除
        StatusMaster n1Item = new StatusMaster();
        n1Item.setStatusId(1);
        statusMasterRepo.delete(n1Item);

        assertThat(statusMasterService.getMinimumStatus(), is(2));

        // 元に戻す
        resetStatusMaster();
    }
}