package com.gameacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class GameAcademyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameAcademyApplication.class, args);
    }

}
