package com.anarimonov.phonesalebot;

import com.anarimonov.phonesalebot.bot.Bot;
import com.anarimonov.phonesalebot.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class PhoneSaleBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhoneSaleBotApplication.class, args);
    }
}
