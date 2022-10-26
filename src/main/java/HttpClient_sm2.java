import com.jklxdata.jce.provider.JKunitrustProvider;
import com.jklxdata.jsse.provider.JKunitrustJSSE;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Security;


/**
 * http 工具
 */
public class HttpClient_sm2 implements Runnable{
    private KeyStore keyStore = null;
    private HttpResponse resp;
    private CloseableHttpClient client=null;
    private  Logger logger = LoggerFactory.getLogger(HttpClient_sm2.class);
    private HttpPost httpPost = null;

    public HttpClient_sm2(){
        Security.addProvider(new JKunitrustProvider());
        Security.addProvider(new JKunitrustJSSE());
        try {
            keyStore = KeyStore.getInstance("extJks");
            keyStore.load(new FileInputStream(new File("/opt/789987.jks")), "12345678".toCharArray());
            //keyStore.load(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\789987.jks")), "12345678".toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //String url ="https://192.168.0.21:60005";
        String url ="https://192.168.2.20:60005";
        //String fileurl="D:\\httpclient\\httpclient_bingfa\\src\\main\\java\\msvcp100.dll";
        String fileurl="/opt/msvcp100.dll";
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("GMSSLv1.1", "JKunitrustJSSE");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("extX509", "JKunitrustJSSE");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX", "JKunitrustJSSE");
            kmf.init(keyStore, "12345678".toCharArray());
            tmf.init(keyStore);
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //创建HttpClient对象
        client = HttpClients.custom().setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        //创建HttpPost对象
        httpPost = new HttpPost(url);
        httpPost.setHeader("connection", "keep-alive");
        File file = new File(fileurl);
        String message = "数据体";
        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName())
                .addTextBody("message", message)
                .build();
        //配置请求参数
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        httpPost.setConfig(requestConfig);
        httpPost.setEntity(entity);
    }
    @Override
    public void run() {
            try {
                //执行请求
                resp = client.execute(httpPost);
                if (resp.getStatusLine().getStatusCode() == 200) {
                    HttpEntity responseObj = resp.getEntity();
                    //respContent = EntityUtils.toString(responseObj, "UTF-8");
                    //System.out.println(resp.getStatusLine().getStatusCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("异常信息：" +e);
            }
            //logger.info("结束 请求url：" + url);
        }
    }