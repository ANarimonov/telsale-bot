package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Brand findByName(String name);

    List<Brand> findByMessageNotNull();
    @Query(nativeQuery = true, value = "select distinct b.* from brands b join products p on b.id = p.brand_id ")
    List<Brand> findByExistsProducts();
}
