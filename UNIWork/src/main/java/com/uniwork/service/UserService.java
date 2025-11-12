package com.uniwork.service;

import com.uniwork.entity.model.Task;
import com.uniwork.entity.request.*;
import com.uniwork.entity.dto.UserDTO;
import com.uniwork.entity.model.ProjectMember;
import com.uniwork.entity.model.User;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.TaskRepository;
import com.uniwork.repository.UserRepository;
import com.uniwork.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private TaskRepository taskRepository;

    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setName(registerRequest.getName());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setCreatedDate(LocalDateTime.now());
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

    public ProjectMember assignMemberToProject(AssignMemberRequest assignMemberRequest) {
        Long userId = assignMemberRequest.getUserId();
        Long projectId = assignMemberRequest.getProjectId();
        String role = assignMemberRequest.getRole();

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(projectId);
        projectMember.setUserId(userId);
        projectMember.setRole(role);
        projectMember.setStatus(true);
        return projectMemberRepository.save(projectMember);
    }

    public ProjectMember updateMemberRole(Long userId, Long projectId, String role) {
        ProjectMember projectMember = projectMemberRepository.findByProjectId(projectId);
        projectMember.setRole(role);
        return projectMemberRepository.save(projectMember);
    }

    public ProjectMember removeMemberFromProject(RemoveMemberRequest removeMemberRequest) {
        Long memberId = removeMemberRequest.getMemberId();
        Long projectId = removeMemberRequest.getProjectId();

        List<Task> tasks = taskRepository.findAllByProjectIdAndAssignedTo(projectId, memberId);
        if (!tasks.isEmpty()) {
            taskRepository.deleteAll(tasks);
        }

        ProjectMember projectMember = projectMemberRepository.findByProjectId(projectId);
        projectMemberRepository.delete(projectMember);
        return projectMember;
    }

    public List<ProjectMember> getAllMembersByProjectId(Long projectId) {

        List<ProjectMember> members = projectMemberRepository.findAllByProjectId(projectId);
        return members;
    }

    public User updateProfile(Long userId, UpdateProfileRequest updateProfileRequest) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        user.setUpdatedDate(LocalDateTime.now());

        if (updateProfileRequest.getPassword() != null && !updateProfileRequest.getPassword().isEmpty()) {
            user.setPassword(updateProfileRequest.getPassword());
        }
        BeanCopyUtils.copyNonNullProperties(updateProfileRequest, user);
        return userRepository.save(user);
    }

    public List<UserDTO> getAllMembersActive() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(user -> new UserDTO(
                        user.getUserId(),
                        user.getPhone(),
                        user.getName(),
                        user.getEmail()
                ))
                .collect(Collectors.toList());
        return users;
    }

}
