# Spring Security meets JUnit

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/chinhung/pointwave/blob/master/LICENSE)
[![Java CI with Gradle](https://github.com/chinhung/springsecurity-unittesting/actions/workflows/gradle.yml/badge.svg)](https://github.com/chinhung/springsecurity-unittesting/actions/workflows/gradle.yml)

This is a demo repository for validating the Spring Security configurations via JUnit.

## Introduction

Thanks for the MockMvc object provided by Spring, it is convenient to perform automated testing to validate the configurations of Spring Security with JUnit. The url mapping, parameter extraction, authentication process and the authorization process could be validated. And it is also helpful to develop the api with Test-Driven Development approach.

This repository uses JSON Web Token(JWT) as the validation policy.

## Run Tests
```
./gradlew test
```
## Spring Security Config

```java
@Configuration
@EnableWebSecurity()
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // ...

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
    
    // ...
}
```

## Authenticated Api

```java
@RestController()
@RequestMapping(value="/hello")
public class HelloController {

    @PreAuthorize("hasAuthority('hello.user')")
    @GetMapping("/user")
    public String user() {
        return "Hello User!";
    }

    @PreAuthorize("hasAuthority('hello.admin')")
    @GetMapping("/admin")
    public String admin() {
        return "Hello Admin!";
    }
}
```

## Testing Setup 

The annotation `@WebMvcTest` is required to inject the WebApplicationContext by Spring with `@Autowired` annotation. Use `MockMvcBuilders.webAppContextSetup` to setup the MockMvc object, and apply the Spring Security:

```java
@WebMvcTest
public class LoginTest {

    @Autowired
    public WebApplicationContext wac;

    public MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }
    
    // ...
}
```

## Test Cases

### Login Test

The two test cases show that one could perform login request and get the token in the response header if the password was correct. However, one would get `401 Unauthorized` if the password was incorrect:

```java
@WebMvcTest
public class LoginTest {

    // ...

    @Test
    public void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/login").content("{\"account\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("token", matchesPattern(Pattern.compile("^[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*\\.[A-Za-z0-9-_]*$"))));
    }

    @Test
    public void testLoginFailed_WrongPassword() throws Exception {
        mockMvc.perform(post("/login").content("{\"account\":\"user\",\"password\":\"xxxxxxxx\"}"))
                .andExpect(status().isUnauthorized());
    }
    
    // ...
}
```

### Authority Test

The two test cases show that a user can visit the authenticated url if the request was authorized with the token in the request header. And one will get `403 Forbidden` if the user was without the required authority:

```java
@WebMvcTest
public class HelloTest {

    // ...

    @Test
    public void testUserSuccess() throws Exception {
        mockMvc.perform(get("/hello/user").header("Authorization", USER_JWT_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserFail_WithoutAuthority() throws Exception {
        mockMvc.perform(get("/hello/user").header("Authorization", ADMIN_JWT_TOKEN))
                .andExpect(status().isForbidden());
    }
    
    // ...
}
```