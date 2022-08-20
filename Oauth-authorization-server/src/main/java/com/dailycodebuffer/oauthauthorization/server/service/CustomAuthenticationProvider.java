package com.dailycodebuffer.oauthauthorization.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// in the previous step we are change the authorization request .i.e all the request must be authenticated with fromLogin
// Now one thing is left that is 'authentication provider' like authentication manager, how you should be managing your authentication
// and for that we will create the CustomAuthenticationProvider for userid and password i.e.(email and password)
// Step6- Create the CustomAuthenticationProvider in the service package for authenticating User credential

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Now let's customize the below Authentication method
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userEmail= authentication.getName();
        String password= authentication.getCredentials().toString();

        UserDetails userDetails = customUserDetailService.loadUserByUsername(userEmail);

        return checkCredential(userDetails, password);
    }
    private Authentication checkCredential(UserDetails userDetails, String rawPassword) {

        if(passwordEncoder.matches(rawPassword, userDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                    userDetails.getPassword(),userDetails.getAuthorities());
        }else
            throw new BadCredentialsException("Bad Credential");
    }
// So in the above Method we are authenticating the user credential using the CustomUserDetailService
    // Let's add in the support as well below
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

// Finally we need to bind this CustomAuthenticationProvider.
// step-7: Let's go to DefaultSecurity Config and inject there