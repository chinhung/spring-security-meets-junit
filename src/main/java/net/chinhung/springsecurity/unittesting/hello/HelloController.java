package net.chinhung.springsecurity.unittesting.hello;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
