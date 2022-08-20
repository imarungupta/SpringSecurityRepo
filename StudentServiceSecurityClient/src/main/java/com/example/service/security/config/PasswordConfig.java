package com.example.service.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig{
	
@Bean
public PasswordEncoder passwordEncoder() {
	//return new BCryptPasswordEncoder(strength)
	return new BCryptPasswordEncoder(10);
	// Here we are passing the integer as strength which encode the password ten times
	// Here we are using BCrypt mechanism to encrypt the password which is very strongest mechanism now a days to encrypt password
	// This internally uses String encode(CharSequence rawPassword); and 
}
}
