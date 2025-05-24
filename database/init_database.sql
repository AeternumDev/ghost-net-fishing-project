-- =========================================
-- Ghost Net Fishing Database Initialization
-- =========================================

-- Datenbank erstellen (falls sie nicht existiert)
CREATE DATABASE IF NOT EXISTS ghostnet_fishing 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Datenbank verwenden
USE ghostnet_fishing;

-- =========================================
-- Tabellen erstellen
-- =========================================

-- Spotter Tabelle (meldende Personen)
CREATE TABLE IF NOT EXISTS Spotter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NULL,
    telefonnummer VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- AbandonedNet Tabelle (Geisternetze)
CREATE TABLE IF NOT EXISTS AbandonedNet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status ENUM('GEMELDET', 'BERGUNG_BEVORSTEHEND', 'GEBORGEN', 'VERSCHOLLEN') NOT NULL DEFAULT 'GEMELDET',
    groesse INT NOT NULL DEFAULT 0,
    beschreibung VARCHAR(500) NULL,
    latitude DECIMAL(10,6) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,
    meldendePerson_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key Constraint
    CONSTRAINT fk_abandoned_net_spotter 
        FOREIGN KEY (meldendePerson_id) 
        REFERENCES Spotter(id) 
        ON DELETE SET NULL 
        ON UPDATE CASCADE,
        
    -- Constraints für Koordinaten
    CONSTRAINT chk_latitude CHECK (latitude >= -90 AND latitude <= 90),
    CONSTRAINT chk_longitude CHECK (longitude >= -180 AND longitude <= 180),
    CONSTRAINT chk_groesse CHECK (groesse >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================
-- Indizes für bessere Performance
-- =========================================

-- Index für Status-Abfragen (häufig verwendet für Filterung)
CREATE INDEX idx_abandoned_net_status ON AbandonedNet(status);

-- Index für Koordinaten (für geografische Abfragen)
CREATE INDEX idx_abandoned_net_location ON AbandonedNet(latitude, longitude);

-- Index für Spotter-Beziehung
CREATE INDEX idx_abandoned_net_spotter ON AbandonedNet(meldendePerson_id);

-- Index für zeitbasierte Abfragen
CREATE INDEX idx_abandoned_net_created ON AbandonedNet(created_at);
CREATE INDEX idx_spotter_created ON Spotter(created_at);

-- =========================================
-- Beispiel-Daten einfügen (optional)
-- =========================================

-- Beispiel Spotter
INSERT INTO Spotter (name, telefonnummer) VALUES 
('Max Mustermann', '+49 123 456789'),
('Anna Schmidt', '+49 987 654321'),
('Fischer Klaus', '+49 555 123456'),
(NULL, NULL); -- Anonymer Spotter

-- Beispiel Geisternetze
INSERT INTO AbandonedNet (status, groesse, beschreibung, latitude, longitude, meldendePerson_id) VALUES 
('GEMELDET', 25, 'Großes Fischernetz am Meeresgrund, ca. 5x5 Meter', 54.323844, 10.122765, 1),
('BERGUNG_BEVORSTEHEND', 15, 'Kleineres Netz an Felsen verfangen', 54.311233, 10.145678, 2),
('GEBORGEN', 30, 'Sehr großes Netz mit Bojen', 54.345789, 10.098765, 3),
('GEMELDET', 10, 'Netz in flachem Wasser entdeckt', 54.367891, 10.087654, 4),
('VERSCHOLLEN', 20, 'Netz war markiert, aber nicht mehr auffindbar', 54.289123, 10.156789, 1);

-- =========================================
-- Benutzer für die Anwendung erstellen
-- =========================================

-- Erstelle dedizierten Datenbankbenutzer für die Anwendung
-- (Sichere Alternative zu root)
CREATE USER IF NOT EXISTS 'ghostnet_app'@'localhost' IDENTIFIED BY 'ghostnet_secure_2024!';

-- Gewähre nur notwendige Rechte auf die Datenbank
GRANT SELECT, INSERT, UPDATE, DELETE ON ghostnet_fishing.* TO 'ghostnet_app'@'localhost';

-- Rechte anwenden
FLUSH PRIVILEGES;

-- =========================================
-- Nützliche Views für Reports
-- =========================================

-- View für Geisternetze mit Spotter-Information
CREATE OR REPLACE VIEW v_ghostnets_with_spotter AS
SELECT 
    an.id,
    an.status,
    an.groesse,
    an.beschreibung,
    an.latitude,
    an.longitude,
    an.created_at as gemeldet_am,
    an.updated_at as letztes_update,
    COALESCE(s.name, 'Anonym') as spotter_name,
    s.telefonnummer as spotter_telefon
FROM AbandonedNet an
LEFT JOIN Spotter s ON an.meldendePerson_id = s.id
ORDER BY an.created_at DESC;

-- View für Status-Statistiken
CREATE OR REPLACE VIEW v_status_statistics AS
SELECT 
    status,
    COUNT(*) as anzahl,
    AVG(groesse) as durchschnittliche_groesse,
    MIN(created_at) as aelteste_meldung,
    MAX(created_at) as neueste_meldung
FROM AbandonedNet
GROUP BY status;

-- =========================================
-- Ausgabe der Ergebnisse
-- =========================================

SELECT 'Datenbank erfolgreich initialisiert!' as Status;
SELECT COUNT(*) as 'Anzahl Spotter' FROM Spotter;
SELECT COUNT(*) as 'Anzahl Geisternetze' FROM AbandonedNet;

-- Zeige Status-Verteilung
SELECT * FROM v_status_statistics;
