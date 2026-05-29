package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.dto.LoginRequest;
import com.pidan.blog.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AuthController {

    private final UserService userService;

    @Value("${blog.jwt.expiration:604800000}")
    private long jwtExpiration;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@Valid @RequestBody LoginRequest request,
                                                   HttpServletResponse response) {
        String token = userService.authenticate(request.getUsername(), request.getPassword());

        Cookie cookie = new Cookie("pidan-blog-token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 开发环境设为 false，生产环境应设为 true
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpiration / 1000));
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);

        return ApiResponse.success(Map.of("message", "登录成功"));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("pidan-blog-token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ApiResponse.success("登出成功", null);
    }
}
