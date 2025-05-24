@echo off
REM =========================================
REM Ghost Net Fishing Database Setup Script
REM =========================================

echo "Starting Ghost Net Fishing Database Setup..."

REM Variablen definieren
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_ROOT_USER=root
set DB_NAME=ghostnet_fishing
set DB_USER=ghostnet_app
set DB_PASSWORD=ghostnet_secure_2024!

echo "Database Configuration:"
echo "  Host: %MYSQL_HOST%"
echo "  Port: %MYSQL_PORT%"
echo "  Database: %DB_NAME%"
echo "  App User: %DB_USER%"
echo ""

REM Prüfe ob MySQL verfügbar ist
echo "Checking MySQL connection..."
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo "ERROR: MySQL client not found in PATH!"
    echo "Please install MySQL or add it to your PATH environment variable."
    pause
    exit /b 1
)

REM Erstelle Datenbank und führe Initialisierungsskript aus
echo "Creating database and running initialization script..."
echo "Please enter your MySQL root password when prompted:"

mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_ROOT_USER% -p < "%~dp0init_database.sql"

if %errorlevel% equ 0 (
    echo ""
    echo "========================================="
    echo "Database setup completed successfully!"
    echo "========================================="
    echo ""
    echo "Database Details:"
    echo "  Database Name: %DB_NAME%"
    echo "  App Username: %DB_USER%"
    echo "  App Password: %DB_PASSWORD%"
    echo ""
    echo "You can now start your Ghost Net Fishing application."
    echo ""
    echo "To connect to the database manually:"
    echo "  mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %DB_USER% -p %DB_NAME%"
    echo ""
) else (
    echo ""
    echo "========================================="
    echo "ERROR: Database setup failed!"
    echo "========================================="
    echo ""
    echo "Please check:"
    echo "  1. MySQL server is running"
    echo "  2. Root password is correct"
    echo "  3. init_database.sql file exists"
    echo ""
)

pause
