package net.chinhung.springsecurity.unittesting.security.userdetails;

import net.chinhung.springsecurity.unittesting.user.User;
import net.chinhung.springsecurity.unittesting.user.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsServiceAdapter implements UserDetailsService {

    private UserService userService;

    public UserDetailsServiceAdapter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.loadUserByName(username).orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));
        List<GrantedAuthority> authorities = user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new UserDetailsAdapter(user.getName(), user.getPassword(), authorities);
    }
}
