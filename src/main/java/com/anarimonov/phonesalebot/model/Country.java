package com.anarimonov.phonesalebot.model;

import com.anarimonov.phonesalebot.model.abs.AbsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "countries")
public class Country extends AbsEntity {
    public Country(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @ManyToMany(mappedBy = "countries")
    private Collection<Product> products;
}
