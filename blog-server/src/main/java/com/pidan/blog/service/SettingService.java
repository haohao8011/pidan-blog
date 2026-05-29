package com.pidan.blog.service;

import com.pidan.blog.entity.SiteSetting;
import com.pidan.blog.repository.SiteSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettingService {

    private final SiteSettingRepository siteSettingRepository;

    public SettingService(SiteSettingRepository siteSettingRepository) {
        this.siteSettingRepository = siteSettingRepository;
    }

    public Map<String, String> getPublic() {
        List<SiteSetting> settings = siteSettingRepository.findAll();
        Map<String, String> result = new HashMap<>();
        for (SiteSetting setting : settings) {
            // 不暴露 jwt_secret
            if (!"jwt_secret".equals(setting.getSettingKey())) {
                result.put(setting.getSettingKey(), setting.getValue());
            }
        }
        return result;
    }

    public Map<String, String> getAll() {
        List<SiteSetting> settings = siteSettingRepository.findAll();
        Map<String, String> result = new HashMap<>();
        for (SiteSetting setting : settings) {
            result.put(setting.getSettingKey(), setting.getValue());
        }
        return result;
    }

    @Transactional
    public void update(Map<String, String> settingsMap) {
        for (Map.Entry<String, String> entry : settingsMap.entrySet()) {
            SiteSetting setting = siteSettingRepository.findBySettingKey(entry.getKey())
                    .orElse(SiteSetting.builder()
                            .settingKey(entry.getKey())
                            .build());
            setting.setValue(entry.getValue());
            siteSettingRepository.save(setting);
        }
    }

    @Transactional
    public void update(String key, String value) {
        SiteSetting setting = siteSettingRepository.findBySettingKey(key)
                .orElse(SiteSetting.builder()
                        .settingKey(key)
                        .build());
        setting.setValue(value);
        siteSettingRepository.save(setting);
    }
}
