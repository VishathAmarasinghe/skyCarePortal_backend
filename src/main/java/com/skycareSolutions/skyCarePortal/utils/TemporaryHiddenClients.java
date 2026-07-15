package com.skycareSolutions.skyCarePortal.utils;

import java.util.Locale;
import java.util.Set;

/**
 * TEMPORARY: hard-coded clients to hide from lists, lookups, and timesheets.
 * Remove this denylist (or clear the set) when the temporary hide is no longer needed.
 */
public final class TemporaryHiddenClients {

    private static final Set<String> HIDDEN_CLIENT_IDS = Set.of(
            "CU53",
            "CU49",
            "CU43",
            "CU39",
            "CU38",
            "CU36",
            "CU37",
            "CU31"
    );

    private TemporaryHiddenClients() {
    }

    public static boolean isHidden(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            return false;
        }
        return HIDDEN_CLIENT_IDS.contains(clientId.trim().toUpperCase(Locale.ROOT));
    }
}
