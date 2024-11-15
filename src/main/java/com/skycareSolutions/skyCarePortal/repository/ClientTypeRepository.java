package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Classification;
import com.skycareSolutions.skyCarePortal.model.ClientType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientTypeRepository extends CrudRepository<ClientType,String> {

    // Find clienttype by name
    @Query("SELECT t FROM ClientType t WHERE t.name = ?1")
    ClientType findByClientTypeName(String name);

    // Get all classifications
    @Query("SELECT t FROM ClientType t")
    List<ClientType> findAll();

    // Get the last classification ID
    @Query(value = "SELECT t.client_typeid FROM client_type t ORDER BY CAST(SUBSTRING(t.client_typeid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfClientType();
}
