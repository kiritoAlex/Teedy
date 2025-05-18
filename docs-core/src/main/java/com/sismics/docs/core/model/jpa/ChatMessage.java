package com.sismics.docs.core.model.jpa;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_CHAT_MESSAGE")
public class ChatMessage {
    @Id
    @Column(name = "MSG_ID_C", length = 36)
    private String id;

    @Column(name = "SENDER_ID_C", length = 36, nullable = false)
    private String senderId;

    @Column(name = "SENDER_NAME_C", length = 50)
    private String senderName;

    @Column(name = "RECEIVER_ID_C", length = 36, nullable = false)
    private String receiverId;

    @Column(name = "RECEIVER_NAME_C", length = 50)
    private String receiverName;

    @Column(name = "CONTENT_C", nullable = false)
    private String content;

    @Column(name = "CREATE_DATE_D", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "READ_B", nullable = false)
    private boolean read;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
} 