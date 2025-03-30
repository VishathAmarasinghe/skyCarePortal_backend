package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.ChatDocuments;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChatDocumentRepository extends CrudRepository<ChatDocuments,Long> {
    Optional<ChatDocuments> findFirstByOrderByDocumentIdDesc();
}
