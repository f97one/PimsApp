/**
 * 
 */
package net.formula97.webapp.pims;

import java.util.List;
import java.util.Locale;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page<T> {
        private List<T> content;
        private int numberOfElements;
        /**
         * @return the content
         */
        public List<T> getContent() {
            return content;
        }
        /**
         * @param content the content to set
         */
        public void setContent(List<T> content) {
            this.content = content;
        }
        /**
         * @return the numberOfElements
         */
        public int getNumberOfElements() {
            return numberOfElements;
        }
        /**
         * @param numberOfElements the numberOfElements to set
         */
        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }
    }
    
}
