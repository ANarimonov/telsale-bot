package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByIdAndBrandIdAndOrderById(Integer id, Integer brandId);

    Product findByName(String name);

    List<Product> findByBrandIdOrderById(Integer brandId);
}
