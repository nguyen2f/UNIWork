package com.uniwork.modules.file.controller;

import com.uniwork.modules.file.service.FileAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file-attachments")
@Slf4j
public class FileAttachmentController {

    private final FileAttachmentService fileAttachmentService;

    public FileAttachmentController(FileAttachmentService fileAttachmentService) {
        this.fileAttachmentService = fileAttachmentService;
    }

}
