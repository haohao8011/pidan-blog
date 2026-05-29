package com.pidan.blog.controller.pub;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.service.SettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public ApiResponse<Map<String, String>> getPublicSettings() {
        Map<String, String> settings = settingService.getPublic();
        return ApiResponse.success(settings);
    }
}
