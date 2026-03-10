package org.sorokin_school.service;

import org.sorokin_school.exception.BankException;
import org.sorokin_school.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int userIdCounter = 1;

    private AccountService accountService;

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public User createUser(String login) {
        if (users.values().stream().anyMatch(u -> u.getLogin().equals(login))) {
            throw new BankException("Логин занят!");
        }
        User user = new User(userIdCounter++, login);
        users.put(user.getId(), user);
        accountService.createAccount(user.getId());
        return user;
    }

    public Optional<User> getUser(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
