package lo.poc.camunda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"lo.poc.camunda"})
public class PocCamundaApplication {
    public static void main(String... args) {
        SpringApplication.run(PocCamundaApplication.class, args);
    }
}
