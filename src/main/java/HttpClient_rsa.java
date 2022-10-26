import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * http 工具
 */
public class HttpClient_rsa implements Runnable{

    private HttpResponse resp;
    private Logger logger = LoggerFactory.getLogger(HttpClient_rsa.class);
    private HttpPost httpPost;
    CloseableHttpClient client;
    public HttpClient_rsa() {
        //String url ="http://192.168.2.10:60014";
        String url ="http://127.0.0.1:60015";

        //String url ="https://192.168.0.21:60004";

        String fileurl="/opt/msvcp100.dll";
        //String fileurl="D:\\httpclient\\httpclient_bingfa\\src\\main\\java\\msvcp100.dll";
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }


        //logger.info("HttpTool.requestPost 开始 请求url：" + url);
        //创建HttpClient对象
        client = HttpClients.custom().setSslcontext(sslContext).
                setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

        //创建HttpPost对象
        httpPost = new HttpPost(url);

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
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("异常信息：" +e);
            }
            //logger.info("结束 请求url：" + url);
        }
    }


