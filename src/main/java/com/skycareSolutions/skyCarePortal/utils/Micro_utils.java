package com.skycareSolutions.skyCarePortal.utils;

import java.security.SecureRandom;

public class Micro_utils {
    public static String idGenerator(String tableName,String lastId){
        int newLanguageNumber = 0;
        if (lastId!=null && !lastId.equalsIgnoreCase("")){
            String numericPart = lastId.substring(2);  // Extracts "00001"
            System.out.println("log issue");
            newLanguageNumber= Integer.parseInt(numericPart);
        }
        int newID = newLanguageNumber+1;
        String formatTablePrefix="SK";
        if (tableName.equalsIgnoreCase("employee")){
            formatTablePrefix="EM";
        }else if (tableName.equalsIgnoreCase("language")){
            formatTablePrefix="LG";
        }else if(tableName.equalsIgnoreCase("classification")){
            formatTablePrefix="CL";
        }else if (tableName.equalsIgnoreCase("clientType")){
            formatTablePrefix="CT";
        }else if (tableName.equalsIgnoreCase("clientStatus")){
            formatTablePrefix="CS";
        }else if (tableName.equalsIgnoreCase("client")){
            formatTablePrefix="CU";
        }else if(tableName.equalsIgnoreCase("notes")){
            formatTablePrefix="NT";
        }else if (tableName.equalsIgnoreCase("careplan")){
            formatTablePrefix="CP";
        }else if (tableName.equalsIgnoreCase("longTermCarePlan")){
            formatTablePrefix="LP";
        }else if (tableName.equalsIgnoreCase("shortTermCarePlan")){
            formatTablePrefix="SP";
        }else if (tableName.equalsIgnoreCase("goalOutcome")){
            formatTablePrefix="GO";
        }else if (tableName.equalsIgnoreCase("carePlanStatus")){
            formatTablePrefix="PS";
        } else if (tableName.equalsIgnoreCase("carePlanBillables")){
            formatTablePrefix="BL";
        }else if (tableName.equalsIgnoreCase("appointment")){
            formatTablePrefix="AP";
        }else if (tableName.equalsIgnoreCase("appointment-type")){
            formatTablePrefix="AT";
        }else if (tableName.equalsIgnoreCase("appointmentAddress")){
            formatTablePrefix="AD";
        }else if(tableName.equalsIgnoreCase("recurrentWork")){
            formatTablePrefix="RW";
        }else if (tableName.equalsIgnoreCase("jobAssigner")){
            formatTablePrefix="JA";
        }else if (tableName.equalsIgnoreCase("careGiver")){
            formatTablePrefix="CG";
        }else if (tableName.equalsIgnoreCase("careGiverdocumentType")){
            formatTablePrefix="CD";
        }else if (tableName.equalsIgnoreCase("careGiverPaymentType")){
            formatTablePrefix="PT";
        }else if (tableName.equalsIgnoreCase("taskStatus")){
            formatTablePrefix="TS";
        }else if (tableName.equalsIgnoreCase("taskImportance")){
            formatTablePrefix="TI";
        }else if (tableName.equalsIgnoreCase("taskItems")){
            formatTablePrefix="TT";
        }else if (tableName.equalsIgnoreCase("tasks")){
            formatTablePrefix="TA";
        }else if (tableName.equalsIgnoreCase("incidents")){
            formatTablePrefix="IN";
        }else if (tableName.equalsIgnoreCase("incidentStatus")){
            formatTablePrefix="IS";
        }else if (tableName.equalsIgnoreCase("incidentType")){
            formatTablePrefix="IT";
        }else if (tableName.equalsIgnoreCase("incidentActionType")){
            formatTablePrefix="IA";
        }else if (tableName.equalsIgnoreCase("recurrentAppointments")){
            formatTablePrefix="RA";
        }
        return String.format(formatTablePrefix+"%05d", newID);
    }

    public static String generateRandomPassword(int length) {
        // Define the character set for the password
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialCharacters = "!@#$%^&*()-_+=<>?";
        String allCharacters = upperCaseLetters + lowerCaseLetters + digits + specialCharacters;

        // Use SecureRandom for better randomness
        SecureRandom random = new SecureRandom();

        // Build the password
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each group
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        // Fill the rest of the password length with random characters
        for (int i = 4; i < length; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // Shuffle the password for randomness
        return shuffleString(password.toString(), random);
    }

    private static String shuffleString(String input, SecureRandom random) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap characters at i and j
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }


}
