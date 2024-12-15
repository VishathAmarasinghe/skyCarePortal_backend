package com.skycareSolutions.skyCarePortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkyCarePortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyCarePortalApplication.class, args);
	}

}
