package com.uniwork.controller;

import com.uniwork.interceptors.Payload;
import com.uniwork.model.entity.Notification;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
@PreAuthorize("hasAuthority('PERM_MANAGE_NOTIFICATIONS')")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity getAllNotification(@RequestAttribute(required = false) Payload payload) {
        List<Notification> notificationList = notificationService.getAllNotificationByUserId(payload.getUserId());
        return ResponseFactory.success(notificationList);
    }

    @PatchMapping("/{notiId}/read")
    public ResponseEntity markAsRead(@PathVariable Long notiId,
                                     @RequestAttribute Payload payload) {
        notificationService.markAsRead(notiId, payload.getUserId());
        return ResponseFactory.success("Mark as read");
    }

    @PostMapping("/read-all")
    public ResponseEntity markAsReadAll(@RequestAttribute Payload payload) {
        notificationService.markAsReadAll(payload.getUserId());
        return ResponseFactory.success("Mark as read all");
    }

}
