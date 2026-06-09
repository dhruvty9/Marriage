@echo off
REM ============================================================================
REM MARRIAGE APP - BACKEND QUICK START (backendraigar)
REM ============================================================================

echo.
echo ============================================================================
echo MARRIAGE APP - BACKEND QUICK START SCRIPT (RAIGAR BACKEND)
echo ============================================================================
echo.

cd /d "C:\Users\Dhruv Tyagi\AndroidStudioProjects\Marriage\backendraigar\backend"

echo [1/4] Clearing npm configuration...
call npm config set proxy ""
call npm config set https-proxy ""
call npm config set strict-ssl true
call npm config set registry https://registry.npmjs.org/
echo ✓ Done
echo.

echo [2/4] Cleaning npm cache...
call npm cache clean --force
echo ✓ Done
echo.

echo [3/4] Installing dependencies (if needed)...
if not exist "node_modules" (
    echo Installing packages...
    call npm install
) else (
    echo ✓ node_modules already exists
)
echo.

echo [4/4] Starting backend server...
echo.
echo ============================================================================
echo Server will start on http://localhost:3000
echo Keep this window open while testing the app
echo ============================================================================
echo.

call npm start

REM Server will run here - keep window open
pause
