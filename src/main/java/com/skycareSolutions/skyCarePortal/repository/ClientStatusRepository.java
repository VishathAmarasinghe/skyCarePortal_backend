package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.ClientStatus;
import com.skycareSolutions.skyCarePortal.model.ClientType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientStatusRepository extends CrudRepository<ClientStatus,String> {
    // Find clientStatus by name
    @Query("SELECT s FROM ClientStatus s WHERE s.status = ?1")
    ClientType findByClientStatusName(String status);

    // Get all classifications
    @Query("SELECT s FROM ClientStatus s")
    List<ClientStatus> findAll();

    // Get the last classification ID
    @Query(value = "SELECT s.client_statusid FROM client_status s ORDER BY CAST(SUBSTRING(s.client_statusid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfClientStatus();
}
