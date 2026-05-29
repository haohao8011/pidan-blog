package com.pidan.blog.service;

import com.pidan.blog.dto.LoginRequest;
import com.pidan.blog.dto.SetupRequest;
import com.pidan.blog.entity.SiteSetting;
import com.pidan.blog.entity.User;
import com.pidan.blog.repository.SiteSettingRepository;
import com.pidan.blog.repository.UserRepository;
import com.pidan.blog.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SiteSettingRepository siteSettingRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       SiteSettingRepository siteSettingRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.siteSettingRepository = siteSettingRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        return jwtTokenProvider.generateToken(user.getUsername());
    }

    public Map<String, Object> getProfile() {
        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("管理员不存在"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("displayName", user.getDisplayName());
        profile.put("createdAt", user.getCreatedAt());
        return profile;
    }

    @Transactional
    public void setup(SetupRequest request) {
        if (userRepository.count() > 0) {
            throw new IllegalArgumentException("系统已初始化，不可重复创建管理员");
        }

        // 创建管理员用户
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername())
                .build();
        userRepository.save(user);

        // 创建默认站点设置
        saveSetting("blog.title", request.getBlogTitle() != null ? request.getBlogTitle() : "我的博客");
        saveSetting("blog.description", request.getBlogDescription() != null ? request.getBlogDescription() : "一个简洁的博客");
        saveSetting("blog.logo", "");
        saveSetting("blog.favicon", "");
        saveSetting("blog.footer", "Powered by Pidan Blog");
    }

    private void saveSetting(String key, String value) {
        SiteSetting setting = SiteSetting.builder()
                .settingKey(key)
                .value(value)
                .build();
        siteSettingRepository.save(setting);
    }
}
