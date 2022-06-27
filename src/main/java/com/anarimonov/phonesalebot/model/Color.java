package com.anarimonov.phonesalebot.model;

import com.anarimonov.phonesalebot.model.abs.AbsEntity;
import lombok.*;

import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "colors")
public class Color extends AbsEntity {
    public Color(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }

}
