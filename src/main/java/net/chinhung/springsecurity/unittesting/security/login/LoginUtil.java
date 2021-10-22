package net.chinhung.springsecurity.unittesting.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.chinhung.springsecurity.unittesting.security.jwt.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class LoginUtil {

    private JwtUtil jwtUtil;

    public LoginUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateJwtToken(String username) {
        return jwtUtil.generateJwtToken(username);
    }

    public LoginData readLoginData(HttpServletRequest request) {
        try {
            return new ObjectMapper().readValue(request.getInputStream(), LoginData.class);
        } catch (IOException e) {
            throw new BadCredentialsException("read login data failed", e);
        }
    }
}
