package net.chinhung.springsecurity.unittesting.user;

import java.util.Optional;

public interface UserService {

    Optional<User> loadUserByName(String name);
}
