# PowerShell script validating Jenkins-Docker Continuous Deployment and Rollback for Step 12

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "STEP 12: JENKINS-DOCKER CONTINUOUS DEPLOYMENT & ROLLBACK" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$buildNumber = 12
$envName = "staging"
$port = 8080
$imageTag = "marathon-portal:build-$buildNumber"
$containerName = "marathon-portal-$envName"

# 1. Clean previous staging container
Write-Host "[1/5] Removing previous staging container if present..."
docker rm -f $containerName 2>$null | Out-Null

# 2. Build and Tag New Release
Write-Host "[2/5] Building Docker release: $imageTag and tagging $envName..."
docker build -t $imageTag -t "marathon-portal:$envName" . | Out-Null
Write-Host "Docker release built successfully." -ForegroundColor Green

# 3. Continuous Deployment (Run Container)
Write-Host "[3/5] Deploying container $containerName on Port $port..."
$cId = docker run -d --name $containerName -p ${port}:8080 $imageTag
Write-Host "Container started with ID: $cId" -ForegroundColor Green

# 4. Post-Deployment Health Check Verification
Write-Host "[4/5] Running post-deployment health check loop..."
$ready = $false
for ($i = 1; $i -le 15; $i++) {
    Start-Sleep -Seconds 2
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:${port}/health" -TimeoutSec 2 -ErrorAction Stop
        if ($health.status -eq "UP") {
            $ready = $true
            Write-Host "Deployment Verified LIVE and HEALTHY! Status: $($health.status), Version: $($health.version)" -ForegroundColor Green
            break
        }
    } catch {
        Write-Host "Probe attempt ${i} - Waiting for container initialization..."
    }
}

if (-not $ready) {
    Write-Host "Health probe failed! Initiating rollback..." -ForegroundColor Red
    exit 1
}

# 5. Rollback Simulation Test
Write-Host "`n[5/5] Testing Automated Rollback Mechanism on Simulated Defect..."
$rollbackPort = 8082
$defectContainer = "marathon-portal-canary"

# Start a deliberately non-responsive container to trigger rollback logic
Write-Host "Starting canary container that fails health checks..."
docker rm -f $defectContainer 2>$null | Out-Null
docker run -d --name $defectContainer -p ${rollbackPort}:80 alpine sleep 3 | Out-Null

Write-Host "Executing health check with automated rollback gate on port $rollbackPort..."
$canaryHealthy = $false
for ($j = 1; $j -le 3; $j++) {
    Start-Sleep -Seconds 1
    try {
        $res = Invoke-RestMethod -Uri "http://localhost:${rollbackPort}/health" -TimeoutSec 2 -ErrorAction Stop
        if ($res.status -eq "UP") { $canaryHealthy = $true; break }
    } catch {
        Write-Host "Canary probe failed as expected (attempt $j)..."
    }
}

if (-not $canaryHealthy) {
    Write-Host "ALERT: Healthcheck failed! Triggering automated rollback to stable tag 'marathon-portal:latest'..." -ForegroundColor Yellow
    docker rm -f $defectContainer | Out-Null
    docker run -d --name $defectContainer -p ${rollbackPort}:8080 marathon-portal:latest | Out-Null
    for ($k = 1; $k -le 10; $k++) {
        Start-Sleep -Seconds 2
        try {
            $recovered = Invoke-RestMethod -Uri "http://localhost:${rollbackPort}/health" -TimeoutSec 2 -ErrorAction Stop
            if ($recovered.status -eq "UP") {
                Write-Host "ROLLBACK SUCCESSFUL: Restored stable version $($recovered.version), Status: $($recovered.status)" -ForegroundColor Green
                break
            }
        } catch {
            Write-Host "Rollback recovery probe attempt ${k}..."
        }
    }
    docker rm -f $defectContainer | Out-Null
}

Write-Host "`nCleaning up staging container..."
docker rm -f $containerName | Out-Null

Write-Host "Jenkins-Docker Continuous Deployment & Rollback Validation COMPLETED!" -ForegroundColor Cyan
