package com.skycareSolutions.skyCarePortal.utils;

import java.util.Locale;
import java.util.Set;

/**
 * TEMPORARY: hard-coded employees/caregivers to hide from profiles, timesheets,
 * dashboards, and related lists. Remove this denylist when no longer needed.
 */
public final class TemporaryHiddenEmployees {

    private static final Set<String> HIDDEN_EMPLOYEE_IDS = Set.of(
            "EM105", "EM118", "EM111", "EM103", "EM112", "EM102", "EM101", "EM100",
            "EM96", "EM92", "EM91", "EM90", "EM88", "EM86", "EM87", "EM85",
            "EM83", "EM82", "EM81", "EM80", "EM78", "EM75", "EM72", "EM73",
            "EM70", "EM69", "EM67", "EM66", "EM62", "EM63", "EM65", "EM61",
            "EM59", "EM56", "EM55", "EM53", "EM50", "EM47", "EM39", "EM40",
            "EM37", "EM13", "EM25", "EM21", "EM48", "EM16", "EM30", "EM26",
            "EM24", "EM77", "EM33", "EM29", "EM28", "EM35",
            // Added batch
            "EM114", "EM108", "EM98", "EM84", "EM9", "EM79", "EM74", "EM60",
            "EM54", "EM52", "EM49", "EM34", "EM38", "EM36", "EM14", "EM5", "EM124"
    );

    private static final Set<String> HIDDEN_CAREGIVER_IDS = Set.of(
            "CG101", "CG114", "CG107", "CG99", "CG108", "CG98", "CG97", "CG96",
            "CG92", "CG88", "CG87", "CG86", "CG84", "CG82", "CG83", "CG81",
            "CG79", "CG78", "CG77", "CG76", "CG74", "CG71", "CG68", "CG69",
            "CG66", "CG65", "CG63", "CG62", "CG58", "CG59", "CG61", "CG57",
            "CG55", "CG53", "CG52", "CG50", "CG47", "CG44", "CG36", "CG37",
            "CG34", "CG9", "CG22", "CG18", "CG45", "CG12", "CG27", "CG23",
            "CG21", "CG73", "CG30", "CG26", "CG25", "CG32",
            // Added batch
            "CG110", "CG104", "CG94", "CG80", "CG5", "CG75", "CG70", "CG56",
            "CG51", "CG49", "CG46", "CG31", "CG35", "CG33", "CG10", "CG1", "CG120"
    );

    private TemporaryHiddenEmployees() {
    }

    public static boolean isHiddenEmployee(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return false;
        }
        return HIDDEN_EMPLOYEE_IDS.contains(employeeId.trim().toUpperCase(Locale.ROOT));
    }

    public static boolean isHiddenCareGiver(String careGiverId) {
        if (careGiverId == null || careGiverId.isBlank()) {
            return false;
        }
        return HIDDEN_CAREGIVER_IDS.contains(careGiverId.trim().toUpperCase(Locale.ROOT));
    }

    /** True if the id is either a hidden employee or caregiver id. */
    public static boolean isHidden(String id) {
        return isHiddenEmployee(id) || isHiddenCareGiver(id);
    }
}
