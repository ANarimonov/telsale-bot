package com.anarimonov.phonesalebot.model;

import com.anarimonov.phonesalebot.model.abs.AbsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "brands")
public class Brand extends AbsEntity {

    private String message;

    public Brand(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}