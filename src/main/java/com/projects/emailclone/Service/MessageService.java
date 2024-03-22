package com.projects.emailclone.Service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MessageService {
    public void sendMessage(String recipient, String message, String sender);
    public Optional<List<Object[]>> readMessages(String sender);
    public Optional<List<Object[]>> readUnreadMessages(String user);
    public void markAsRead(String user);
}
