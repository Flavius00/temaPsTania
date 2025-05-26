package com.example.demo.model;

import java.time.LocalDateTime;

public class Notification {
    private String id;
    private String type;
    private String message;
    private LocalDateTime timestamp;
    private Object data;
    private String recipientId;

    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.id = java.util.UUID.randomUUID().toString();
    }

    public Notification(String type, String message, Object data, String recipientId) {
        this();
        this.type = type;
        this.message = message;
        this.data = data;
        this.recipientId = recipientId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
}