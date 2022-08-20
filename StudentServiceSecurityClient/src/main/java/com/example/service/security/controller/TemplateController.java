package com.example.service.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {
	
	@GetMapping(path="/login")
	public String getLoginView() {
		return"login";
	}
	
	@GetMapping(path="/cources")
	public String getCources() {
		return"cources";
	}
}
