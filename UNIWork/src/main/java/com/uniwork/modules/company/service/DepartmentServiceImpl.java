package com.uniwork.modules.company.service;

import com.uniwork.modules.company.entity.Department;
import com.uniwork.modules.company.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentRepository departmentRepository;

    @Cacheable(value = "uniwork:department:list", key = "'active'")
    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAllByIsDeletedFalse();
    }
}
