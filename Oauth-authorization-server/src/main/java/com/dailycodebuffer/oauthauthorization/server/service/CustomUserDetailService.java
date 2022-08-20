package com.dailycodebuffer.oauthauthorization.server.service;

import com.dailycodebuffer.oauthauthorization.server.entity.User;
import com.dailycodebuffer.oauthauthorization.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
// Step 1: Load the user from the DB and redirect to spring security

/* 1- We have implemented UserDetailsService SpringFramework security
   2- Override its method 'loadUserByUsername' to get the user from DB and
      to pass to the spring security to handle the user:
    Note: Without implementing this UserDetailsService spring-security will not know about user that we have in the DB
    That is why we have added User entity and UserRepository here in this application

   3-
 */

@Transactional
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(11);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // find the user by email from DB
        User user = userRepository.findUserByEmail(email);
        if(user==null){
            throw new UsernameNotFoundException("No User Found");
        }else {
            // create and return the User object  from UserDetails
            return new org.springframework.security.core.userdetails.User(
                    // Pass the detail
                    user.getEmail(),
                    user.getPassword(),
                    user.isEnabled(),
                    true,
                    true,
                    true,
                    getAuthorities(Collections.singletonList((user.getRole())))
            );
        }
    }
    // Based on the roles let's create the authorities
    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles){

        List<GrantedAuthority> grantedAuthorities= new ArrayList<>();
        for(String role: roles){
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        return grantedAuthorities;
    }
}

// Now the user became the part of spring security and now whenever required the user will take from here

/*
   Step2- Now needs to implement the configuration i.e. we need to add configuration for
   our authorization server. Note this implementation would be standard for all the
   authorization servers so here we just need to use the bunch of code which already written,
   we just need to use here. Here we will create public key, private key and others to handles authorization server
   so that our clients can connect to it and exchange between the  authorization code token can be happen
Now let's create the 'AuthorizationServerConfig' in the config package.
*/
