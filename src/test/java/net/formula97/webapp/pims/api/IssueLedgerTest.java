/**
 * 
 */
package net.formula97.webapp.pims.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import net.formula97.webapp.pims.BaseTestCase;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;

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
        
        IssueLedger il1 = new IssueLedger();
        il1.setLedgerId(1);
        il1.setIsPublic(true);
        il1.setLedgerName("公開台帳１");
        il1.setOpenStatus(1);
        
        IssueLedger il2 = new IssueLedger();
        il2.setLedgerId(2);
        il2.setIsPublic(true);
        il2.setLedgerName("公開台帳２");
        il2.setOpenStatus(2);
        
        IssueLedger il3 = new IssueLedger();
        il3.setLedgerId(3);
        il3.setIsPublic(false);
        il3.setLedgerName("非公開台帳１");
        il3.setOpenStatus(3);
        
        issueLedgerRepository.save(Arrays.asList(il1, il2, il3));
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
        assertThat(il1.getIsPublic(), is(true));
        assertThat(il1.getOpenStatus(), is(1));
        
        IssueLedger il2 = resp.getBody().get(1);
        assertThat(il2.getLedgerName(), is("公開台帳２"));
        assertThat(il2.getIsPublic(), is(true));
        assertThat(il2.getOpenStatus(), is(2));

    }

}
