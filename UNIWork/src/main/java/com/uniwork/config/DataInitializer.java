package com.uniwork.config;

import com.uniwork.enums.SystemRole;
import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin")) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setSystemRole(SystemRole.ADMIN.name());
            admin.setActive(true);
            admin.setCreatedDate(LocalDateTime.now());
            userRepository.save(admin);
            System.out.println("Default admin created: admin / admin");
        }
    }
}
