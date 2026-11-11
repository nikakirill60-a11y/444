# Read-only Metro: Last Light executable diagnostic. Windows PowerShell 5.1+.
param([string]$GamePath)
$ErrorActionPreference = 'Stop'

function Describe-Exe([string]$file) {
    $exe = Get-Item -LiteralPath $file -ErrorAction Stop
    if ($exe.Extension -ne '.exe') { throw 'Specify a .exe game file or its folder.' }
    @(
        "Executable: $($exe.FullName)",
        "File version: $($exe.VersionInfo.FileVersion)",
        "Product name: $($exe.VersionInfo.ProductName)",
        "Product version: $($exe.VersionInfo.ProductVersion)",
        "Size (bytes): $($exe.Length)",
        "SHA-256: $((Get-FileHash -LiteralPath $exe.FullName -Algorithm SHA256).Hash)"
    )
}

function Find-Executables([string]$path) {
    if (-not (Test-Path -LiteralPath $path)) { throw "Path does not exist: $path" }
    $item = Get-Item -LiteralPath $path
    if (-not $item.PSIsContainer) { return @($item.FullName) }
    $executables = @(Get-ChildItem -LiteralPath $item.FullName -File -Filter '*.exe' |
        Where-Object { $_.Name -match '(?i)metro|last.light' })
    if ($executables.Count -eq 0) { throw "No Metro .exe found in: $path (choose the game .exe directly)" }
    return @($executables | ForEach-Object { $_.FullName })
}

$lines = New-Object 'System.Collections.Generic.List[string]'
$lines.Add('Metro: Last Light - read-only diagnostic report')
$lines.Add('Edition is not guaranteed by filename; the executable hash identifies the build.')
$lines.Add('')
try {
    if (-not $GamePath) {
        Write-Host 'Enter the full path to the game .exe or its folder (Steam and non-Steam versions):'
        $GamePath = Read-Host 'Game path'
    }
    $GamePath = $GamePath.Trim().Trim([char]34)
    foreach ($file in @(Find-Executables $GamePath)) {
        foreach ($line in (Describe-Exe $file)) { $lines.Add($line) }
        $lines.Add('')
    }
} catch {
    Write-Error $_.Exception.Message
    exit 1
}
$report = Join-Path $PSScriptRoot 'metro-diagnostic.txt'
[System.IO.File]::WriteAllLines($report, [string[]]$lines, [System.Text.Encoding]::UTF8)
$lines | ForEach-Object { Write-Host $_ }
Write-Host "Report saved: $report"
Write-Host 'Before sharing, remove any personal details from the executable path.'
