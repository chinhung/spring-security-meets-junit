package net.chinhung.springsecurity.unittesting.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.chinhung.springsecurity.unittesting.security.userdetails.UserDetailsAdapter;
import net.chinhung.springsecurity.unittesting.user.User;
import net.chinhung.springsecurity.unittesting.user.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtil {

    private UserService userService;

    public JwtUtil(UserService userService) {
        this.userService = userService;
    }

    public boolean isJwtFormat(String authorizationString) {
        return authorizationString != null && authorizationString.startsWith("Bearer ");
    }

    public Authentication loadAuthentication(String authorizationString) {
        String jwtToken = authorizationString.replace("Bearer ", "");
        UserDetails userDetails = loadUserDetails(jwtToken);
        return new UsernamePasswordAuthenticationToken(null, null, userDetails.getAuthorities());
    }

    private UserDetails loadUserDetails(String jwtToken) {
        String subject = null;
        try {
            subject = Jwts.parser().setSigningKey("secret")
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new BadCredentialsException("parse jwt token failed");
        }

        String username = subject;
        User user = userService.loadUserByName(username).orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));
        List<GrantedAuthority> authorities = user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new UserDetailsAdapter(user.getName(), user.getPassword(), authorities);
    }

    public String generateJwtToken(String username) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2030, 6, 6);
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(calendar.getTime())
                .signWith(SignatureAlgorithm.HS512, "secret")
                .compact();
    }
}
