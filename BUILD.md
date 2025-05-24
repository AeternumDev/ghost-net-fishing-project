# Ghost Net Fishing - Build und Deploy Anleitung

## Datenbank Setup

### Automatisches Setup mit Maven

#### Development Environment
```bash
mvn clean compile -Pdev
```
Führt automatisch das PowerShell Database-Setup aus.

#### Test Environment  
```bash
mvn clean test -Ptest
```
Erstellt eine separate Test-Datenbank mit Testdaten.

#### Production Environment
```bash
mvn clean package -Pprod
```
Validiert nur das Schema (keine automatische Datenbank-Erstellung).

### Manuelles Setup

#### PowerShell (empfohlen für Windows)
```powershell
cd database
.\setup_database.ps1
```

#### Batch Script
```cmd
cd database
setup_database.bat
```

#### Direktes SQL
```bash
mysql -u root -p < database/init_database.sql
```

## Maven Commands

### Datenbank-spezifische Commands

```bash
# Erstelle nur die Datenbank
mvn sql:execute@create-database

# Führe Database Tests aus
mysql -u root -p < database/test_database.sql

# Baue für Development
mvn clean package -Pdev

# Baue für Production
mvn clean package -Pprod

# Führe Tests mit Test-DB aus
mvn clean test -Ptest
```

### Standard Build Commands

```bash
# Standard Build
mvn clean package

# Build mit Tests
mvn clean test

# Build ohne Tests
mvn clean package -DskipTests

# Deploy zu Tomcat
mvn tomcat7:deploy

# Redeploy zu Tomcat
mvn tomcat7:redeploy
```

## Umgebungsvariablen

### Development
```bash
export APP_ENVIRONMENT=dev
export MYSQL_ROOT_PASSWORD=1234
```

### Production
```bash
export APP_ENVIRONMENT=prod
export MYSQL_ROOT_PASSWORD=secure_password
```

## Datenbank URLs per Environment

- **Development**: `ghostnet_fishing_dev`
- **Test**: `ghostnet_fishing_test`  
- **Production**: `ghostnet_fishing`

## Troubleshooting

### MySQL Verbindungsprobleme
1. Prüfe ob MySQL läuft: `net start mysql80`
2. Teste Verbindung: `mysql -u root -p`
3. Prüfe Firewall-Einstellungen

### Build-Probleme
1. Prüfe Java Version: `java -version` (min. 17)
2. Prüfe Maven: `mvn -version` (min. 3.6)
3. Cleane Target: `mvn clean`

### Database Setup Probleme  
1. Führe Test-Script aus: `mysql -u root -p < database/test_database.sql`
2. Prüfe Logs in Console
3. Prüfe Database Health: `GET /api/database/health`
