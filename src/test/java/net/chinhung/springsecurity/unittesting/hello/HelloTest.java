package net.chinhung.springsecurity.unittesting.hello;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class HelloTest {

    private String USER_JWT_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxOTA5NTUxMzkyfQ.-zLUaXSWCoWN0Wr877qeyiYrmfwR4WTy6CSFw6TDkBZCYGIkJLYVKNS4H9wrC4WHZiu2LPu6k_dNuTLXCbldYQ";

    private String ADMIN_JWT_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTkwOTU1MTQ0NX0.CwL9LNjN02VrDMOXtzNNwBE1SbEe0_Up0cAIfEeEr0BQMyuuYkZiW4ZZjY7kBRF6o0P_uumo-JSaxc_xvwrvkQ";

    @Autowired
    public WebApplicationContext wac;

    public MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

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

    @Test
    public void testUserFail_WorngToken() throws Exception {
        mockMvc.perform(get("/hello/user").header("Authorization", "Bearer a.b.c"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUserFail_WorngToken2() throws Exception {
        mockMvc.perform(get("/hello/user").header("Authorization", "xxxxxxxxxx"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUserFail_NoToken() throws Exception {
        mockMvc.perform(get("/hello/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAdminSuccess() throws Exception {
        mockMvc.perform(get("/hello/admin").header("Authorization", ADMIN_JWT_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    public void testAdminFailed_WithoutAuthority() throws Exception {
        mockMvc.perform(get("/hello/admin").header("Authorization", USER_JWT_TOKEN))
                .andExpect(status().isForbidden());
    }

}
