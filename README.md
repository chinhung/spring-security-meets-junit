# Spring Security with JUnit

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/chinhung/pointwave/blob/master/LICENSE)
[![Java CI with Gradle](https://github.com/chinhung/springsecurity-unittesting/actions/workflows/gradle.yml/badge.svg)](https://github.com/chinhung/springsecurity-unittesting/actions/workflows/gradle.yml)

This is a demo repository for validating the Spring Security configurations via JUnit.

## Introduction

Thanks for the MockMvc object provided by Spring, it is useful to perform automated testing to validate the configurations of Spring Security with JUnit. The url mapping, parameter setting, authentication process and the authorization process could also be validated. And it is helpful to develop the api with Test-Driven Development.

This repository use JSON Web Token(JWT).

## Example

This example shows that one could perform login request and get the token in the response header if the password was correct. However, if the password was incorrect, one would get response code 401:

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


This example shows that a user can visit the authenticated url if the request header contained the correct token:

```java
@WebMvcTest
public class HelloTest {

    // ...

    @Test
    public void testUserSuccess() throws Exception {
        mockMvc.perform(get("/hello/user").header("Authorization", USER_JWT_TOKEN))
                .andExpect(status().isOk());
    }
    
    // ...
}
```

## Unit Tests
```
./gradlew test
```