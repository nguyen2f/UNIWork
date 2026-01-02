package com.uniwork.service.impl;

import com.uniwork.model.dto.ProfileDTO;
import com.uniwork.model.entity.Task;
import com.uniwork.model.enumuration.Role;
import com.uniwork.model.enumuration.SystemRole;
import com.uniwork.model.projection.UserProfileProjection;
import com.uniwork.model.request.*;
import com.uniwork.model.dto.UserDTO;
import com.uniwork.model.entity.ProjectMember;
import com.uniwork.model.entity.User;
import com.uniwork.repository.ProjectMemberRepository;
import com.uniwork.repository.ProjectRepository;
import com.uniwork.repository.TaskRepository;
import com.uniwork.repository.UserRepository;
import com.uniwork.service.MailService;
import com.uniwork.service.UserService;
import com.uniwork.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;

    @CacheEvict(value = "uniwork:user:list", key = "'active'")
    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setName(registerRequest.getName());

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        user.setPassword(encodedPassword);
        user.setEmail(registerRequest.getEmail());
        user.setCreatedDate(LocalDateTime.now());
        user.setSystemRole(SystemRole.EMPLOYEE);
        user.setActive(true);
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
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
        projectMember.setRole(Role.valueOf(role));
        projectMember.setStatus(true);

        mailService.sendAssignMail(assignMemberRequest.getEmail(), assignMemberRequest.getProjectName(), role);

        return projectMemberRepository.save(projectMember);

    }

    public ProjectMember updateMemberRole(Long userId, Long projectId, String role) {
        ProjectMember projectMember = projectMemberRepository.findByProjectId(projectId);
        projectMember.setRole(Role.valueOf(role));
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

    @Cacheable(value = "uniwork:user:list", key = "'active'")
    public List<UserDTO> getAllMembersActive() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(user -> new UserDTO(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getDepartment()
                ))
                .collect(Collectors.toList());
        return users;
    }

    public ProfileDTO getUserById(Long userId) {
        UserProfileProjection user = userRepository.getUserProfile(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return new ProfileDTO(
                user.getUserId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getBio(),
                user.getAddress(),
                user.getDepartment(),
                user.getActive(),
                user.getSystemRole()
        );
    }

}
