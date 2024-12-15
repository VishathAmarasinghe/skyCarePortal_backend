package com.skycareSolutions.skyCarePortal.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateIncidentQuestionsDTO {
    private String mainQuestion;
    private boolean yesNoType;
    private List<String> subQuestions;
}
