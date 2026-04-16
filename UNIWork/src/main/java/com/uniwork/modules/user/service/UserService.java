package com.uniwork.modules.user.service;

import com.uniwork.modules.user.dto.ProfileDTO;
import com.uniwork.modules.user.dto.UserDTO;
import com.uniwork.modules.project.entity.ProjectMember;
import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.project.request.AssignMemberRequest;
import com.uniwork.modules.auth.request.RegisterRequest;
import com.uniwork.modules.project.request.RemoveMemberRequest;
import com.uniwork.modules.user.request.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User registerUser(RegisterRequest registerRequest);

    User login(String email, String password);

    ProjectMember assignMemberToProject(AssignMemberRequest assignMemberRequest);

    ProjectMember removeMemberFromProject(RemoveMemberRequest removeMemberRequest);

    User updateProfile(Long userId, UpdateProfileRequest updateProfileRequest);

    User findByEmail(String email);

    List<UserDTO> getAllMembersActive();

    ProfileDTO getUserById(Long userId);

    ProfileDTO updateAvatar(Long userId, MultipartFile file);

    User createUserByAdmin(com.uniwork.modules.admin.request.AdminCreateUserRequest req);

    org.springframework.data.domain.Page<ProfileDTO> getAllUsersPaginated(org.springframework.data.domain.Pageable pageable);
}
