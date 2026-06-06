package com.banking.user.service;

import com.banking.user.entity.Account;
import com.banking.user.entity.User;
import com.banking.user.entity.UserProfile;
import com.banking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // =========================================
    // CREATE USER
    // =========================================
    public User createUser(User user) {

        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        // init roles safely
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

        if (user.getRoles().isEmpty()) {
            user.getRoles().add("ROLE_USER");
        }

        // init accounts safely
        if (user.getAccounts() == null) {
            user.setAccounts(new ArrayList<>());
        }

        return userRepository.save(user);
    }

    // =========================================
    // GET ALL USERS
    // =========================================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================================
    // GET USER BY ID
    // =========================================
    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    // =========================================
    // UPDATE USER
    // =========================================
    public User updateUser(String id, User newUser) {

        User user = getUserById(id);

        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }

        if (newUser.getPassword() != null) {
            user.setPassword(newUser.getPassword());
        }

        return userRepository.save(user);
    }

    // =========================================
    // DELETE USER
    // =========================================
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // =========================================
    // ADD ROLE
    // =========================================
    public User addRole(String id, String role) {

        User user = getUserById(id);

        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }

        return userRepository.save(user);
    }

    // =========================================
    // PROFILE
    // =========================================
    public UserProfile getProfile(String userId) {
        return getUserById(userId).getProfile();
    }

    public UserProfile updateProfile(String userId, UserProfile profile) {

        User user = getUserById(userId);

        user.setProfile(profile);

        userRepository.save(user);

        return profile;
    }

    // =========================================
    // ACCOUNTS
    // =========================================
    public Account createAccount(String userId, Account.AccountType type) {

        User user = getUserById(userId);

        if (user.getAccounts() == null) {
            user.setAccounts(new ArrayList<>());
        }

        Account account = Account.builder()
                .id(UUID.randomUUID().toString())
                .accountNumber(generateAccountNumber())
                .iban(generateIban())
                .accountType(type)
                .status("ACTIVE")
                .currency("SAR")
                .build();

        user.getAccounts().add(account);

        userRepository.save(user);

        return account;
    }

    public List<Account> getAccounts(String userId) {
        User user = getUserById(userId);
        return user.getAccounts() != null ? user.getAccounts() : new ArrayList<>();
    }

    // =========================================
    // HELPERS
    // =========================================
    private String generateAccountNumber() {
        return "ACC-" + System.currentTimeMillis();
    }

    private String generateIban() {
        return "SA" + System.currentTimeMillis();
    }
}