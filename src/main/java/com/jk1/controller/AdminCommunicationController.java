package com.jk1.controller;

import com.jk1.entity.Announcement;
import com.jk1.entity.Campaign;
import com.jk1.entity.enums.AnnouncementTarget;
import com.jk1.entity.enums.CampaignType;
import com.jk1.service.AnnouncementService;
import com.jk1.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/communications")
@RequiredArgsConstructor
public class AdminCommunicationController {

    private final AnnouncementService announcementService;
    private final CampaignService campaignService;

    @GetMapping
    public String getDashboard(Model model) {
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        model.addAttribute("campaigns", campaignService.getAllCampaigns());
        return "admin/communications";
    }

    @PostMapping("/announcements")
    public String createAnnouncement(@RequestParam String title,
                                     @RequestParam String content,
                                     @RequestParam AnnouncementTarget target,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledDate) {
        announcementService.createAnnouncement(title, content, target, scheduledDate);
        return "redirect:/admin/communications";
    }

    @PostMapping("/campaigns")
    public String createCampaign(@RequestParam String name,
                                 @RequestParam CampaignType type,
                                 @RequestParam String content,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        campaignService.createCampaign(name, type, content, startDate, endDate);
        return "redirect:/admin/communications";
    }

    @PostMapping("/announcements/{id}/delete")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/admin/communications";
    }

    @PostMapping("/campaigns/{id}/delete")
    public String deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return "redirect:/admin/communications";
    }
}
