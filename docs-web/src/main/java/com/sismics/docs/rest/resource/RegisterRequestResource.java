package com.sismics.docs.rest.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;
import jakarta.ws.rs.FormParam;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.constant.Constants;

@Path("/register-request")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class RegisterRequestResource {
    // 临时内存存储，后续可替换为数据库DAO
    private static final CopyOnWriteArrayList<RegisterRequest> requests = new CopyOnWriteArrayList<>();
    private static final AtomicInteger idGen = new AtomicInteger(1);

    // 访客提交注册申请
    @POST
    public Response submitRequest(@FormParam("username") String username,
                                  @FormParam("email") String email,
                                  @FormParam("password") String password,
                                  @FormParam("message") String message) {
        RegisterRequest req = new RegisterRequest();
        req.id = idGen.getAndIncrement();
        req.username = username;
        req.email = email;
        req.password = password;
        req.message = message;
        req.status = "pending";
        req.createTime = new java.sql.Timestamp(System.currentTimeMillis());
        requests.add(req);
        return Response.ok().build();
    }

    // 管理员获取所有注册申请
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getAllRequests() {
        StringBuilder sb = new StringBuilder();
        for (RegisterRequest req : requests) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("id=").append(req.id)
              .append("&username=").append(encode(req.username))
              .append("&email=").append(encode(req.email))
              .append("&password=").append(encode(req.password))
              .append("&message=").append(encode(req.message))
              .append("&status=").append(encode(req.status))
              .append("&createTime=").append(req.createTime != null ? encode(req.createTime.toString()) : "")
              .append("&processTime=").append(req.processTime != null ? encode(req.processTime.toString()) : "");
        }
        return Response.ok(sb.toString()).build();
    }

    private String encode(String s) {
        if (s == null) return "";
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }

    // 管理员审批通过
    @POST
    @Path("/{id}/approve")
    public Response approveRequest(@PathParam("id") String id) {
        for (RegisterRequest req : requests) {
            if (String.valueOf(req.id).equals(id)) {
                req.status = "approved";
                req.processTime = new java.sql.Timestamp(System.currentTimeMillis());
                // 自动创建正式用户账号
                try {
                    User user = new User();
                    user.setRoleId(Constants.DEFAULT_USER_ROLE);
                    user.setUsername(req.username);
                    user.setPassword(req.password);
                    user.setEmail(req.email);
                    user.setOnboarding(true);
                    user.setStorageQuota(1073741824L); // 1GB 默认配额，可根据需要调整
                    UserDao userDao = new UserDao();
                    userDao.create(user, "system"); // "system" 代表系统操作
                } catch (Exception e) {
                    // 用户已存在等异常
                    return Response.status(Response.Status.CONFLICT).entity("User creation failed: " + e.getMessage()).build();
                }
                return Response.ok().build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 管理员拒绝
    @POST
    @Path("/{id}/reject")
    public Response rejectRequest(@PathParam("id") String id) {
        for (RegisterRequest req : requests) {
            if (String.valueOf(req.id).equals(id)) {
                req.status = "rejected";
                req.processTime = new java.sql.Timestamp(System.currentTimeMillis());
                return Response.ok().build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // DTO类（可单独放到dto包）
    public static class RegisterRequestDto {
        public int id;
        public String username;
        public String email;
        public String password;
        public String message;
        public String status;
        public String createTime;
        public String processTime;
    }

    // 实体类（可单独放到model包）
    public static class RegisterRequest {
        public int id;
        public String username;
        public String email;
        public String password;
        public String message;
        public String status;
        public java.sql.Timestamp createTime;
        public java.sql.Timestamp processTime;
        public Integer adminId;
    }
} 