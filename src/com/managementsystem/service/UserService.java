package com.managementsystem.service;

import com.managementsystem.exception.DuplicateItemException;
import com.managementsystem.exception.UserNotFoundException;
import com.managementsystem.model.Member;
import com.managementsystem.model.StaffUser;
import com.managementsystem.model.User;
import com.managementsystem.repository.Repository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing user registration, retrieval, and profile queries.
 */
public class UserService {
    private final Repository<User, String> userRepository;

    public UserService(Repository<User, String> userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) throws DuplicateItemException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (userRepository.existsById(user.getUserId())) {
            throw new DuplicateItemException("User with ID '" + user.getUserId() + "' already exists.");
        }
        userRepository.save(user);
    }

    public User getUser(String userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Member> getAllMembers() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof Member)
                .map(u -> (Member) u)
                .collect(Collectors.toList());
    }

    public List<StaffUser> getAllStaff() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof StaffUser)
                .map(u -> (StaffUser) u)
                .collect(Collectors.toList());
    }

    public List<Member> getMembersWithFines() {
        return getAllMembers().stream()
                .filter(m -> m.getUnpaidFines() > 0)
                .collect(Collectors.toList());
    }
}
