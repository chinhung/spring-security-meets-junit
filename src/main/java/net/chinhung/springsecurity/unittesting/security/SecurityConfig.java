package net.chinhung.springsecurity.unittesting.security;

import net.chinhung.springsecurity.unittesting.security.jwt.JwtFilter;
import net.chinhung.springsecurity.unittesting.security.jwt.JwtUtil;
import net.chinhung.springsecurity.unittesting.security.login.LoginFilter;
import net.chinhung.springsecurity.unittesting.security.login.LoginUtil;
import net.chinhung.springsecurity.unittesting.security.userdetails.UserDetailsServiceAdapter;
import net.chinhung.springsecurity.unittesting.user.UserService;
import net.chinhung.springsecurity.unittesting.user.UserServiceForTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity()
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserService userService;
    private JwtFilter jwtFilter;
    private LoginFilter loginFilter;

    public SecurityConfig() {
        this.userService = new UserServiceForTest();
        JwtUtil jwtUtil = new JwtUtil(userService);
        this.jwtFilter = new JwtFilter(jwtUtil);
        this.loginFilter = new LoginFilter(new LoginUtil(jwtUtil));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(getUserDetailsService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/hello/**").authenticated()
                .and()
                .addFilterBefore(getJwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(getLoginFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

    private UserDetailsService getUserDetailsService() {
        return new UserDetailsServiceAdapter(userService);
    }

    private Filter getJwtFilter() {
        return jwtFilter;
    }

    private Filter getLoginFilter(AuthenticationManager manager) {
        loginFilter.setAuthenticationManager(manager);
        return loginFilter;
    }
}
