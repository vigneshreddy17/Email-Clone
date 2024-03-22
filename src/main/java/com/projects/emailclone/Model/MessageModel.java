package com.projects.emailclone.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@Table(name = "\"messages\"")
@Entity
public class MessageModel {
    private String sender;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "is_read")
    private boolean is_read;
    @Column(name = "date and time")
    private LocalDateTime date;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public MessageModel(String sender, String recipient, String message, boolean is_read) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.is_read = is_read;
    }

    public boolean isRead() {
        return is_read;
    }

    public void setRead(boolean read) {
        is_read = read;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", message='" + message + '\'' +
                ", id=" + id +
                '}';
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String recipient;
    private String message;

    public MessageModel() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
