package com.uniwork.service;

import com.uniwork.model.dto.ProfileDTO;
import com.uniwork.model.dto.UserDTO;
import com.uniwork.model.entity.ProjectMember;
import com.uniwork.model.entity.User;
import com.uniwork.model.request.AssignMemberRequest;
import com.uniwork.model.request.RegisterRequest;
import com.uniwork.model.request.RemoveMemberRequest;
import com.uniwork.model.request.UpdateProfileRequest;

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

}
