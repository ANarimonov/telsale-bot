package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    @Query(nativeQuery = true, value = "select c.* from countries c join products_countries pc " +
            "on c.id = pc.countries_key where product_id = :productId")
    List<Country> findByProductId(Integer productId);

    Country findByName(String name);
}
