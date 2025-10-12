package com.uniwork.repository;

import com.uniwork.entity.model.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment,Long> {
    List<FileAttachment> findAllByTaskId(Long taskId);
}
