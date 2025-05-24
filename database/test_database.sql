-- =========================================
-- Ghost Net Fishing Database Test Script
-- =========================================

USE ghostnet_fishing;

-- =========================================
-- Teste Tabellen-Struktur
-- =========================================

SELECT 'Testing database structure...' as Status;

-- Prüfe ob alle Tabellen existieren
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH
FROM 
    information_schema.TABLES 
WHERE 
    TABLE_SCHEMA = 'ghostnet_fishing'
    AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- =========================================
-- Teste Constraints
-- =========================================

SELECT 'Testing constraints...' as Status;

-- Teste Check Constraints (falls unterstützt)
SELECT 
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE,
    TABLE_NAME
FROM 
    information_schema.TABLE_CONSTRAINTS 
WHERE 
    TABLE_SCHEMA = 'ghostnet_fishing'
ORDER BY TABLE_NAME, CONSTRAINT_TYPE;

-- =========================================
-- Teste Foreign Keys
-- =========================================

SELECT 'Testing foreign keys...' as Status;

SELECT 
    kcu.CONSTRAINT_NAME,
    kcu.TABLE_NAME,
    kcu.COLUMN_NAME,
    kcu.REFERENCED_TABLE_NAME,
    kcu.REFERENCED_COLUMN_NAME
FROM 
    information_schema.KEY_COLUMN_USAGE kcu
WHERE 
    kcu.TABLE_SCHEMA = 'ghostnet_fishing'
    AND kcu.REFERENCED_TABLE_NAME IS NOT NULL;

-- =========================================
-- Teste Daten-Integrität
-- =========================================

SELECT 'Testing data integrity...' as Status;

-- Teste gültige Status-Werte
SELECT 
    status,
    COUNT(*) as count,
    CASE 
        WHEN status IN ('GEMELDET', 'BERGUNG_BEVORSTEHEND', 'GEBORGEN', 'VERSCHOLLEN') 
        THEN 'VALID' 
        ELSE 'INVALID' 
    END as validity
FROM AbandonedNet 
GROUP BY status;

-- Teste Koordinaten-Bereiche
SELECT 
    'Coordinate validation' as test_type,
    COUNT(*) as total_records,
    COUNT(CASE WHEN latitude BETWEEN -90 AND 90 THEN 1 END) as valid_latitude,
    COUNT(CASE WHEN longitude BETWEEN -180 AND 180 THEN 1 END) as valid_longitude
FROM AbandonedNet;

-- Teste Größen-Werte
SELECT 
    'Size validation' as test_type,
    COUNT(*) as total_records,
    COUNT(CASE WHEN groesse >= 0 THEN 1 END) as valid_sizes,
    MIN(groesse) as min_size,
    MAX(groesse) as max_size,
    AVG(groesse) as avg_size
FROM AbandonedNet;

-- =========================================
-- Teste Beziehungen
-- =========================================

SELECT 'Testing relationships...' as Status;

-- Teste AbandonedNet -> Spotter Beziehung
SELECT 
    'Net-Spotter relationship' as test_type,
    COUNT(an.id) as total_nets,
    COUNT(an.meldendePerson_id) as nets_with_spotter,
    COUNT(s.id) as valid_spotter_references
FROM AbandonedNet an
LEFT JOIN Spotter s ON an.meldendePerson_id = s.id;

-- Prüfe verwaiste Referenzen
SELECT 
    'Orphaned references check' as test_type,
    COUNT(*) as orphaned_references
FROM AbandonedNet an
WHERE 
    an.meldendePerson_id IS NOT NULL 
    AND an.meldendePerson_id NOT IN (SELECT id FROM Spotter);

-- =========================================
-- Teste Views
-- =========================================

SELECT 'Testing views...' as Status;

-- Teste v_ghostnets_with_spotter View
SELECT COUNT(*) as records_in_view FROM v_ghostnets_with_spotter;

-- Teste v_status_statistics View
SELECT * FROM v_status_statistics;

-- =========================================
-- Performance Tests
-- =========================================

SELECT 'Testing performance...' as Status;

-- Teste Index-Nutzung für Status-Filter
EXPLAIN SELECT * FROM AbandonedNet WHERE status = 'GEMELDET';

-- Teste Index-Nutzung für Koordinaten
EXPLAIN SELECT * FROM AbandonedNet WHERE latitude BETWEEN 54.0 AND 55.0;

-- =========================================
-- Daten-Qualitäts-Tests
-- =========================================

SELECT 'Testing data quality...' as Status;

-- Teste auf NULL-Werte in kritischen Feldern
SELECT 
    'Critical NULL check' as test_type,
    COUNT(CASE WHEN latitude IS NULL THEN 1 END) as null_latitude,
    COUNT(CASE WHEN longitude IS NULL THEN 1 END) as null_longitude,
    COUNT(CASE WHEN status IS NULL THEN 1 END) as null_status
FROM AbandonedNet;

-- Teste auf doppelte Koordinaten (mögliche Duplikate)
SELECT 
    latitude,
    longitude,
    COUNT(*) as duplicate_count
FROM AbandonedNet
GROUP BY latitude, longitude
HAVING COUNT(*) > 1;

-- Teste Spotter ohne Namen und Telefonnummer (Anonyme)
SELECT 
    COUNT(*) as anonymous_spotters,
    COUNT(CASE WHEN name IS NOT NULL THEN 1 END) as named_spotters,
    COUNT(CASE WHEN telefonnummer IS NOT NULL THEN 1 END) as spotters_with_phone
FROM Spotter;

-- =========================================
-- Statistik-Tests
-- =========================================

SELECT 'Generating test statistics...' as Status;

-- Detaillierte Status-Statistiken
SELECT 
    status,
    COUNT(*) as count,
    AVG(groesse) as avg_size,
    MIN(created_at) as oldest_report,
    MAX(created_at) as newest_report,
    DATEDIFF(NOW(), MIN(created_at)) as days_since_first,
    DATEDIFF(NOW(), MAX(created_at)) as days_since_last
FROM AbandonedNet
GROUP BY status
ORDER BY count DESC;

-- Geografische Verteilung (vereinfacht)
SELECT 
    ROUND(latitude, 1) as lat_rounded,
    ROUND(longitude, 1) as lng_rounded,
    COUNT(*) as nets_in_area
FROM AbandonedNet
GROUP BY ROUND(latitude, 1), ROUND(longitude, 1)
HAVING COUNT(*) > 1
ORDER BY nets_in_area DESC;

-- =========================================
-- Abschluss-Bericht
-- =========================================

SELECT 'Database tests completed!' as Status;

-- Zusammenfassung
SELECT 
    'Summary' as report_section,
    (SELECT COUNT(*) FROM AbandonedNet) as total_nets,
    (SELECT COUNT(*) FROM Spotter) as total_spotters,
    (SELECT COUNT(DISTINCT status) FROM AbandonedNet) as unique_statuses,
    NOW() as test_completed_at;
