package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {

    Channel findByChannelId(String channelId);
}
