package com.uniwork.controller;

import com.uniwork.interceptors.Payload;
import com.uniwork.model.entity.Notification;
import com.uniwork.model.response.ResponseFactory;
import com.uniwork.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity getAllNotification(@RequestAttribute Payload payload) {
        List<Notification> notificationList = notificationService.getAllNotificationByUserId(payload.getUserId());
        return ResponseFactory.success(notificationList);
    }

    @PatchMapping("/{notiId}/read")
    public ResponseEntity markAsRead(@PathVariable Long notiId,
                                     @RequestAttribute Payload payload) {
        notificationService.markAsRead(notiId, payload.getUserId());
        return ResponseFactory.success("Mark as read");
    }

}
