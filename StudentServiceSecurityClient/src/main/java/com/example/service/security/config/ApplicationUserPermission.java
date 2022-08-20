package com.example.service.security.config;

public enum ApplicationUserPermission {

	STUDENT_READ("student:read"),
	STUDENT_WRITE("student:write"),
	COURCE_READ("cource:read"),
	COURCE_WRITE("cource:write");
	
	private final String permission;

	// Constructor
	ApplicationUserPermission(String permission) {
		this.permission = permission;
	} 
	// Getter of final variable
	public String getPermission() {
		return permission; 
	}	
}
