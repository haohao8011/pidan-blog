package com.pidan.blog.repository;

import com.pidan.blog.entity.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, UUID> {

    Optional<SiteSetting> findBySettingKey(String settingKey);
}
