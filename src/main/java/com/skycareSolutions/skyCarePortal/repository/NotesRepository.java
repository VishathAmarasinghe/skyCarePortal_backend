package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Notes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotesRepository extends CrudRepository<Notes, String> {

    @Query("SELECT n FROM Notes n ORDER BY n.createDate DESC")
    List<Notes> getAllNotes();

    @Query("SELECT n FROM Notes n WHERE n.clientID.clientID = :clientID ORDER BY n.createDate DESC")
    List<Notes> findByClientID(String clientID);

    @Query(value = "SELECT n.noteid FROM Notes n ORDER BY CAST(SUBSTRING(n.noteid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfNotes();
}
