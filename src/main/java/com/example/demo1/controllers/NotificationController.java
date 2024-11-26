package com.example.demo1.controllers;

import com.example.demo1.models.Notification;
import com.example.demo1.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public String getAllNotifications(Model model) {
        List<Notification> notifications = notificationService.getAllNotification();
        model.addAttribute("notifications",notifications);
        return "notifications/list";
    }



}
