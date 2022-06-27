package com.anarimonov.phonesalebot.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
}
