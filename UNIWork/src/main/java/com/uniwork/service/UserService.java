package com.uniwork.service;

import com.uniwork.dto.UserDTO;
import com.uniwork.model.User;
import com.uniwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setCreatedDate(new Date());
        return userRepository.save(user);
    }


    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }
        return user;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
