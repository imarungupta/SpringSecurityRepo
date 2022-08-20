package com.example.service.security.config;

import java.util.concurrent.TimeUnit;


import com.example.service.security.jwt.JwtTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.service.security.jwt.JwtUsernameAndPasswordAuthenticationFilter;

/* http
.authorizeRequests()                       // Authorize user request 
.antMatchers("/","index","/css/*","/js/*").permitAll() // Except these ant matchers // permit all ant matchers without any authentication  
.anyRequest()                              // But Any type of http request 
.authenticated()                           // must be authenticated 
.and()                                     // And 
.httpBasic();                              // I want to use httpBasic() mechanism to authenticate any request
*/
// Here in the below code we have added permission for api for student role. 
/*  http
 .authorizeRequests()
 .antMatchers("/","index","/css/*","/js/*").permitAll()
 .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENTS.name())
 .anyRequest()
 .authenticated()
 .and()
 .httpBasic(); */ 
/*	http
    .csrf().disable()
    .authorizeRequests()
    .antMatchers("/","index","/css/*","/js/*").permitAll()
    .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENTS.name())
    .anyRequest()
    .authenticated()
    .and()
    .httpBasic(); */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter{
		
    private final PasswordEncoder passwordEncoder;
    // Added for DaoAuthenticationProvider when to get user from DB
 //   private final ApplicationUserService applicationUserService;
	//, ApplicationUserService applicationUserService
	@Autowired
	public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder= passwordEncoder;
		//this.applicationUserService = applicationUserService;
	}
		
@Override
protected void configure(HttpSecurity http) throws Exception {
	System.out.println("::::::::::::configure");
	//super.configure(http); we will use http so will comment super.configure	
/* This is role based access for the api
	http
	   .csrf().disable()
	   .authorizeRequests()
	   .antMatchers("/","/css/*","/js/*").permitAll()
	   .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENTS.name())
	   .antMatchers(HttpMethod.DELETE,"/managment/api/**").hasAuthority(ApplicationUserPermission.COURCE_READ.name())
	   .antMatchers(HttpMethod.POST,"/managment/api/**").hasAuthority(ApplicationUserPermission.COURCE_WRITE.name())
	   .antMatchers(HttpMethod.PUT,"/managment/api/**").hasAuthority(ApplicationUserPermission.COURCE_READ.name())
	   .antMatchers(HttpMethod.GET,"/managment/api/**").hasAnyRole(ApplicationUserRole.ADMIN.name(),ApplicationUserRole.ADMINTRANEE.name())
	   .anyRequest()
	   .authenticated()
	   .and()
	   .httpBasic();	 */
// --------------------------------------------------------------------------------------------------------------------------
/*  Permission or authrities based access for the API so instead of .name() we will use getPermission() but for role it will be name() only
	http	   
	   .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
	   .and()
	   .csrf().disable()
	   .authorizeRequests()
	   .antMatchers("/","/css/*","/js/*").permitAll()
	   .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENTS.name())
	   
 //Note:  Let's comment the below antMatcher because we are using @PreAuthorities for the same in the Controller class for authentication and authorization 
 
      .antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(ApplicationUserPermission.COURCE_WRITE.getPermission())
      .antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(ApplicationUserPermission.COURCE_WRITE.getPermission())
      .antMatchers(HttpMethod.PUT,"/management/api/**").hasAuthority(ApplicationUserPermission.COURCE_WRITE.getPermission())
      .antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ApplicationUserRole.ADMIN.name(),ApplicationUserRole.ADMINTRANEE.name())
	  .anyRequest()
	  .authenticated()
	  .and()
	  .httpBasic(); // Basic Authentication*/ 
//-----------------------------------------------------------------------------------------------------------------------------	
 // Commented the above Basic Authentication and now let's Enable Form Based Authentication: 
/*
http
   .csrf().disable()
   .authorizeRequests()
   .antMatchers("/","index","/css/**","/js/**").permitAll()
   .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENTS.name())
   // we can include antMatchers for permission based authentication like we have done for basic auth since we start using Annotation based authorities so we had comment antMatchers
   .anyRequest()
   .authenticated()
   .and()
   .formLogin() // Enabling form based authentication
	   .loginPage("/login").permitAll()
	   .defaultSuccessUrl("/cources", true)
	   .usernameParameter("username-parameter")
	   .passwordParameter("password-parameter")
   .and()
   //.rememberMe(); // Defaults to 2 weeks
   //.rememberMe().tokenRepository(tokenRepository) // in case if we use any database
   .rememberMe()
      .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
      .key("keyforgettingMd5Hash")
      .rememberMeParameter("remember-me-parameter")
   .and()
   .logout()
      .logoutUrl("/logout")         // To call logout resource
      .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
      .clearAuthentication(true)    //  To clear all the authentication
      .invalidateHttpSession(true)  // Make the session invalidate
      .deleteCookies("JSESSIONID","remember-me") // Delete all the sessionid and cookies
      .logoutSuccessUrl("/login");  // Finally redirect to login page 
      */
//------------------------------------------------------------------------------------------------------------------------------------
// Comment the above form based authentication and now let's enable token based authentication
http
   .csrf().disable() 
   .sessionManagement()
   		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // Since JWT uses stateless policy that is why added this line before authentication and now the session would not be store in any database 
   .and()
   .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager()))
		.addFilterAfter(new JwtTokenVerifier(),JwtUsernameAndPasswordAuthenticationFilter.class)
   // add filter before authorize request . This authenticationManager is accessible because of extending WebSecurityConfigurerAdapter above at class and this authenticationManager authenticate the user 
   .authorizeRequests()
   .antMatchers("/","index","/css/**","/js/**").permitAll()
   .antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENTS.name())
   .anyRequest()
   .authenticated();
}
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(daoAuthenticationProvider());
//	}
//@Bean
//public DaoAuthenticationProvider daoAuthenticationProvider(){
//		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//		provider.setPasswordEncoder(passwordEncoder);
//		provider.setUserDetailsService(applicationUserService);
//		return provider;
//}

//Since we are fetching the user detail from the DB FakeApplicationUserDaoService class > getApplicationUserList() so let's either delete this or comment all the code creating Users

@Override
@Bean
protected UserDetailsService userDetailsService() {
//	return super.userDetailsService();
	
// ROLE Based authentication 	
	/** 
	 * UserDetails annasmithUser = User.builder()              // Create user 
                        .username("annasmith")       // with username- Arun
                       // .password("admin123")   // and password - admin123
                        .password(passwordEncoder.encode("admin123"))   // Encoding the password using BCrypt
                        .roles(ApplicationUserRole.STUDENTS.name())//.roles("STUDENT") // whose role is Student and internally it will created as [ROLE_STUDENT] 
                        .build();               // and finally build() // we are using builder pattern to create user
	 // Creating Admin user
	 UserDetails lindaAdmin = User.builder()
						     .username("linda")
						     .password(passwordEncoder.encode("admin@123"))
						      .roles(ApplicationUserRole.ADMIN.name())//.roles("ADMIN")
						     .build();

	 UserDetails tomTraneeUser = User.builder()
			                        .username("tom")
			                        .password(passwordEncoder.encode("admin@123"))
			                        .roles(ApplicationUserRole.ADMINTRANEE.name())
			                        .build();
	 *  **/
	// Permission based authentication or authorities based authentications 	
	
	UserDetails annasmithUser = User.builder()              // Create user 
	                           .username("annasmith")       // with username- Arun
	                          // .password("admin123")   // and password - admin123
	                           .password(passwordEncoder.encode("admin123"))   // Encoding the password using BCrypt
	                           //.roles(ApplicationUserRole.STUDENTS.name())//.roles("STUDENT") // whose role is Student and internally it will created as [ROLE_STUDENT] 
	                           .authorities(ApplicationUserRole.STUDENTS.getGrantedAuthorities())
	                           .build();               // and finally build() // we are using builder pattern to create user
	 // Creating Admin user
	 UserDetails lindaAdmin = User.builder()
						     .username("linda")
						     .password(passwordEncoder.encode("admin@123"))
						     // .roles(ApplicationUserRole.ADMIN.name())//.roles("ADMIN")
						     .authorities(ApplicationUserRole.ADMIN.getGrantedAuthorities())
						     .build();
	 
	 UserDetails tomTraneeUser = User.builder()
			                        .username("tom")
			                        .password(passwordEncoder.encode("admin@123"))
			                        //.roles(ApplicationUserRole.ADMINTRANEE.name())
			                        .authorities(ApplicationUserRole.ADMINTRANEE.getGrantedAuthorities())
			                        .build();
	
	return new InMemoryUserDetailsManager( // just putting the created user in the InMemoery 
			annasmithUser, 
			lindaAdmin,
			tomTraneeUser
			); 
	}


}
