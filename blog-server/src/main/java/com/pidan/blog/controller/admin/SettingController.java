package com.pidan.blog.controller.admin;

import com.pidan.blog.common.ApiResponse;
import com.pidan.blog.service.SettingService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public ApiResponse<Map<String, String>> getAllSettings() {
        Map<String, String> settings = settingService.getAll();
        return ApiResponse.success(settings);
    }

    @PutMapping
    public ApiResponse<Void> updateSettings(@RequestBody Map<String, String> settingsMap) {
        settingService.update(settingsMap);
        return ApiResponse.success("设置更新成功", null);
    }
}
