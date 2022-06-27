package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColorRepository extends JpaRepository<Color, Integer> {
    @Query(nativeQuery = true, value = "select c.* from colors c join products_colors pc on c.id = pc.color_id where product_id = :productId")
    List<Color> findByProductId(Integer productId);

    Color findByName(String name);
}
