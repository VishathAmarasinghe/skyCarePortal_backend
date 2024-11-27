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
        }
        return String.format(formatTablePrefix+"%05d", newID);
    }
}
