package com.skycareSolutions.skyCarePortal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Hibernate ddl-auto=update does not widen existing VARCHAR columns to TEXT.
 * Care plan free-text fields (goals, living situation, supports, risk, outcomes)
 * must allow content longer than 255 characters.
 */
@Component
public class CarePlanTextSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CarePlanTextSchemaPatch.class);

    private static final Map<String, Set<String>> TABLE_TEXT_COLUMNS = Map.of(
            "careplan", Set.of(
                    "currentlivingsituation",
                    "current_living_situation",
                    "supportsfundedcore",
                    "supports_funded_core",
                    "supportsfundedcapacitybuilding",
                    "supports_funded_capacity_building",
                    "supportsfundedcapital",
                    "supports_funded_capital",
                    "riskmanagement",
                    "risk_management",
                    "expectedoutcomes",
                    "expected_outcomes",
                    "billingbudgetnotes",
                    "billing_budget_notes"
            ),
            "care_plan_long_term_goal", Set.of(
                    "longtermgoal",
                    "long_term_goal",
                    "achieveway",
                    "achieve_way",
                    "supportway",
                    "support_way",
                    "notes"
            ),
            "care_plan_short_term_goal", Set.of(
                    "goaltitle",
                    "goal_title",
                    "goaldescription",
                    "goal_description",
                    "outcomedetails",
                    "outcome_details",
                    "goalstrategy",
                    "goal_strategy"
            ),
            "careplan_support_schedule", Set.of(
                    "supporttype",
                    "support_type"
            )
    );

    private final JdbcTemplate jdbcTemplate;

    public CarePlanTextSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        TABLE_TEXT_COLUMNS.forEach((table, columnKeys) -> {
            try {
                widenTextColumns(table, columnKeys);
            } catch (Exception ex) {
                log.warn("Could not patch {} text columns: {}", table, ex.getMessage());
            }
        });
    }

    private void widenTextColumns(String table, Set<String> columnKeys) {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                """
                        SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, EXTRA
                        FROM INFORMATION_SCHEMA.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = ?
                        """,
                table
        );

        for (Map<String, Object> column : columns) {
            String name = String.valueOf(column.get("COLUMN_NAME"));
            String type = String.valueOf(column.get("COLUMN_TYPE")).toLowerCase(Locale.ROOT);
            String nullable = String.valueOf(column.get("IS_NULLABLE"));
            String normalized = name.toLowerCase(Locale.ROOT).replace("_", "");

            boolean matches = columnKeys.contains(name.toLowerCase(Locale.ROOT))
                    || columnKeys.contains(normalized)
                    || columnKeys.stream().anyMatch(key -> key.replace("_", "").equals(normalized));
            if (!matches) {
                continue;
            }
            if (type.startsWith("text") || type.startsWith("mediumtext") || type.startsWith("longtext")) {
                continue;
            }

            String nullClause = "YES".equalsIgnoreCase(nullable) ? "NULL" : "NOT NULL";
            String sql = "ALTER TABLE `" + table + "` MODIFY `" + name + "` TEXT " + nullClause;
            log.info("Applying care plan text schema patch: {}", sql);
            jdbcTemplate.execute(sql);
        }
    }
}
