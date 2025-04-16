package com.kvantino.gizmochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GizmoChatApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GizmoChatApiApplication.class, args);
	}

}
