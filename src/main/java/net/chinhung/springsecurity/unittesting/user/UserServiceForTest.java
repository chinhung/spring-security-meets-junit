package net.chinhung.springsecurity.unittesting.user;

import java.util.*;

public class UserServiceForTest implements UserService {

    private Map<String, User> userMap;

    public UserServiceForTest() {
        this.userMap = new HashMap<>();

        List<String> adminAuthorities = new ArrayList<>();
        adminAuthorities.add("hello.admin");
        User admin = new User();
        admin.setId("1");
        admin.setName("admin");
        admin.setPassword("{noop}password");
        admin.setAuthorities(adminAuthorities);

        List<String> userAuthorities = new ArrayList<>();
        userAuthorities.add("hello.user");
        User user = new User();
        user.setId("2");
        user.setName("user");
        user.setPassword("{noop}password");
        user.setAuthorities(userAuthorities);

        userMap.put(admin.getName(), admin);
        userMap.put(user.getName(), user);
    }

    @Override
    public Optional<User> loadUserByName(String name) {
        User user = userMap.get(name);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
