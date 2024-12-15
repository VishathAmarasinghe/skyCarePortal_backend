package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.ShiftNoteDocuments;
import com.skycareSolutions.skyCarePortal.model.ShiftNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShiftDocumentRepository extends JpaRepository<ShiftNoteDocuments,String> {
    @Query(value = "SELECT r.document_id FROM shift_notes_documents r ORDER BY CAST(SUBSTRING(r.document_id, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfShiftNotesDocuments();

    @Query("SELECT r from ShiftNoteDocuments r where shiftNotes=:shiftNotes")
    List<ShiftNoteDocuments> getShiftNoteDocumentsRelatedToShiftNote(ShiftNotes shiftNotes);
}
