package com.jk1.controller;

import com.jk1.entity.NotificationPreference;
import com.jk1.entity.User;
import com.jk1.repository.NotificationPreferenceRepository;
import com.jk1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String getSettings(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        NotificationPreference pref = notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    NotificationPreference defaultPref = NotificationPreference.builder()
                            .user(user)
                            .build();
                    return notificationPreferenceRepository.save(defaultPref);
                });
        model.addAttribute("preferences", pref);
        return "user/settings";
    }

    @PostMapping("/notifications")
    public String updatePreferences(@RequestParam(defaultValue = "false") boolean emailNotifications,
                                    @RequestParam(defaultValue = "false") boolean orderAlerts,
                                    @RequestParam(defaultValue = "false") boolean offerAlerts,
                                    @RequestParam(defaultValue = "false") boolean wishlistAlerts,
                                    Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        NotificationPreference pref = notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> NotificationPreference.builder().user(user).build());

        pref.setEmailNotificationsEnabled(emailNotifications);
        pref.setOrderAlertsEnabled(orderAlerts);
        pref.setOfferAlertsEnabled(offerAlerts);
        pref.setWishlistAlertsEnabled(wishlistAlerts);

        notificationPreferenceRepository.save(pref);
        return "redirect:/user/settings?success";
    }
}
