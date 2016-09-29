/**
 * 
 */
package net.formula97.webapp.pims;

import java.util.Locale;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author f97one
 *
 */
@RunWith(SpringRunner.class)
public abstract class BaseTestCase {
    
    /**
     * テストケース用RestTemplate
     */
    @Autowired
    protected TestRestTemplate restTemplate;
    
    /**
     * 接続ポート
     */
    @Value("${local.server.port}")
    protected int port;
    
    /**
     * モックオブジェクトルール
     */
    @Rule
    public final MockitoRule mockRule = MockitoJUnit.rule();
    
    /**
     * API Endpoint格納変数
     */
    protected String apiEndpoint;
    
    /**
     * API EndpointとなるURLを作る。
     * 
     * @param contextBaseName 
     * @param apiName
     * @return
     */
    protected String getApiEndpoint(String contextBaseName, String apiName) {
        return String.format(Locale.getDefault(), "http://localhost:%d/%s/%s", port, contextBaseName, apiName);
    }
    
}
