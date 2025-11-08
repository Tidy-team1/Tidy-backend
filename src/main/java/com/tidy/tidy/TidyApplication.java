package com.tidy.tidy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TidyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TidyApplication.class, args);
	}

}
