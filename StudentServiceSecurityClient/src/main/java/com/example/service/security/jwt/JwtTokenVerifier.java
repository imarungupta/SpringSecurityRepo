package com.example.service.security.jwt;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {
    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

    // Get the token from the header
    String authorizationHeader = request.getHeader("Authorization");
    // Now check if this authorizationHeader is null or empty or does not start with "Bearer" then does not validate the token
    if(Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer")){
        filterChain.doFilter(request,response);
        return; // does not validated and return the exception
    }
    // Check for correct token in the try other wise we will exception in the catch block if token got expired or modified
    // Remove the "Bearer" and put " " from the actual token and we will get actual token generated by Jwt
    String token = authorizationHeader.replace("Bearer"," ");
    try{
        // Get the SecretKey which was used during the token creation
        String secureKey = "SecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKeySecureKey"; // make sure this key should be long enough to generate big encoded value

        // Get the payload from the token
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secureKey.getBytes()))
                .parseClaimsJws(token);

        // Now from this payload let's get the actual body of the request
        Claims body = claimsJws.getBody();
        // From the body let's retrieve the subject (username)
        String username = body.getSubject(); // Like linda , tom or annasmith
        //Note here we can do several thing like -> body.setExpiration() , we can set the expiration date (increase or decrease)
        // Now let's extract the authorities which is list of map if we see in the jwt.io and from this user can be authenticated
        List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("Authorities");

        // Collect the set of authorities
        Set<SimpleGrantedAuthority> simpleGrantedAuthority = authorities.stream()            //.map(m->m.get("authorities"))
                                                                        .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                                                                        .collect(Collectors.toSet());

        // let's authenticate the username and its authorities
        Authentication authentication = new UsernamePasswordAuthenticationToken(
           username,
          null,
           simpleGrantedAuthority
        );
        // finally we are authenticated the token sent by the client by setting authentication in the below line
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }catch (JwtException e){
        throw new IllegalStateException(String.format("Token %s cannot be trusted",token));
    }
    // if we don't write the below line then further filter will not executed and request cannot reach to its destination api to get response
    filterChain.doFilter(request,response);
  }
}
