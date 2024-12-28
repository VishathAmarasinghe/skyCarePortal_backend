package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Employee;
import com.skycareSolutions.skyCarePortal.model.Language;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LanguageRepository extends CrudRepository<Language,String> {

    @Query("SELECT l FROM Language l")
    List<Language> getAllLanguages();

    @Query(value = "SELECT l.languageID FROM language l ORDER BY CAST(SUBSTRING(l.languageID, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfLanguage();

    @Query(value = "SELECT * FROM language l WHERE l.language=?1", nativeQuery = true)
    Language getlanguageIDByLanguageName(String language);
}
