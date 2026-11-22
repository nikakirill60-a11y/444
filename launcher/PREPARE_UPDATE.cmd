@echo off
chcp 65001 >nul
setlocal
if "%~1"=="" (
  echo Перетащи папку .minecraft на этот файл. Нужен Python 3.12 или новее.
  pause
  exit /b 1
)
set /p "VERSION=Версия dev (латиница, цифры, точка, дефис): "
py -3 "%~dp0file_release.py" prepare "%~1" --out "%~dp0prepared" --version "%VERSION%"
if errorlevel 1 (
  echo Подготовка не завершена. Ничего не опубликовано.
) else (
  echo Файлы подготовлены в publisher\prepared. Загрузка и публикация не выполнялись.
)
pause
