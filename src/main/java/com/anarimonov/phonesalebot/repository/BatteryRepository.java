package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Battery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BatteryRepository extends JpaRepository<Battery, Integer> {
    @Query(nativeQuery = true,value = "select distinct b.* from batteries b join products_battery pb on b.id = pb.battery_key where product_id = :productId")
    List<Battery> findByProductId(Integer productId);

    Battery findByName(String capacity);
}
