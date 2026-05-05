package com.uniwork.modules.company.repository;

import com.uniwork.modules.company.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findById(long id);

    List<Department> findAllByIsDeletedFalse();

}
