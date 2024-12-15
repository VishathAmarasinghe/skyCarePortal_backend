package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Client;
import com.skycareSolutions.skyCarePortal.model.ClientDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientDocumentRepository extends JpaRepository<ClientDocuments, String> {

    @Query(value = "SELECT cd.client_documentid FROM client_documents cd ORDER BY CAST(SUBSTRING(cd.client_documentid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfClientDocument();

    @Query("SELECT cd from ClientDocuments cd WHERE clientid=:clientID")
    List<ClientDocuments> getDocumentsByClientID(Client clientID);
}
