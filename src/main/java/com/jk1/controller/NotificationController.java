package com.jk1.controller;

import com.jk1.entity.Notification;
import com.jk1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public String getInbox(Model model, Authentication authentication) {
        String email = authentication.getName();
        List<Notification> notifications = notificationService.getUserNotifications(email);
        long unreadCount = notificationService.getUnreadCount(email);
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        return "notifications/inbox";
    }

    @PostMapping("/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        notificationService.markAsRead(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    @ResponseBody
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        // ideally add ownership check here
        notificationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        long count = notificationService.getUnreadCount(authentication.getName());
        return ResponseEntity.ok(count);
    }
}
