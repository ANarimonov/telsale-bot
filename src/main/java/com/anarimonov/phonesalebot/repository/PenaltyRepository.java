package com.anarimonov.phonesalebot.repository;

import com.anarimonov.phonesalebot.model.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PenaltyRepository extends JpaRepository<Penalty, Integer> {
    Penalty findByAmount(double penalty);

    @Query(nativeQuery = true,value = "select distinct p.* from penalty p join products_battery pb on p.id = pb.penalty_id where battery_key=:batteryId")
    Penalty findByBatteryId(Integer batteryId);
    @Query(nativeQuery = true,value = "select distinct p.* from penalty p join products_storage ps on p.id = ps.penalty_id where storage_key=:storageId")
    Penalty findByStorageId(Integer storageId);
}
