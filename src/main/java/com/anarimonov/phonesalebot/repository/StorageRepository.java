package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Integer> {
    Storage findByName(String name);

    @Query(nativeQuery = true, value = "select s.* from storages s " +
            "join products_storages ps on s.id = ps.storages_key where product_id = :productId")
    List<Storage> findByProductId(Integer productId);
}
