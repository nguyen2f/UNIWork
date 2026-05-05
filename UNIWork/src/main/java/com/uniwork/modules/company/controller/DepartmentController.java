package com.uniwork.modules.company.controller;

import com.uniwork.common.response.ResponseFactory;
import com.uniwork.modules.company.entity.Department;
import com.uniwork.modules.company.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

//    @PreAuthorize("hasAuthority('PERM_MANAGE_SYSTEM')")
    @GetMapping("")
    public ResponseEntity getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseFactory.success(departments);
    }
}
