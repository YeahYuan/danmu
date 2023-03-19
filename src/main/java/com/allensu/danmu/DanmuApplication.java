package com.allensu.danmu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.allensu.danmu.mapper")
public class DanmuApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanmuApplication.class, args);
    }

}
