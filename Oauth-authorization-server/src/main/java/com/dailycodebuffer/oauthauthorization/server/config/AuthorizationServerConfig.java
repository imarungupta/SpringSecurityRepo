package com.dailycodebuffer.oauthauthorization.server.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/*
   Step2- Now needs to implement the configuration i.e. we need to add configuration for
   our authorization server. Note this implementation would be standard for all the
   authorization servers so here we just need to use the bunch of code which already written,
   we just need to use here. Here we will create public key, private key and others to handles authorization server
   so that our clients can connect to it and exchange between the  authorization code token can be happen
Now let's create the 'AuthorizationServerConfig' in the config package.
*/

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Add the security filter chain. and apply the default security for all and then go via formLogin

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);

        // Note if we go inside 'OAuth2AuthorizationServerConfiguration' it will implement all the default security like RSA, Jwt etc
        return httpSecurity.formLogin(Customizer.withDefaults()).build();
    }

    // Step3- Register the client to Authorization server
// As we saw earlier everytime clients are getting registered to the authorization server, so here also we need to
// register the clients. Let's enable client registration here next just by adding default basic configuration (Standard code)
// just copy from the google and past: https://www.baeldung.com/spring-security-oauth-auth-server


    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        // Let's create the object of Registered Client with Random UUID
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("api-client") // This is the name of client to be registered
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/api-client-oidc")
                .redirectUri("http://127.0.0.1:8080/authorized")
                .scope(OidcScopes.OPENID)
                .scope("api.read")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
        // The client will be registered in InMemoryRegisteredClientRepository
    }

// Currently here we have only one client i.e. 'SpringSecurityClient' which i want to register
// in the Authorization Server so the above information will be added in the client's application.yml
// so that client could connect to authorization server.So here as we can see in the above method Unique client id
// (api-client), clientSecret, AuthenticationMethod, GrantType(to be accessed), redirectUrl, scope etc.
// Same property will be added in yml file
// Note: Since we have only one client to be registered that is why we have added only one static method RegisteredClientRepository
// But if we want to make it dynamically then we will use JDBC Registration client (JdbcRegisteredClientRepository) here
// 8.50.23

//Step4- Next step is to configure public and private key, which is nothing but the standard configuration for public and private key.
//Each authorization server needs its signing key for tokens to keep a proper boundary between security domains. Let's generate a 2048-byte RSA key
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet((JWK) rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }
    private static RSAKey generateRsa() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }
    private static KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
    @Bean
    public ProviderSettings providerSettings(){
        return ProviderSettings.builder()
                .issuer("http://DESKTOP-B1DPRDQ:9090")  //DESKTOP-B1DPRDQ
                .build();
    }
}
// So this all about authorization server configuration
// Step-5:-Now let's do the configuration for Basic spring security, so let's add the default basic security configration 
// Let's create on class 'DefaultSecurityConfig'