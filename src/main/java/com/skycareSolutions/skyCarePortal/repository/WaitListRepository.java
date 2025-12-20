package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.WaitList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaitListRepository extends JpaRepository<WaitList, Long> {
    boolean existsByEmail(String email);
    Optional<WaitList> findByEmail(String email);
}
