# =========================================
# Ghost Net Fishing Database Setup Script (PowerShell)
# =========================================

Write-Host "Starting Ghost Net Fishing Database Setup..." -ForegroundColor Green

# Variablen definieren
$MYSQL_HOST = "localhost"
$MYSQL_PORT = "3306"
$MYSQL_ROOT_USER = "root"
$DB_NAME = "ghostnet_fishing"
$DB_USER = "ghostnet_app"
$DB_PASSWORD = "ghostnet_secure_2024!"

Write-Host "`nDatabase Configuration:" -ForegroundColor Yellow
Write-Host "  Host: $MYSQL_HOST"
Write-Host "  Port: $MYSQL_PORT"
Write-Host "  Database: $DB_NAME"
Write-Host "  App User: $DB_USER"
Write-Host ""

# Prüfe ob MySQL verfügbar ist
Write-Host "Checking MySQL connection..." -ForegroundColor Blue
try {
    $mysqlVersion = mysql --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "MySQL client found: $mysqlVersion" -ForegroundColor Green
    } else {
        throw "MySQL not found"
    }
} catch {
    Write-Host "ERROR: MySQL client not found in PATH!" -ForegroundColor Red
    Write-Host "Please install MySQL or add it to your PATH environment variable." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Pfad zum SQL-Skript
$scriptPath = Join-Path $PSScriptRoot "init_database.sql"

if (-not (Test-Path $scriptPath)) {
    Write-Host "ERROR: init_database.sql not found at: $scriptPath" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Erstelle Datenbank und führe Initialisierungsskript aus
Write-Host "Creating database and running initialization script..." -ForegroundColor Blue
Write-Host "Please enter your MySQL root password when prompted:" -ForegroundColor Yellow

try {
    $process = Start-Process -FilePath "mysql" -ArgumentList "-h", $MYSQL_HOST, "-P", $MYSQL_PORT, "-u", $MYSQL_ROOT_USER, "-p" -RedirectStandardInput $scriptPath -Wait -PassThru -NoNewWindow
    
    if ($process.ExitCode -eq 0) {
        Write-Host "`n=========================================" -ForegroundColor Green
        Write-Host "Database setup completed successfully!" -ForegroundColor Green
        Write-Host "=========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Database Details:" -ForegroundColor Yellow
        Write-Host "  Database Name: $DB_NAME"
        Write-Host "  App Username: $DB_USER"
        Write-Host "  App Password: $DB_PASSWORD"
        Write-Host ""
        Write-Host "You can now start your Ghost Net Fishing application." -ForegroundColor Green
        Write-Host ""
        Write-Host "To connect to the database manually:" -ForegroundColor Cyan
        Write-Host "  mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $DB_USER -p $DB_NAME"
        Write-Host ""
        
        # Teste die Verbindung mit dem neuen Benutzer
        Write-Host "Testing application database connection..." -ForegroundColor Blue
        $testConnection = "SELECT 'Connection successful!' as status;"
        $testResult = echo $testConnection | mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME 2>$null
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Application database connection test: PASSED" -ForegroundColor Green
        } else {
            Write-Host "Application database connection test: FAILED" -ForegroundColor Yellow
            Write-Host "You may need to verify the database user permissions." -ForegroundColor Yellow
        }
        
    } else {
        throw "MySQL process failed with exit code: $($process.ExitCode)"
    }
} catch {
    Write-Host "`n=========================================" -ForegroundColor Red
    Write-Host "ERROR: Database setup failed!" -ForegroundColor Red
    Write-Host "=========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "  1. MySQL server is running"
    Write-Host "  2. Root password is correct"
    Write-Host "  3. init_database.sql file exists"
    Write-Host "  4. MySQL has sufficient permissions"
    Write-Host ""
    Write-Host "Error details: $($_.Exception.Message)" -ForegroundColor Red
}

Read-Host "`nPress Enter to exit"
