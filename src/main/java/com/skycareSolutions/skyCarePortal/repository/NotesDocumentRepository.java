package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.NoteDocuments;
import com.skycareSolutions.skyCarePortal.model.NoteDocumentsKey;
import org.springframework.data.repository.CrudRepository;

public interface NotesDocumentRepository extends CrudRepository<NoteDocuments, NoteDocumentsKey> {

}
