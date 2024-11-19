package com.skycareSolutions.skyCarePortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoteDocumentsDTO {
    private String noteID;
    private String docID;
    private String document;
}
