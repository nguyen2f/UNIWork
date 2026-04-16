package com.uniwork.modules.user.service;

import com.uniwork.modules.user.dto.ProfileDTO;
import com.uniwork.modules.task.entity.Task;
import com.uniwork.enums.Role;
import com.uniwork.enums.SystemRole;
import com.uniwork.modules.user.projection.UserProfileProjection;
import com.uniwork.modules.task.request.*;
import com.uniwork.modules.project.request.*;
import com.uniwork.modules.user.request.*;
import com.uniwork.modules.auth.request.*;
import com.uniwork.modules.stage.request.*;
import com.uniwork.modules.comment.request.*;
import com.uniwork.modules.event.request.*;
import com.uniwork.modules.file.request.*;
import com.uniwork.modules.user.dto.UserDTO;
import com.uniwork.modules.project.entity.ProjectMember;
import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.project.repository.ProjectMemberRepository;
import com.uniwork.modules.project.repository.ProjectRepository;
import com.uniwork.modules.task.repository.TaskRepository;
import com.uniwork.modules.user.repository.UserRepository;
import com.uniwork.modules.file.service.FileAttachmentService;
import com.uniwork.modules.mail.service.MailService;
import com.uniwork.modules.user.service.UserService;
import com.uniwork.common.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private FileAttachmentService fileAttachmentService;

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

        mailService.sendRegisterMail(registerRequest.getEmail(), registerRequest.getName());

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
                user.getSystemRole(),
                user.getAvatarUrl(),
                user.getAvatarPublicId()
        );
    }

    @Override
    public ProfileDTO updateAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findByUserId(userId);
        String publicId = "avatars/user_" + userId + "/" + java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
        Map uploadResult = fileAttachmentService.uploadImage(file, publicId);
        user.setAvatarUrl((String) uploadResult.get("secure_url"));
        user.setAvatarPublicId((String) uploadResult.get("public_id"));
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);
        return ProfileDTO.builder()
                .avatarUrl(user.getAvatarUrl())
                .avatarPublicId(user.getAvatarPublicId())
                .build();
    }

    @Override
    public User createUserByAdmin(com.uniwork.modules.admin.request.AdminCreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setName(request.getName());
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);
        user.setEmail(request.getEmail());
        user.setCreatedDate(LocalDateTime.now());
        
        SystemRole role = request.getSystemRole();
        if (role == null) {
            role = SystemRole.EMPLOYEE;
        }
        user.setSystemRole(role);
        user.setActive(true);
        
        return userRepository.save(user);
    }

    @Override
    public org.springframework.data.domain.Page<ProfileDTO> getAllUsersPaginated(org.springframework.data.domain.Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> ProfileDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .bio(user.getBio())
                .address(user.getAddress())
                .department(user.getDepartment())
                .active(user.getActive())
                .systemRole(user.getSystemRole() != null ? user.getSystemRole().name() : null)
                .avatarUrl(user.getAvatarUrl())
                .avatarPublicId(user.getAvatarPublicId())
                .build());
    }
}
