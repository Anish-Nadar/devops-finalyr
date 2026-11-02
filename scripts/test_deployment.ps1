# Deployment verification script
Write-Host "Starting Marathon Registration Portal on Port 8080..."
$proc = Start-Process -FilePath "java" -ArgumentList "-jar", "target/marathon-portal-1.0.0-SNAPSHOT.jar", "--server.port=8080" -PassThru

Write-Host "Waiting 8 seconds for Embedded Tomcat to initialize..."
Start-Sleep -Seconds 8

try {
    Write-Host "---------------------------------------------------------"
    Write-Host "[PROBE 1] Querying /health endpoint..."
    $health = Invoke-RestMethod -Uri "http://localhost:8080/health"
    Write-Host "HTTP Status: 200 OK"
    $health | ConvertTo-Json -Depth 4

    Write-Host "---------------------------------------------------------"
    Write-Host "[PROBE 2] Querying /api/status endpoint for seeded runner..."
    $status = Invoke-RestMethod -Uri "http://localhost:8080/api/status?query=REG-FM-101A"
    Write-Host "HTTP Status: 200 OK"
    $status | ConvertTo-Json -Depth 4

    Write-Host "---------------------------------------------------------"
    Write-Host "[PROBE 3] Probing Landing Page /..."
    $response = Invoke-WebRequest -Uri "http://localhost:8080/" -UseBasicParsing
    Write-Host "Landing Page HTTP Status Code:" $response.StatusCode
    Write-Host "Page Content Length:" $response.RawContentLength "bytes"
}
finally {
    Write-Host "---------------------------------------------------------"
    Write-Host "Stopping server process (PID: $($proc.Id))..."
    Stop-Process -Id $proc.Id -Force
    Write-Host "Server stopped successfully."
}
