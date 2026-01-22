package com.uniwork.repository;

import com.uniwork.model.entity.FileAttachment;
import com.uniwork.model.projection.FileAttachmentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment,Long> {
    @Query(value = "SELECT f.file_id AS fileId, " +
            "       f.uploader_id AS uploaderId, " +
            "       u.name AS uploaderName, " +
            "       f.public_id AS publicId, " +
            "       f.original_file_name AS originalFileName, " +
            "       f.file_url AS fileUrl, " +
            "       f.file_type AS fileType, " +
            "       f.content_type AS contentType, " +
            "       f.file_size AS fileSize, " +
            "       f.upload_date AS uploadDate " +
            "FROM file_attachments f " +
            "JOIN users u ON f.uploader_id = u.user_id " +
            "WHERE f.task_id = :taskId",
            nativeQuery = true)
    List<FileAttachmentProjection> findAllByTaskId(@Param("taskId") Long taskId);

}
