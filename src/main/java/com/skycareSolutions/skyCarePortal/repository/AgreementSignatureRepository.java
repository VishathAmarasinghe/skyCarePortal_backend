package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgreementSignatureRepository extends JpaRepository<AgreementSignature, String> {

    @Query(value = "SELECT id FROM agreement_signature ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}
