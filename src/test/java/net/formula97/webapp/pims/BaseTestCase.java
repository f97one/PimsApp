/**
 * 
 */
package net.formula97.webapp.pims;

import net.formula97.webapp.pims.domain.Users;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

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

    SecurityContext securityContext;

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

    protected void setAuthenticated(Users users) {
        Authentication auth = new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword(), users.getAuthorities());
        securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    protected void releaseAuthenticated() {
        SecurityContextHolder.clearContext();
    }
}
