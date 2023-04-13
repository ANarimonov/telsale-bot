package com.anarimonov.phonesalebot.common;

import com.anarimonov.phonesalebot.bot.Bot;
import com.anarimonov.phonesalebot.repository.*;
import com.anarimonov.phonesalebot.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserActivityService userActivityService;
    private final ChannelRepository channelRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final StorageRepository storageRepository;
    private final CountryRepository countryRepository;
    private final PenaltyRepository penaltyRepository;
    private final BatteryRepository batteryRepository;
    private final RestTemplate restTemplate;

    @Override
    public void run(String... args) {

        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(new Bot(userActivityService,
                    channelRepository,brandRepository,productRepository,colorRepository,storageRepository,countryRepository,penaltyRepository,batteryRepository));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
