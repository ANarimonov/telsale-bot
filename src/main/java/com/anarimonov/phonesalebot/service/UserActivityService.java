package com.anarimonov.phonesalebot.service;

import com.anarimonov.phonesalebot.model.User;
import com.anarimonov.phonesalebot.model.UserActivity;
import com.anarimonov.phonesalebot.repository.UserActivityRepository;
import com.anarimonov.phonesalebot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


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
            return userActivityRepository.save(new UserActivity(user, null, "user", 0));
        }
    }

    public void update(UserActivity userActivity) {
        userActivityRepository.save(userActivity);
    }
}
