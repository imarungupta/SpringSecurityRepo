package com.example.service.security.student;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {

	//Create a static list of Students
	private static final List<Student>STUDENTS= Arrays.asList(
			new Student(1,"James Bond"),
			new Student(2,"Maria Jones"),
			new Student(3,"Anna Smith")			
			);	
	/**  this commented line taken from ApplicationSecurityConfiguration.java file just for reference to mention in the @PreAuthorize 
	 * 
	.antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(ApplicationUserPermission.COURCE_WRITE.getPermission())
   .antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(ApplicationUserPermission.COURCE_WRITE.getPermission())
   .antMatchers(HttpMethod.PUT,"/management/api/**").hasAuthority(ApplicationUserPermission.COURCE_WRITE.getPermission())
   .antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ApplicationUserRole.ADMIN.name(),ApplicationUserRole.ADMINTRANEE.name())
	 **/
	
	// Let's define a method which both linda and tom can access where linda will be having read and write both while tom will have only readonly 
	// We are just simply going to define the CRUD operation On Student object 
	@GetMapping
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ADMINTRANEE')")
	public List<Student> getAllStudents(){
		System.out.println("inside getAllStudents()");
		return STUDENTS;
	}
	@PostMapping
	@PreAuthorize("hasAuthority('cource:write')")
	public void registerNewStudent(@RequestBody Student newStudent) {
		
		System.out.println("inside registerNewStudent()");
		System.out.println(newStudent);
	}
	@DeleteMapping(path="{studentId}")
	@PreAuthorize("hasAuthority('cource:write')")
	public void deleteStudent(@PathVariable("studentId") Integer studentId) {
		System.out.println("inside deleteStudent()");
		System.out.println(studentId);
	}
	@PutMapping(path="{studentId}")
	@PreAuthorize("hasAuthority('cource:write')")
	public void updateStudent(@PathVariable("studentId") Integer studentId, @RequestBody Student studnet) {
		System.out.println("inside updateStudent()");
		System.out.println(String.format("%s %s", studentId,studnet));
	}
}
