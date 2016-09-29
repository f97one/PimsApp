/**
 * 
 */
package net.formula97.webapp.pims.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.net.URI;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import net.formula97.webapp.pims.BaseTestCase;

/**
 * タイトル画面Controllerのテストケース。
 * 
 * @author f97one
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
                properties = {"server.port:0", "spring.profiles.active:test"})
public class TitleControllerTest extends BaseTestCase {

    @InjectMocks
    private TitleController mController;
    
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

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.mMvcMock = MockMvcBuilders.standaloneSetup(this.mController).build();
        apiEndpoint = String.format(Locale.getDefault(), "http://localhost:%d/", port);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void 普通に起動すると公開データだけ表示される() throws Exception {

        URI uri = new URI(apiEndpoint);
        this.mMvcMock.perform(MockMvcRequestBuilders.get(uri))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("title"));
    }

}
