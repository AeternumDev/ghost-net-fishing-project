# Ghost Net Fishing - Datenbank Setup Anleitung

## Übersicht

Diese Anleitung beschreibt die Einrichtung der MySQL-Datenbank für die Ghost Net Fishing Anwendung. Das System verwendet JPA mit Hibernate als Persistenz-Provider und MySQL als Datenbank.

## Voraussetzungen

- MySQL Server 8.0 oder höher
- Java 17 oder höher
- Maven 3.6 oder höher

## Schnell-Setup

### Option 1: Automatisches Setup (PowerShell)

```powershell
cd database
.\setup_database.ps1
```

### Option 2: Automatisches Setup (Batch)

```cmd
cd database
setup_database.bat
```

### Option 3: Manuelles Setup

```sql
mysql -u root -p < init_database.sql
```

## Datenbankschema

### Tabellen

#### 1. Spotter (Meldende Personen)
- `id` (BIGINT, Primary Key, Auto Increment)
- `name` (VARCHAR(255), NULL erlaubt)
- `telefonnummer` (VARCHAR(255), NULL erlaubt)
- `created_at` (TIMESTAMP, Default: CURRENT_TIMESTAMP)
- `updated_at` (TIMESTAMP, Default: CURRENT_TIMESTAMP ON UPDATE)

#### 2. AbandonedNet (Geisternetze)
- `id` (BIGINT, Primary Key, Auto Increment)
- `status` (ENUM: 'GEMELDET', 'BERGUNG_BEVORSTEHEND', 'GEBORGEN', 'VERSCHOLLEN')
- `groesse` (INT, Größe in Quadratmetern)
- `beschreibung` (VARCHAR(500), Beschreibung des Fundorts)
- `latitude` (DECIMAL(10,6), Breitengrad)
- `longitude` (DECIMAL(10,6), Längengrad)
- `meldendePerson_id` (BIGINT, Foreign Key zu Spotter)
- `created_at` (TIMESTAMP, Meldezeitpunkt)
- `updated_at` (TIMESTAMP, Letzte Aktualisierung)

### Constraints und Indizes

- **Foreign Key**: `meldendePerson_id` → `Spotter.id`
- **Check Constraints**: 
  - Latitude: -90 bis +90
  - Longitude: -180 bis +180
  - Größe: >= 0
- **Indizes** für Performance:
  - Status-Filter
  - Geografische Koordinaten
  - Zeitbasierte Abfragen

## Konfiguration

### Umgebungen

Die Anwendung unterstützt verschiedene Umgebungen:

- **Development**: `ghostnet_fishing_dev`
- **Production**: `ghostnet_fishing` 
- **Test**: `ghostnet_fishing_test`

### Persistence Units

#### Standard (persistence.xml)
```xml
<persistence-unit name="ghostnet_fishing">
    <!-- Standard Konfiguration -->
</persistence-unit>
```

#### Profile-basiert (persistence-profiles.xml)
- `ghostnet_fishing_dev` - Development mit create-drop
- `ghostnet_fishing_prod` - Production mit validate

### Datenbankbenutzer

- **Root User**: Nur für Setup
- **App User**: `ghostnet_app` - Dedizierter Anwendungsbenutzer
- **Passwort**: `ghostnet_secure_2024!` (in Produktion ändern!)

## Funktionale Anforderungen

Das Datenbankschema unterstützt folgende Anwendungsfunktionen:

### 1. Geisternetz-Meldung
- **Controller**: `MarineNetController.meldeGeisternetz()`
- **DAO**: `AbandonedNetDAO.save()`
- **Tabellen**: `AbandonedNet`, `Spotter`

### 2. Geisternetz-Verwaltung
- **CRUD-Operationen**: Create, Read, Update, Delete
- **Status-Updates**: Bergung-Workflow
- **Filterung**: Nach Status, Zeitraum, Koordinaten

### 3. Spotter-Verwaltung
- **Anonyme Meldungen**: NULL-Werte für Name/Telefon
- **Kontakt-Informationen**: Optional für Rückfragen

### 4. Geografische Funktionen
- **Koordinaten-Speicherung**: Präzise Positionsangaben
- **Bereichs-Abfragen**: Geografische Filterung (zukünftig)

### 5. Reporting und Statistiken
- **Status-Verteilung**: Anzahl pro Status
- **Zeitanalysen**: Meldungen über Zeit
- **Größen-Statistiken**: Durchschnittswerte

## REST API Endpunkte

### Database Health Check
```
GET /api/database/health
```
**Response:**
```json
{
  "healthy": true,
  "jpaInitialized": true,
  "connectionTest": true,
  "timestamp": 1653123456789
}
```

### Database Statistics
```
GET /api/database/statistics
```
**Response:**
```json
{
  "totalNets": 45,
  "totalSpotters": 23,
  "netsByStatus": {
    "GEMELDET": 12,
    "BERGUNG_BEVORSTEHEND": 5,
    "GEBORGEN": 25,
    "VERSCHOLLEN": 3
  },
  "averageNetSize": 18.7
}
```

## Troubleshooting

### Häufige Probleme

#### 1. Verbindungsfehler
```
Error: Access denied for user 'ghostnet_app'@'localhost'
```
**Lösung**: Setup-Skript erneut ausführen oder Benutzerrechte prüfen

#### 2. Schema-Validierung fehlgeschlagen
```
Error: Table 'AbandonedNet' doesn't exist
```
**Lösung**: `init_database.sql` ausführen

#### 3. JPA Initialization Failed
```
Error: No suitable driver found
```
**Lösung**: MySQL JDBC Driver in Classpath prüfen

### Debug-Hilfen

#### 1. Verbindung testen
```bash
mysql -h localhost -P 3306 -u ghostnet_app -p ghostnet_fishing
```

#### 2. Tabellen prüfen
```sql
SHOW TABLES;
DESCRIBE AbandonedNet;
DESCRIBE Spotter;
```

#### 3. Beispieldaten prüfen
```sql
SELECT COUNT(*) FROM AbandonedNet;
SELECT COUNT(*) FROM Spotter;
SELECT * FROM v_status_statistics;
```

## Performance-Optimierung

### Indizes
Das Schema enthält optimierte Indizes für:
- Status-basierte Filterung
- Geografische Abfragen
- Zeitbasierte Sortierung

### Connection Pooling
- Development: 5-10 Verbindungen
- Production: 20+ Verbindungen

### Query-Optimierung
- Verwendung von Views für komplexe Abfragen
- Batch-Operations für Bulk-Updates

## Sicherheit

### Datenbankbenutzer
- Minimale Rechte (nur SELECT, INSERT, UPDATE, DELETE)
- Kein DDL-Zugriff für Anwendungsbenutzer
- Separate Benutzer für verschiedene Umgebungen

### Datenvalidierung
- Check Constraints für Koordinaten
- Enum-Typen für Status-Werte
- NOT NULL Constraints für kritische Felder

## Wartung

### Backup
```bash
mysqldump -u root -p ghostnet_fishing > backup_$(date +%Y%m%d).sql
```

### Restore
```bash
mysql -u root -p ghostnet_fishing < backup_20240524.sql
```

### Cleanup
Die `DatabaseUtil` Klasse bietet Cleanup-Funktionen für:
- Verwaiste Datensätze
- Temporäre Daten
- Log-Bereinigung

## Weiterentwicklung

### Geplante Erweiterungen
- Geografische Indizes für Bereichs-Abfragen
- Audit-Tabellen für Änderungsverfolgung
- Partitionierung für große Datenmengen
- Read-Replicas für bessere Performance
