package com.springbootsecurityrest.resources;

import com.springbootsecurityrest.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private List<User> users;

    public UserRepository() {
        this.users = List.of(
                new User("anton", "1234", "Антон", "Иванов", 20),
                new User("ivan", "12345", "Сергей", "Петров", 21));
    }

    public User getByLogin(String login) {
        return this.users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAll() {
        return this.users;
    }
}
