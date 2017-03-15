package it.peruvianit.invoke;

import org.springframework.boot.SpringApplication;

import it.peruvianit.configuration.BatchConfiguration;

public class Application {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BatchConfiguration.class, args);
	}
}