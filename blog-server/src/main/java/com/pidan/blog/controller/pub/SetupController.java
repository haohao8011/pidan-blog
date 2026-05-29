package com.pidan.blog.controller.pub;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.dto.SetupRequest;
import com.pidan.blog.repository.UserRepository;
import com.pidan.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    private final UserRepository userRepository;
    private final UserService userService;

    public SetupController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/status")
    public ApiResponse<Map<String, Boolean>> checkStatus() {
        boolean initialized = userRepository.count() > 0;
        return ApiResponse.success(Map.of("initialized", initialized));
    }

    @PostMapping
    public ApiResponse<Map<String, String>> testDatabase() {
        // 如果能访问到 userRepository，说明数据库连接正常
        try {
            userRepository.count();
            return ApiResponse.success("数据库连接成功", Map.of("status", "ok"));
        } catch (Exception e) {
            return ApiResponse.error(500, "数据库连接失败: " + e.getMessage());
        }
    }

    @PostMapping("/admin")
    public ApiResponse<Void> createAdmin(@Valid @RequestBody SetupRequest request) {
        userService.setup(request);
        return ApiResponse.success("初始化成功", null);
    }
}
