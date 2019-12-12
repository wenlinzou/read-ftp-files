package com.mq.ftp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wenlinzou
 */
@SpringBootApplication
@MapperScan("com.mq.ftp.dao")
@EnableScheduling
public class FtpDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(FtpDataApplication.class, args);

    }
}
