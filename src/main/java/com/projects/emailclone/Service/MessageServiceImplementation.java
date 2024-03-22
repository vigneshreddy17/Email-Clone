package com.projects.emailclone.Service;

import com.projects.emailclone.Model.MessageModel;
import com.projects.emailclone.Repository.MessageRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImplementation implements MessageService{
    final MessageRepo messageRepo;
    final EntityManager entityManager;

    @Autowired
    public MessageServiceImplementation(final MessageRepo messageRepo, EntityManager entityManager) {
        this.messageRepo = messageRepo;
        this.entityManager = entityManager;
    }

    @Override
    public void sendMessage(final String recipient, final String message,
                            final String sender) {
        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(message);
        messageModel.setRecipient(recipient);
        messageModel.setSender(sender);
        messageModel.setRead(false);
        messageModel.setDate(LocalDateTime.now());
        messageRepo.save(messageModel);
    }

    @Override
    public Optional<List<Object[]>> readMessages(String sender) {
        return messageRepo.findBySender(sender);
    }

    @Override
    public Optional<List<Object[]>> readUnreadMessages(String user) {
        return messageRepo.getUnreadMessages(user);
    }

    @Override
    @Transactional
    public void markAsRead(String user) {
        messageRepo.markAsRead(user);
        entityManager.flush();
    }
}
