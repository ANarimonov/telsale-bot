package com.anarimonov.phonesalebot.service;

import com.anarimonov.phonesalebot.model.User;
import com.anarimonov.phonesalebot.model.UserActivity;
import com.anarimonov.phonesalebot.repository.UserActivityRepository;
import com.anarimonov.phonesalebot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;


@Service
public record UserActivityService(UserActivityRepository userActivityRepository, UserRepository userRepository) {

    public UserActivity findByUserId(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        UserActivity userActivity = userActivityRepository.findByUserId(chatId);
        if (userActivity != null) {
            return userActivity;
        } else {
            org.telegram.telegrambots.meta.api.objects.User from = message.getFrom();
            User user = userRepository.save(new User(chatId, from.getFirstName(), from.getLastName(), null, from.getUserName()));
            return userActivityRepository.save(new UserActivity(user, "uz", "user", 0));
        }
    }

    public void update(UserActivity userActivity) {
        userActivityRepository.save(userActivity);
    }

    public String getAdminPhoneNumber() {
        List<UserActivity> admins = userActivityRepository.findByRole("admin");
        if (!admins.isEmpty()) {
            return admins.get(0).getUser().getPhoneNumber();
        }
        return "";
    }
}
