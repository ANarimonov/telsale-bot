package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
