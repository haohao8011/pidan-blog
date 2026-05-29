package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.entity.User;
import com.pidan.blog.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/profile")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        Map<String, Object> profile = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "displayName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername(),
                "createdAt", user.getCreatedAt()
        );

        return ApiResponse.success(profile);
    }

    @PutMapping
    public ApiResponse<Map<String, Object>> updateProfile(@RequestBody Map<String, String> body,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (body.containsKey("displayName")) {
            user.setDisplayName(body.get("displayName"));
        }
        userRepository.save(user);

        Map<String, Object> profile = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "displayName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername()
        );

        return ApiResponse.success("个人信息更新成功", profile);
    }
}
