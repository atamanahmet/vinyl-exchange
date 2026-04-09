@echo off
setlocal

REM -----------------------------
REM Start containers in background
REM -----------------------------
echo Starting Vinyl Exchange application...
docker compose up -d --build

REM -----------------------------
REM Wait for backend to be healthy
REM -----------------------------
echo Waiting for backend to be ready...
:backend_wait
for /f %%i in ('docker inspect -f "{{.State.Health.Status}}" vx-backend 2^>nul') do set status=%%i
if "%status%"=="healthy" goto frontend_wait
timeout /t 2 >nul
goto backend_wait

REM -----------------------------
REM Wait for frontend to respond
REM -----------------------------
:frontend_wait
echo Waiting for frontend to respond...
:frontend_loop
curl -s -o nul -w %%{http_code} http://localhost | findstr "200" >nul
if errorlevel 1 (
    timeout /t 2 >nul
    goto frontend_loop
)

REM -----------------------------
REM Everything is ready
REM -----------------------------
echo.
echo All services are ready
echo Frontend: http://localhost
echo Backend:  http://localhost:8080
echo.


