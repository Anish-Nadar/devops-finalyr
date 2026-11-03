# PowerShell script to reset Jenkins password for Anish201205 to 'Anish@05'
# Requires Administrator privileges

$newHash = "#jbcrypt:`$2a`$10`$kgbkTvEl.G5zceIIk/jxYuPHrJ0p4y/Uv69383Pb68L5SkeUr8Lpa"
$userConfigFile = (Get-ChildItem -Path "C:\ProgramData\Jenkins\.jenkins\users\anish201205_*\config.xml").FullName

if (-not $userConfigFile) {
    Write-Host "ERROR: Could not locate user config.xml in Jenkins home." -ForegroundColor Red
    exit 1
}

Write-Host "Found user config: $userConfigFile"
Write-Host "Stopping Jenkins service..."
Stop-Service jenkins -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2

Write-Host "Updating password hash for Anish201205 to 'Anish@05'..."
$content = Get-Content -Path $userConfigFile -Raw
$updated = $content -replace '<passwordHash>[^<]+</passwordHash>', "<passwordHash>$newHash</passwordHash>"
Set-Content -Path $userConfigFile -Value $updated -Force

Write-Host "Restarting Jenkins service..."
Start-Service jenkins -ErrorAction SilentlyContinue
Start-Sleep -Seconds 5

Write-Host "`nSUCCESS! Password for 'Anish201205' has been reset to: Anish@05" -ForegroundColor Green
Write-Host "You can now log in at http://localhost:8081" -ForegroundColor Cyan

