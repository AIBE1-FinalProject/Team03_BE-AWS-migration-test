package com.team03.ticketmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class TicketmonGoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketmonGoApplication.class, args);
	}

}
