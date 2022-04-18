package com.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class HutoolLearnApplication {

	public static void main(String[] args) {
		SpringApplication.run(HutoolLearnApplication.class, args);
	}

}
