package com.anarimonov.phonesalebot.model;

import com.anarimonov.phonesalebot.model.abs.AbsEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "brands")
public class Brand extends AbsEntity {

    @Column(columnDefinition = "text")
    private String message;

    public Brand(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
