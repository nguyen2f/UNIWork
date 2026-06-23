package com.uniwork.modules.admin.controller;

import com.uniwork.common.response.PageMetadata;
import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.admin.request.AdminCreateDepartmentRequest;
import com.uniwork.modules.admin.request.AdminCreateUserRequest;
import com.uniwork.modules.company.entity.Department;
import com.uniwork.modules.company.service.DepartmentService;
import com.uniwork.modules.user.entity.User;
import com.uniwork.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;

    @PreAuthorize("hasAuthority('PERM_MANAGE_SYSTEM')")
    @PostMapping("/user/create")
    public ResponseEntity createUser(@RequestBody AdminCreateUserRequest request) {
        log.info("Admin creating user: {}", request.getEmail());
        User user = userService.createUserByAdmin(request);
        return ResponseFactory.success(user);
    }

    @PreAuthorize("hasAuthority('PERM_MANAGE_SYSTEM')")
    @GetMapping("/users")
    public ResponseEntity getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.getAllUsers(pageable);
        
        PageMetadata metadata = PageMetadata.of(
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements()
        );
        
        return ResponseFactory.makePagination(userPage.getContent(), metadata);
    }

    @PreAuthorize("hasAuthority('PERM_MANAGE_SYSTEM')")
    @PostMapping("/department/create")
    public ResponseEntity createDepartment(@RequestBody AdminCreateDepartmentRequest request) {
        log.info("Admin creating department: {}", request.getDepartmentName());
        Department department = userService.createDepartmentByAdmin(request);
        return ResponseFactory.success(department);
    }

}
