package com.anarimonov.phonesalebot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity(name = "usersActivity")
public class UserActivity {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne
    private User user;
    private String languageCode;
    private String role;
    private int step;

    public UserActivity(User user, String languageCode, String role, int step) {
        this.user = user;
        this.languageCode = languageCode;
        this.role = role;
        this.step = step;
    }

    public UserActivity setId(Integer id) {
        this.id = id;
        return this;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserActivity setStep(int step) {
        this.step = step;
        return this;
    }
}
