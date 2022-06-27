package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Integer> {
    UserActivity findByUserId(Long userId);
    List<UserActivity> findByRole(String role);
}
