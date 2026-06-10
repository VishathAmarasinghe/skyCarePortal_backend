-- Prometheus mysqld_exporter user (must match monitoring/docker-compose.yml)
-- Username: exporter_user
-- Password: choose a strong password and set the same value in Jenkins credential
--   SKYCARE_MONITORING_MYSQL_EXPORTER_PASSWORD and MYSQLD_EXPORTER_PASSWORD at deploy time.
--
-- Run as MySQL admin (e.g. root), on the SAME server mysqld-exporter reaches
-- (compose uses host.docker.internal:3306 — usually MySQL on the host).
--
-- If exporter runs on another host, replace '%' with that host IP or use '%' only if acceptable.

-- MySQL 8+
CREATE USER IF NOT EXISTS 'exporter_user'@'%' IDENTIFIED BY 'CHANGEME_STRONG_PASSWORD';

-- Minimum privileges for prom/mysqld-exporter
GRANT PROCESS, REPLICATION CLIENT ON *.* TO 'exporter_user'@'%';
GRANT SELECT ON performance_schema.* TO 'exporter_user'@'%';

FLUSH PRIVILEGES;

-- Verify (optional):
-- SHOW GRANTS FOR 'exporter_user'@'%';
