package com.campusmenu.mess_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MessManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessManagementApplication.class, args);
		System.out.println("===========================================");
		System.out.println("Mess Management System Started Successfully");
		System.out.println("===========================================");
	}

}
