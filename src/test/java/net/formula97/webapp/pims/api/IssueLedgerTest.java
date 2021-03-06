/**
 * 
 */
package net.formula97.webapp.pims.api;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 課題台帳APIのテストケース。
 * 
 * @author f97one
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
                properties = {"server.port:0", "spring.profiles.active:test"})
public class IssueLedgerTest extends BaseTestCase {
    
    @Autowired
    private IssueLedgerRepository issueLedgerRepository;
    
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
        apiEndpoint = getApiEndpoint("api", "ledger");
        
        issueLedgerRepository.deleteAll();
        
        IssueLedger il1 = new IssueLedger();
        il1.setLedgerId(11);
        il1.setPublicLedger(true);
        il1.setLedgerName("公開台帳１");
        il1.setOpenStatus(1);
        
        IssueLedger il2 = new IssueLedger();
        il2.setLedgerId(12);
        il2.setPublicLedger(true);
        il2.setLedgerName("公開台帳２");
        il2.setOpenStatus(2);
        
        IssueLedger il3 = new IssueLedger();
        il3.setLedgerId(13);
        il3.setPublicLedger(false);
        il3.setLedgerName("非公開台帳１");
        il3.setOpenStatus(3);

        issueLedgerRepository.saveAll(Arrays.asList(il1, il2, il3));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        issueLedgerRepository.deleteAll();
    }

    @Test
    public void getPublicLedgerで公開台帳を取得できる() {
        ResponseEntity<List<IssueLedger>> resp = restTemplate.exchange(
                apiEndpoint,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<IssueLedger>>() { });
        
        
        assertThat(resp.getStatusCode(), is(HttpStatus.OK));
        
        assertThat(resp.getBody().size(), is(2));

        IssueLedger il1 = resp.getBody().get(0);
        assertThat(il1.getLedgerName(), is("公開台帳１"));
        assertThat(il1.getPublicLedger(), is(true));
        assertThat(il1.getOpenStatus(), is(1));
        
        IssueLedger il2 = resp.getBody().get(1);
        assertThat(il2.getLedgerName(), is("公開台帳２"));
        assertThat(il2.getPublicLedger(), is(true));
        assertThat(il2.getOpenStatus(), is(2));

    }

}
