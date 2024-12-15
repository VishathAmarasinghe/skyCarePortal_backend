package com.skycareSolutions.skyCarePortal.dto;

import java.util.HashMap;
import java.util.Map;

public class MetaEmployeeCareGiverMapDTO {

    private Map<String, String> employeeCareGiverMap;

    // Constructor
    public MetaEmployeeCareGiverMapDTO() {
        this.employeeCareGiverMap = new HashMap<>();
    }

    // Getter and Setter for the Map
    public Map<String, String> getEmployeeCareGiverMap() {
        return employeeCareGiverMap;
    }

    public void setEmployeeCareGiverMap(Map<String, String> employeeCareGiverMap) {
        this.employeeCareGiverMap = employeeCareGiverMap;
    }

    // Add a mapping
    public void addMapping(String employeeID, String careGiverID) {
        this.employeeCareGiverMap.put(employeeID, careGiverID);
    }

    // Get a caregiver for a given employee ID
    public String getCareGiver(String employeeID) {
        return this.employeeCareGiverMap.get(employeeID);
    }

    // Remove a mapping
    public void removeMapping(String employeeID) {
        this.employeeCareGiverMap.remove(employeeID);
    }

    // Override toString for better representation
    @Override
    public String toString() {
        return "MetaEmployeeCareGiverMapDTO{" +
                "employeeCareGiverMap=" + employeeCareGiverMap +
                '}';
    }
}
