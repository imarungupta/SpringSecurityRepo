package com.example.service.security.config;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.google.common.collect.Sets;

public enum ApplicationUserRole {

	STUDENTS(Sets.newHashSet()), // Since initally student does not have any permission so created empty sets of permissions
	ADMIN(Sets.newHashSet(ApplicationUserPermission.COURCE_READ,
			              ApplicationUserPermission.COURCE_WRITE,
			              ApplicationUserPermission.STUDENT_READ)),
	ADMINTRANEE(Sets.newHashSet(ApplicationUserPermission.COURCE_READ,ApplicationUserPermission.STUDENT_READ));
	
	// Create a Set of ApplicationUserPermission class type so that we could have unique permission for each role
	private final Set<ApplicationUserPermission> permissions;
	
	private ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
		this.permissions=permissions;
	}

	public Set<ApplicationUserPermission> getPermissions() {
		return permissions;
	}
	
	// We have created set of SimpleGrantedAuthority type and map all the permissions (declared inside ApplicationUserPermission class) to
	// its implementation class SimpleGrantedAuthority and convert them into Set and now this permissions set contains all the set of permissions 
	// which is declared inside  ApplicationUserPermission class 
	public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
		Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
						.map(permission->new SimpleGrantedAuthority(permission.getPermission()))
						.collect(Collectors.toSet());
		
		permissions.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
		return permissions;
	}
	// This getGrantedAuthorities will called inside .authorities(STUDENT.getGrantedAuthorities()) like that to authenticate the user based on authorities 
	
}
