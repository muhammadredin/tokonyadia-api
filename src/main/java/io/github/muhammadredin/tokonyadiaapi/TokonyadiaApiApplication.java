package io.github.muhammadredin.tokonyadiaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TokonyadiaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TokonyadiaApiApplication.class, args);
    }

}
