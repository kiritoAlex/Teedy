package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.ChatMessage;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatMessageDao {
    public String create(ChatMessage chatMessage, String senderName, String receiverName) {
        // Create chat message
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        chatMessage.setId(UUID.randomUUID().toString());
        chatMessage.setCreateDate(new Date());
        chatMessage.setRead(false);
        chatMessage.setSenderName(senderName);
        chatMessage.setReceiverName(receiverName);
        em.persist(chatMessage);
        
        return chatMessage.getId();
    }

    public List<ChatMessage> getMessages(String userId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.createQuery("select m from ChatMessage m where m.receiverId = :userId order by m.createDate desc")
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<ChatMessage> getConversation(String userId1, String userId2) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.createQuery("select m from ChatMessage m where " +
                "(m.senderId = :userId1 and m.receiverId = :userId2) or " +
                "(m.senderId = :userId2 and m.receiverId = :userId1) " +
                "order by m.createDate asc")
                .setParameter("userId1", userId1)
                .setParameter("userId2", userId2)
                .getResultList();
    }

    public void markAsRead(String messageId) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        ChatMessage message = em.find(ChatMessage.class, messageId);
        if (message != null) {
            message.setRead(true);
        }
    }
} 