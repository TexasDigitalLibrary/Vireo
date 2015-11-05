///* 
// * AuthServerInit.java 
// * 
// * Version: 
// *     $Id$ 
// * 
// * Revisions: 
// *     $Log$ 
// */
//package edu.tamu.auth;
//
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.context.web.SpringBootServletInitializer;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//
///** 
// * Authorization server initialization.
// * 
// * @author
// *
// */
//@ComponentScan
//@Configuration
//@EnableAutoConfiguration
//@EnableConfigurationProperties
//public class AuthServerInit extends SpringBootServletInitializer {
//
//	/**
//	 * Entry point to the application from within servlet.
//	 *
//	 * @param       args    		String[]
//	 *
//	 */
//    public static void main(String[] args) {
//        SpringApplication.run(AuthServerInit.class, args);
//    }
//    
//    /**
//	 * Entry point to the application if run using spring-boot:run.
//	 *
//	 * @param       application    	SpringApplicationBuilder
//	 *
//	 * @return		SpringApplicationBuilder
//	 *
//	 */
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(AuthServerInit.class);
//    }
//
//}