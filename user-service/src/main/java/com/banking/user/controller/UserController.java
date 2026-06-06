package com.banking.user.controller;

import com.banking.user.entity.*;
import com.banking.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ================= USERS =================
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    // ================= ROLE =================
    @PostMapping("/{id}/role")
    public User addRole(@PathVariable String id, @RequestParam String role) {
        return userService.addRole(id, role);
    }

    // ================= PROFILE =================
    @GetMapping("/{id}/profile")
    public UserProfile getProfile(@PathVariable String id) {
        return userService.getProfile(id);
    }

    @PutMapping("/{id}/profile")
    public UserProfile updateProfile(@PathVariable String id,
                                     @RequestBody UserProfile profile) {
        return userService.updateProfile(id, profile);
    }

    // ================= ACCOUNTS =================
    @PostMapping("/{id}/accounts")
    public Account createAccount(@PathVariable String id,
                                 @RequestParam Account.AccountType type) {
        return userService.createAccount(id, type);
    }

    @GetMapping("/{id}/accounts")
    public List<Account> getAccounts(@PathVariable String id) {
        return userService.getAccounts(id);
    }
}