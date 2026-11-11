@echo off
cd /d "%~dp0"
py -3 -m metro_spawner
if errorlevel 1 (
  echo.
  echo Requires Python 3 with tkinter installed on Windows.
  pause
)
