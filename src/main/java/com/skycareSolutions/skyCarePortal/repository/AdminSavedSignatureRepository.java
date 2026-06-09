package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AdminSavedSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminSavedSignatureRepository extends JpaRepository<AdminSavedSignature, String> {

    Optional<AdminSavedSignature> findByAdminEmployeeId(String adminEmployeeId);

    @Query(value = "SELECT id FROM admin_saved_signature ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}
