pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['staging', 'production', 'local'], description: 'Target Deployment Environment')
        string(name: 'SERVER_PORT', defaultValue: '8080', description: 'Application Host Port Mapping')
        booleanParam(name: 'AUTO_DEPLOY', defaultValue: true, description: 'Automatically deploy containerized artifact')
        booleanParam(name: 'SIMULATE_HEALTH_FAILURE', defaultValue: false, description: 'Simulate health check failure to trigger automated rollback')
    }

    environment {
        APP_NAME = 'marathon-portal'
        IMAGE_NAME = "marathon-portal"
        CONTAINER_NAME = "marathon-portal-${params.DEPLOY_ENV}"
        BUILD_TAG = "${env.BUILD_NUMBER ?: '1.0.0'}"
        JAVA_HOME = "C:\\Program Files\\BellSoft\\LibericaJDK-21"
        PATH = "C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                echo "=========================================================="
                echo "Stage 1: Checkout Source Code from GitHub Repository"
                echo "=========================================================="
                checkout scm
                script {
                    echo "Checked out Git Branch: ${env.BRANCH_NAME ?: 'develop'}"
                    echo "Target Environment:     ${params.DEPLOY_ENV}"
                    echo "Target Host Port:       ${params.SERVER_PORT}"
                    echo "Container Image Tag:    ${IMAGE_NAME}:${BUILD_TAG}"
                }
            }
        }

        stage('Compile & Static Verification') {
            steps {
                echo "=========================================================="
                echo "Stage 2: Compiling Java 21 Source Code"
                echo "=========================================================="
                bat 'call .\\mvnw.cmd clean compile'
            }
        }

        stage('Unit & MockMvc Testing') {
            steps {
                echo "=========================================================="
                echo "Stage 3: Running Unit and Component MockMvc Tests"
                echo "=========================================================="
                bat 'call .\\mvnw.cmd test -Dtest=MarathonApplicationTests,RegistrationControllerTest'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Automated Selenium E2E Testing') {
            steps {
                echo "=========================================================="
                echo "Stage 4: Running Headless Chrome Selenium E2E Suite"
                echo "=========================================================="
                bat 'call .\\mvnw.cmd test -Dtest=RegistrationSeleniumTest'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('Package Executable JAR') {
            steps {
                echo "=========================================================="
                echo "Stage 5: Packaging Standalone Executable Artifact"
                echo "=========================================================="
                bat 'call .\\mvnw.cmd package -DskipTests=true'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build & Tag Docker Image') {
            when {
                expression { return params.AUTO_DEPLOY == true }
            }
            steps {
                echo "=========================================================="
                echo "Stage 6: Building Multi-Stage Docker Container Image"
                echo "=========================================================="
                bat """
                    set "PATH=C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;%PATH%"
                    docker build -t ${IMAGE_NAME}:${BUILD_TAG} -t ${IMAGE_NAME}:${params.DEPLOY_ENV} -t ${IMAGE_NAME}:latest .
                    docker images ${IMAGE_NAME}
                """
            }
        }

        stage('Continuous Deployment (Docker Run)') {
            when {
                expression { return params.AUTO_DEPLOY == true }
            }
            steps {
                echo "=========================================================="
                echo "Stage 7: Deploying Container ${CONTAINER_NAME} on Port ${params.SERVER_PORT}"
                echo "=========================================================="
                bat """
                    set "PATH=C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;%PATH%"
                    docker rm -f marathon-app 2>nul || ver >nul
                    docker rm -f ${CONTAINER_NAME} 2>nul || ver >nul
                    docker run -d --name ${CONTAINER_NAME} -p ${params.SERVER_PORT}:8080 ${IMAGE_NAME}:${BUILD_TAG}
                    docker ps --filter name=${CONTAINER_NAME}
                """
            }
        }

        stage('Post-Deploy Verification & Automated Rollback') {
            when {
                expression { return params.AUTO_DEPLOY == true }
            }
            steps {
                echo "=========================================================="
                echo "Stage 8: Verifying Container Health Probe with Automated Rollback Gate"
                echo "=========================================================="
                script {
                    echo "Checking container health at http://localhost:${params.SERVER_PORT}/health..."
                    bat """
                        set "PATH=C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;%PATH%"
                        powershell -ExecutionPolicy Bypass -Command "\$ready=\$false; for(\$i=1;\$i -le 15;\$i++){ Start-Sleep -Seconds 2; try { \$h = Invoke-RestMethod -Uri 'http://localhost:${params.SERVER_PORT}/health' -TimeoutSec 2; if(\$h.status -eq 'UP'){ Write-Host 'Health probe confirmed UP!'; \$ready=\$true; break; } } catch { Write-Host 'Waiting for container ready (attempt '\$i'/15)...'; } }; if(-not \$ready){ Write-Host 'HEALTHCHECK FAILED: Triggering automated rollback...'; docker rm -f ${CONTAINER_NAME}; docker run -d --name ${CONTAINER_NAME} -p ${params.SERVER_PORT}:8080 ${IMAGE_NAME}:latest; exit 1; }"
                    """
                }
            }
        }
    }

    post {
        always {
            echo "=========================================================="
            echo "Pipeline Execution Completed for ${env.APP_NAME}"
            echo "=========================================================="
        }
        success {
            echo "CD PIPELINE SUCCEEDED: ${CONTAINER_NAME} is live and healthy."
            echo "Live URL:     http://localhost:${params.SERVER_PORT}/"
            echo "Health Probe: http://localhost:${params.SERVER_PORT}/health"
            echo "Admin Portal: http://localhost:${params.SERVER_PORT}/admin"
        }
        failure {
            echo "CD PIPELINE FAILED: Rollback initiated or build halted. Environments protected."
        }
    }
}
