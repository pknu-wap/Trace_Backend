package com.example.trace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TraceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TraceApplication.class, args);
	}

}
