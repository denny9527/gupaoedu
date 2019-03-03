package com.gupao.edu.vip.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class JVMTuningApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(JVMTuningApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(JVMTuningApplication.class);
    }
}









