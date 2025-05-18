package com.sismics.docs.rest.resource;

import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class ChatResource extends BaseResource {

    // 使用内存存储消息
    private static final CopyOnWriteArrayList<ChatMessage> messages = new CopyOnWriteArrayList<>();
    private static final AtomicInteger idGen = new AtomicInteger(1);

    @GET
    @Path("/messages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessages() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // 获取当前用户的所有消息
        List<ChatMessage> userMessages = new ArrayList<>();
        for (ChatMessage message : messages) {
            if (message.getReceiverId().equals(principal.getId())) {
                userMessages.add(message);
            }
        }

        JsonObjectBuilder response = Json.createObjectBuilder();
        JsonArrayBuilder messagesJson = Json.createArrayBuilder();
        for (ChatMessage message : userMessages) {
            JsonObjectBuilder messageJson = Json.createObjectBuilder()
                .add("id", message.getId())
                .add("content", message.getContent())
                .add("sender_id", message.getSenderId())
                .add("sender_name", message.getSenderName())
                .add("receiver_id", message.getReceiverId())
                .add("receiver_name", message.getReceiverName())
                .add("create_date", message.getCreateDate().getTime())
                .add("read", message.isRead());
            messagesJson.add(messageJson);
        }
        response.add("messages", messagesJson);

        return Response.ok().entity(response.build()).build();
    }

    @GET
    @Path("/conversation/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConversation(@PathParam("username") String username) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // 获取对方用户
        UserDao userDao = new UserDao();
        User otherUser = userDao.getActiveByUsername(username);
        if (otherUser == null) {
            throw new ServerException("UserNotFound", "User not found: " + username);
        }

        // 获取两个用户之间的对话
        List<ChatMessage> conversation = new ArrayList<>();
        for (ChatMessage message : messages) {
            if ((message.getSenderId().equals(principal.getId()) && message.getReceiverId().equals(otherUser.getId())) ||
                (message.getSenderId().equals(otherUser.getId()) && message.getReceiverId().equals(principal.getId()))) {
                conversation.add(message);
            }
        }

        JsonObjectBuilder response = Json.createObjectBuilder();
        JsonArrayBuilder messagesJson = Json.createArrayBuilder();
        for (ChatMessage message : conversation) {
            JsonObjectBuilder messageJson = Json.createObjectBuilder()
                .add("id", message.getId())
                .add("content", message.getContent())
                .add("sender_id", message.getSenderId())
                .add("sender_name", message.getSenderName())
                .add("receiver_id", message.getReceiverId())
                .add("receiver_name", message.getReceiverName())
                .add("create_date", message.getCreateDate().getTime())
                .add("read", message.isRead());
            messagesJson.add(messageJson);
        }
        response.add("messages", messagesJson);

        return Response.ok().entity(response.build()).build();
    }

    @POST
    @Path("/send/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMessage(@PathParam("username") String username, @FormParam("content") String content) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // 验证输入数据
        ValidationUtil.validateRequired(content, "content");

        // 获取接收者
        UserDao userDao = new UserDao();
        User receiver = userDao.getActiveByUsername(username);
        if (receiver == null) {
            throw new ServerException("UserNotFound", "User not found: " + username);
        }

        // 获取发送者信息
        User sender = userDao.getById(principal.getId());
        if (sender == null) {
            throw new ServerException("UserNotFound", "Sender not found");
        }

        // 创建消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(String.valueOf(idGen.getAndIncrement()));
        chatMessage.setSenderId(principal.getId());
        chatMessage.setSenderName(sender.getUsername());
        chatMessage.setReceiverId(receiver.getId());
        chatMessage.setReceiverName(username);
        chatMessage.setContent(content);
        chatMessage.setCreateDate(new Date());
        chatMessage.setRead(false);

        // 保存消息到内存
        messages.add(chatMessage);

        // 返回创建的消息
        JsonObjectBuilder response = Json.createObjectBuilder()
            .add("id", chatMessage.getId())
            .add("content", content)
            .add("sender_id", principal.getId())
            .add("sender_name", sender.getUsername())
            .add("receiver_id", receiver.getId())
            .add("receiver_name", username)
            .add("create_date", chatMessage.getCreateDate().getTime())
            .add("read", false);

        return Response.ok().entity(response.build()).build();
    }

    @POST
    @Path("/read/{messageId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markAsRead(@PathParam("messageId") String messageId) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        for (ChatMessage message : messages) {
            if (message.getId().equals(messageId) && message.getReceiverId().equals(principal.getId())) {
                message.setRead(true);
                break;
            }
        }

        return Response.ok().build();
    }

    // 消息实体类
    public static class ChatMessage {
        private String id;
        private String content;
        private String senderId;
        private String receiverId;
        private String receiverName;
        private String senderName;
        private Date createDate;
        private boolean read;

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
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

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }
    }
} 