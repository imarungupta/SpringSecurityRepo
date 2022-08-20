package com.dailycodebuffer.oauthauthorization.server.config;

// So we have completed the authorization server configuration
// Step-5:-Now let's do the configuration for Basic spring security, so let's add the default basic security configration
// Let's create on class 'DefaultSecurityConfig'

import com.dailycodebuffer.oauthauthorization.server.service.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class DefaultSecurityConfig {

    //Step-7.1
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
                .formLogin(withDefaults());
        return http.build();
    }
    //Step-7.2
    // Binding CustomAuthenticationProvider with AuthenticationManagerBuilder
    @Autowired
    public void bindAuthenticationProvider(AuthenticationManagerBuilder authenticationManagerBuilder){
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
    }

}

// Here in the above method we are change the authorization request .i.e all the request must be authenticated with fromLogin
// Now one thing is left that is 'authentication provider' like authentication manager, how you should be managing your authentication
// and for that we will create the CustomAuthenticationProvider for userid and password i.e.(email and password)
    // Step6- Create the CustomAuthenticationProvider in the service package for authenticating User credential

// After step 7, now Authorization server is ready, all the configurations are done, handeled all the request
// Now we need to register our client to talk to this authorization server. Every Authorization server will give the detail to handle
// everything in your system. So let's go to AuthorizationServerConfig ==> RegisteredClientRepository and take the detail from hter
// Now let's go to our client spring-security-client module and add configuration in application.yml

// Step-8
