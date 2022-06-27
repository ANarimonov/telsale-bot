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
@Entity
public class Penalty {
    public Penalty(double amount) {
        this.amount = amount;
    }

    @Id
    @GeneratedValue
    private Long id;
    private double amount;

}
