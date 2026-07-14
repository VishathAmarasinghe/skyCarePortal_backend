package com.skycareSolutions.skyCarePortal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hibernate ddl-auto=update does not relax NOT NULL on existing FK columns.
 * Behaviour data allows optional staff, so staffEmployeeID must be nullable.
 */
@Component
public class BehaviourDataSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BehaviourDataSchemaPatch.class);

    private static final String TABLE = "behaviour_data_record";
    private static final Set<String> NULLABLE_STAFF_COLUMNS = Set.of(
            "staffEmployeeID",
            "staffemployeeid",
            "staff_employeeid",
            "staff_employee_id"
    );

    private final JdbcTemplate jdbcTemplate;

    public BehaviourDataSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureStaffColumnNullable();
        } catch (Exception ex) {
            log.warn("Could not patch behaviour_data_record schema: {}", ex.getMessage());
        }
    }

    private void ensureStaffColumnNullable() {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                """
                        SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE
                        FROM INFORMATION_SCHEMA.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = ?
                          AND LOWER(COLUMN_NAME) IN (
                            'staffemployeeid',
                            'staff_employeeid',
                            'staff_employee_id'
                          )
                        """,
                TABLE
        );

        for (Map<String, Object> column : columns) {
            String name = String.valueOf(column.get("COLUMN_NAME"));
            String type = String.valueOf(column.get("COLUMN_TYPE"));
            String nullable = String.valueOf(column.get("IS_NULLABLE"));
            if (!NULLABLE_STAFF_COLUMNS.contains(name) && !NULLABLE_STAFF_COLUMNS.contains(name.toLowerCase())) {
                continue;
            }
            if ("YES".equalsIgnoreCase(nullable)) {
                continue;
            }
            String sql = "ALTER TABLE `" + TABLE + "` MODIFY `" + name + "` " + type + " NULL";
            log.info("Applying behaviour data schema patch: {}", sql);
            jdbcTemplate.execute(sql);
        }
    }
}
