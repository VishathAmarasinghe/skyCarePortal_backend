package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientRepository extends CrudRepository<Client,String> {

    // Query to find client by email
    @Query("SELECT c FROM Client c WHERE c.email = ?1")
    Client findByEmail(String email);

    @Query("SELECT c FROM Client c ORDER BY c.joinDate DESC")
    List<Client> getAllClients();

    @Query(value = "SELECT c.clientID FROM client c ORDER BY CAST(SUBSTRING(c.clientID, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfClient();
}
