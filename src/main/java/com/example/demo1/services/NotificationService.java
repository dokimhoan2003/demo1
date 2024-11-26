package com.example.demo1.services;

import com.example.demo1.models.Notification;

import java.util.List;

public interface NotificationService {
    public List<Notification> getAllNotification();
    public void saveNotification(Notification notification);
}
