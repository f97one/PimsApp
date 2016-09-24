/**
 * 
 */
package net.formula97.webapp.pims.api;

import static org.junit.Assert.*;

import org.hamcrest.CoreMatchers;
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
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPublicLedgerで公開台帳を取得できる() {
        fail("Not yet implemented");
        
        ResponseEntity<Page<IssueLedger>> actualPublicLedger = restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null, new ParameterizedTypeReference<Page<IssueLedger>>() {
        });
        
        assertThat(actualPublicLedger.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        
        
    }

}
