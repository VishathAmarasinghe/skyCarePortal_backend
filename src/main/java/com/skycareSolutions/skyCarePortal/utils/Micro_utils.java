package com.skycareSolutions.skyCarePortal.utils;

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
        }
        return String.format(formatTablePrefix+"%05d", newID);
    }
}
