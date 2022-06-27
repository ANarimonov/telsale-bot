package com.anarimonov.phonesalebot.model;

import com.anarimonov.phonesalebot.model.abs.AbsEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "storages")
public class Storage extends AbsEntity {
    public Storage(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
