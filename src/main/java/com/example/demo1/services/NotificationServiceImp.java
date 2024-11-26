package com.example.demo1.services;

import com.example.demo1.models.Notification;
import com.example.demo1.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImp implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAllNotification() {
        return notificationRepository.findAll(Sort.by("id").descending());
    }

    @Override
    public void saveNotification(Notification notification) {
       notificationRepository.save(notification);
    }
}
