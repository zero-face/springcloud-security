import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Zero
 * @Date 2021/7/15 22:52
 * @Since 1.8
 * @Description
 **/
@SpringBootApplication()
@EnableDiscoveryClient
public class GateWay {
    public static void main(String[] args) {
        SpringApplication.run(GateWay.class, args);
    }
}
