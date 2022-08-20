package com.example.service.security.jwt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

// This authenticatioManager will use to authenticate credentials
private final AuthenticationManager authenticationManager;

public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
}

//Step1- override AttemptAuthenticaion method 
@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
	// TODO Auto-generated method stub
	// Take the username & password from the request and mapped it to UsernameAndPasswordAuthenticationRequest using ObjectMapper.readValue() 
	try {
		UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper().readValue(request.getInputStream(),
				                                                                                      UsernameAndPasswordAuthenticationRequest.class);
		
		// Step-2 Now after getting the credential, let's authenticate and for that we will use AuthenticationManager and inject it into constructor 
		//Authentication authenticate = null; 
		//Note: this Authentication is an interface we will use its one of the implementation which is UsernamePasswordAuthenticationToken(springframeWork.security.authentication) 
		// instead of assigning null lets takes its implementation class 
		//Authentication authenticate = new UsernamePasswordAuthenticationToken(principal, credentials, authorities); 
		// username is principal and password is credential and authorities is not mandatory
		
		Authentication getUsernamePasswordToAuthenticate = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
																								   authenticationRequest.getPassword()); 
		// Now pass this getUsernamePasswordToAuthenticate to authenticationManager which will authenticate this username and password and 
		// this will make sure that username and password is exist and validate
		
		Authentication authenticate = authenticationManager.authenticate(getUsernamePasswordToAuthenticate);
		return authenticate;
		// So till we have completed two steps 
		// 1- Got the credentials from the client
		// 2- Then authenticate this credential by overriding Authentication interface and its implementation method UsernamePasswordAuthenticationToken
		// Now the next step is to generate the token and send this token to client
	} catch (IOException e) {
		//e.printStackTrace();
		throw new RuntimeException(e);
	}
  }	
@Override
protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {

	// Generate the token 
   String secureKey = "SecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKey"; // make sure this key should be long enough to generate big encoded value 
	
   String token = Jwts.builder()       // Returns a new JwtBuilder instance that can be configured and then used to create JWT compact serialized strings.
					    .setSubject(authResult.getName())   // so set the username or principal : Looks like header part 
					    .claim("Authorities", authResult.getAuthorities())                   // Payload or body part
					    .setIssuedAt(new Date())
					    .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2))) // Token will expires in 2 weeks 
					    .signWith(Keys.hmacShaKeyFor(secureKey.getBytes())) // After Body now let's add the sign the token 
					    .compact();                                           // and finally compact this token 
	
   // Now send this token to client simply by using response header 
   
   response.addHeader("Authorization", "Bearer"+token);
}
}
