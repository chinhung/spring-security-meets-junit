package net.chinhung.springsecurity.unittesting.security.login;

import net.chinhung.springsecurity.unittesting.security.userdetails.UserDetailsAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private LoginUtil loginUtil;

    public LoginFilter(LoginUtil loginUtil) {
        this.loginUtil = loginUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginData loginData = loginUtil.readLoginData(request);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginData.getAccount(), loginData.getPassword());
        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = ((UserDetailsAdapter) authResult.getPrincipal()).getUsername();
        String jwtToken = loginUtil.generateJwtToken(username);
        response.addHeader("token", jwtToken);
    }
}
