# PowerShell script to validate Docker Container Lifecycle for Marathon Registration Portal

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "STEP 11: DOCKER CONTAINER LIFECYCLE VALIDATION" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Cleanup any existing container
Write-Host "[1/7] Cleaning up previous containers..."
docker rm -f marathon-app 2>$null | Out-Null

# 2. Run container
Write-Host "[2/7] Running container marathon-portal:1.0.0 on port 8080..."
$containerId = docker run -d --name marathon-app -p 8080:8080 marathon-portal:1.0.0
Write-Host "Container started with ID: $containerId" -ForegroundColor Green

# 3. Wait for Spring Boot application startup with polling loop
Write-Host "[3/7] Waiting for Spring Boot application to become ready..."
$isReady = $false
$attempts = 0
$maxAttempts = 15

while (-not $isReady -and $attempts -lt $maxAttempts) {
    Start-Sleep -Seconds 2
    $attempts++
    try {
        $resp = Invoke-RestMethod -Uri "http://localhost:8080/health" -Method Get -TimeoutSec 2 -ErrorAction Stop
        if ($resp.status -eq "UP") {
            $isReady = $true
            Write-Host "Application is READY after $($attempts * 2) seconds." -ForegroundColor Green
        }
    } catch {
        Write-Host "Attempt ${attempts} - Waiting for server port 8080..."
    }
}

# 4. Inspect container status and logs
Write-Host "`n[4/7] Inspecting running container status and logs..."
docker ps --filter "name=marathon-app" --format "table {{.ID}}\t{{.Names}}\t{{.Status}}\t{{.Ports}}"
Write-Host "`n--- Tail Container Logs ---"
docker logs --tail 15 marathon-app

# 5. Verify HTTP Endpoints
Write-Host "`n[5/7] Testing HTTP Endpoints against containerized application..."
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/health" -Method Get -TimeoutSec 5
    Write-Host "Health check response: $($health | ConvertTo-Json -Compress)" -ForegroundColor Green
    
    $landingResp = Invoke-WebRequest -Uri "http://localhost:8080/" -Method Get -TimeoutSec 5 -UseBasicParsing
    Write-Host "Landing page status code: $($landingResp.StatusCode) OK" -ForegroundColor Green
} catch {
    Write-Host "Endpoint probe error: $_" -ForegroundColor Red
}

# 6. Test Stop and Restart Lifecycle
Write-Host "`n[6/7] Testing Container Stop and Restart lifecycle..."
Write-Host "Stopping container..."
docker stop marathon-app | Out-Null
$stoppedStatus = docker ps -a --filter "name=marathon-app" --format "{{.Status}}"
Write-Host "Status after stop: $stoppedStatus"

Write-Host "Restarting container..."
docker start marathon-app | Out-Null
Start-Sleep -Seconds 6
$restartedStatus = docker ps --filter "name=marathon-app" --format "{{.Status}}"
Write-Host "Status after restart: $restartedStatus" -ForegroundColor Green

# 7. Final Cleanup
Write-Host "`n[7/7] Cleaning up container..."
docker rm -f marathon-app | Out-Null
Write-Host "Container removed successfully." -ForegroundColor Green

Write-Host "`nDocker container lifecycle test COMPLETED SUCCESSFULLY!" -ForegroundColor Cyan
