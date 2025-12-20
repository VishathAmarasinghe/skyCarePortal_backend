package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.NaplicWaitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NaplicWaitlistRepository extends JpaRepository<NaplicWaitlist, Long> {
    boolean existsByEmail(String email);
    Optional<NaplicWaitlist> findByEmail(String email);
}

