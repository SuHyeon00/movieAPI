package team15.potato;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing // Spring Batch 기능 사용 활성화
public class PotatoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PotatoApplication.class, args);
    }

}
