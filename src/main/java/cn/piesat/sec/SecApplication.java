package cn.piesat.sec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"cn.piesat"})
@EnableConfigurationProperties
public class SecApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecApplication.class, args);
    }

}
