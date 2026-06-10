package com.skycareSolutions.skyCarePortal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Hibernate ddl-auto=update does not always relax NOT NULL on existing FK columns.
 * Manual staff names require employeeid to be nullable.
 */
@Component
public class StaffTrainingSchemaPatch implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StaffTrainingSchemaPatch.class);

    private static final String TABLE = "staff_training_record";
    private static final List<String> NULLABLE_EMPLOYEE_COLUMNS = List.of(
            "employeeID",
            "employeeid",
            "assessorEmployeeID",
            "assessor_employeeid",
            "assessoremployeeid"
    );

    private final JdbcTemplate jdbcTemplate;

    public StaffTrainingSchemaPatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureEmployeeColumnsNullable();
        } catch (Exception ex) {
            log.warn("Could not patch staff_training_record schema: {}", ex.getMessage());
        }
    }

    private void ensureEmployeeColumnsNullable() {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                """
                        SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE
                        FROM INFORMATION_SCHEMA.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = ?
                          AND COLUMN_NAME IN ('employeeID', 'employeeid', 'assessorEmployeeID', 'assessor_employeeid', 'assessoremployeeid')
                        """,
                TABLE
        );

        for (Map<String, Object> column : columns) {
            String name = String.valueOf(column.get("COLUMN_NAME"));
            String type = String.valueOf(column.get("COLUMN_TYPE"));
            String nullable = String.valueOf(column.get("IS_NULLABLE"));
            if (!NULLABLE_EMPLOYEE_COLUMNS.contains(name) || "YES".equalsIgnoreCase(nullable)) {
                continue;
            }
            String sql = "ALTER TABLE `" + TABLE + "` MODIFY `" + name + "` " + type + " NULL";
            log.info("Applying staff training schema patch: {}", sql);
            jdbcTemplate.execute(sql);
        }
    }
}
