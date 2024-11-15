package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Classification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassificationRepository extends CrudRepository<Classification, String> {

    // Check if a classification exists by name
    boolean existsByClassificationName(String classificationName);

    // Find classification by name
    @Query("SELECT c FROM Classification c WHERE c.classificationName = ?1")
    Classification findByClassificationName(String name);

    // Get all classifications
    @Query("SELECT c FROM Classification c")
    List<Classification> findAll();

    // Get the last classification ID
    @Query(value = "SELECT c.classificationID FROM Classification c ORDER BY CAST(SUBSTRING(c.classificationID, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfClassification();
}
