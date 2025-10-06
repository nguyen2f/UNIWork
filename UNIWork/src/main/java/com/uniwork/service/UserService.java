package com.uniwork.service;

import com.uniwork.dto.UserDTO;
import com.uniwork.model.ProjectMember;
import com.uniwork.model.User;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

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

    public ResponseEntity assignMemberToProject(Long userId, Long projectId, String role) {
//        Project project = projectRepository.findByProjectId(projectId);
//        User user = userRepository.findByUserId(userId);

        ProjectMember projectMember = projectMemberRepository.findByProjectId(projectId);
        projectMember.setProjectId(projectId);
        projectMember.setUserId(userId);
        projectMember.setRole(role);
        projectMember.setStatus(true);
        return ResponseEntity.ok(projectMemberRepository.save(projectMember));
    }

    public ResponseEntity updateMemberRole(Long userId, Long projectId, String role) {
        ProjectMember projectMember = projectMemberRepository.findByProjectId(projectId);
        projectMember.setRole(role);
        return ResponseEntity.ok(projectMemberRepository.save(projectMember));
    }

    public ResponseEntity removeMemberFromProject(Long userId, Long projectId) {
        ProjectMember projectMember = projectMemberRepository.findByProjectId(projectId);
        projectMemberRepository.delete(projectMember);
        return ResponseEntity.ok("Member removed from project");
    }

    public ResponseEntity getAllMembersByProjectId(Long projectId) {

        List<ProjectMember> members = projectMemberRepository.findAllByProjectId(projectId);
        return ResponseEntity.ok(members);
    }

}
