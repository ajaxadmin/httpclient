import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步执行HTTP请求
 */
public class HttpClient {

    public static void main(String[] args) {
        int sum=1000000;
        Scanner scanner = new Scanner(System.in);
        int th = scanner.nextInt();
        ExecutorService ex = Executors.newFixedThreadPool(th);
        for(int i =0 ; i< sum; i++){
            ex.submit(new HttpClient_sm2());
            //ex.submit(new HttpClient_rsa());
            //new Thread(new HttpClient_sm2()).start();
        }
    }

}
