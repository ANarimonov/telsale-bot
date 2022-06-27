package com.anarimonov.phonesalebot.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "channels")
public class Channel {
    public Channel(String channelId) {
        this.channelId = channelId;
    }

    @Id
    @GeneratedValue
    private Integer id;
    private String channelId;

}
