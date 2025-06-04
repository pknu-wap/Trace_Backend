package com.example.trace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@EnableFeignClients
public class TraceApplication {

	static {
		// JVM 시작 시점에 시간대 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		System.setProperty("user.timezone", "Asia/Seoul");
	}
	public static void main(String[] args) {
		SpringApplication.run(TraceApplication.class, args);
	}

}
