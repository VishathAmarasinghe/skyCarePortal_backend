package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.EmailTemplates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<EmailTemplates,String> {

    List<EmailTemplates> findByCategoryStartingWithOrderByTemplateIDAsc(String categoryPrefix);
}

