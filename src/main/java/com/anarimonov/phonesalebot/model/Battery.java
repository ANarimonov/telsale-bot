package com.anarimonov.phonesalebot.model;

import com.anarimonov.phonesalebot.model.abs.AbsEntity;
import lombok.*;

import javax.persistence.Entity;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "batteries")
public class Battery extends AbsEntity {
    public Battery(String name) {
        super(name);
    }
}
