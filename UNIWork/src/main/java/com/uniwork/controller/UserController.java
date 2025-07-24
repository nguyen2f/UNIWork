package com.uniwork.controller;
import com.uniwork.dto.UserDTO;
import com.uniwork.model.User;
import com.uniwork.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity registerUser(UserDTO userDTO) {
        log.info("Registering user: {}", userDTO.getName());
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(
            @RequestParam String email,
            @RequestParam String password) {
        log.info("Logining user: {}", email);
        User user = userService.login(email, password);
        return ResponseEntity.ok(user);
    }

}
