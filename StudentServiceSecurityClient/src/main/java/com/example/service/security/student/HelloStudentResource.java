package com.example.service.security.student;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping(produces={"application/json","application/xml"})
public class HelloStudentResource {

	@GetMapping("/helloStudent")
	public String helloStudent() {
		return "Hello Student!!!";
	}
	
	@GetMapping(value ="/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public Student json() {
		return new Student(101, "Arun");
	}
	

}
