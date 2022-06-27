package com.anarimonov.phonesalebot.model.abs;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class AbsEntity {
    public AbsEntity(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    private Integer id;
    private String name;


}
