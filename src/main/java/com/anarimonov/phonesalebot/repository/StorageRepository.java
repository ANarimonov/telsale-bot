package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StorageRepository extends JpaRepository<Storage, Integer> {
    Storage findByName(String name);

    @Query(nativeQuery = true, value = "select s.* from storages s " +
            "join products_storage_penalty psp on s.id = psp.storage_id where product_id = :productId")
    List<Storage> findByProductId(Integer productId);
}
