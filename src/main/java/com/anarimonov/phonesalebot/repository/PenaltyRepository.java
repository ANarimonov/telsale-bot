package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PenaltyRepository extends JpaRepository<Penalty, Integer> {
    Penalty findByAmount(double penalty);

    @Query(nativeQuery = true,value = "select distinct p.* from penalty p join products_battery pb on p.id = pb.penalty_id where battery_key=:batteryId and product_id = :productId")
    Penalty findByBatteryIdAndProductId(Integer batteryId, Integer productId);
    @Query(nativeQuery = true,value = "select distinct p.* from penalty p join products_storages ps on p.id = ps.penalty_id where storages_key=:storageId and product_id = :productId")
    Penalty findByStorageIdAndProductId(Integer storageId, Integer productId);
    @Query(nativeQuery = true,value = "select distinct p.* from penalty p join products_countries ps on p.id = ps.penalty_id where ps.countries_key=:countryId and product_id = :productId")
    Penalty findByCountryIdAndProductId(Integer countryId, Integer productId);
}
