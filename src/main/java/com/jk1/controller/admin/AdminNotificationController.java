package com.jk1.controller.admin;

import com.jk1.entity.Notification;
import com.jk1.entity.User;
import com.jk1.entity.enums.NotificationType;
import com.jk1.service.NotificationService;
import com.jk1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public String listNotifications(Model model) {
        model.addAttribute("notifications", notificationService.findAll());
        return "admin/notifications";
    }

    @PostMapping("/send")
    public String sendNotification(@RequestParam String title,
                                   @RequestParam String message,
                                   @RequestParam String type,
                                   @RequestParam(required = false) Long userId,
                                   RedirectAttributes redirectAttributes) {
        
        NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
        
        if (userId != null) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .notificationType(notificationType)
                    .user(user)
                    .build();
            notificationService.save(notification);
        } else {
            // Send to all users
            List<User> users = userService.findAll();
            for (User user : users) {
                Notification notification = Notification.builder()
                        .title(title)
                        .message(message)
                        .notificationType(notificationType)
                        .user(user)
                        .build();
                notificationService.save(notification);
            }
        }
        
        redirectAttributes.addFlashAttribute("success", "Notification sent successfully.");
        return "redirect:/admin/notifications";
    }
}
