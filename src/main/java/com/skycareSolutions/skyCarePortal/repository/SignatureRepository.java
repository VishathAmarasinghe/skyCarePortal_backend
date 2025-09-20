package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Signature;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignatureRepository extends CrudRepository<Signature, Long> {

    // Find signature by employee ID
    @Query("SELECT s FROM Signature s WHERE s.employeeID = ?1")
    Optional<Signature> findByEmployeeID(String employeeID);

    // Check if signature exists for employee
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Signature s WHERE s.employeeID = ?1")
    boolean existsByEmployeeID(String employeeID);

    // Delete signature by employee ID
    @Query("DELETE FROM Signature s WHERE s.employeeID = ?1")
    void deleteByEmployeeID(String employeeID);
}
