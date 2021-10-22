package net.chinhung.springsecurity.unittesting.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    public void testLoginFailed_AccountNotExists() throws Exception {
        mockMvc.perform(post("/login").content("{\"account\":\"notExists\",\"password\":\"xxxxxxxx\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginFailed_WrongContentFormat() throws Exception {
        mockMvc.perform(post("/login").content("notLoginDTO"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginFailed_EmptyContent() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isUnauthorized());
    }
}
