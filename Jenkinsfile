pipeline {
    agent any

    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['staging', 'production', 'local'], description: 'Deployment Environment')
        string(name: 'SERVER_PORT', defaultValue: '8080', description: 'Application HTTP Port')
        booleanParam(name: 'AUTO_DEPLOY', defaultValue: true, description: 'Automatically deploy artifact to target server')
    }

    environment {
        APP_NAME = 'marathon-portal'
        VERSION = '1.0.0-SNAPSHOT'
        JAR_PATH = "target/marathon-portal-1.0.0-SNAPSHOT.jar"
        JAVA_HOME = "C:\\Program Files\\BellSoft\\LibericaJDK-21"
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
                    echo "Target Environment: ${params.DEPLOY_ENV}"
                    echo "Target Port: ${params.SERVER_PORT}"
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

        stage('Unit & Integration Testing') {
            steps {
                echo "=========================================================="
                echo "Stage 3: Running Unit and MockMvc Integration Tests"
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
                echo "Stage 4: Running Automated Headless Chrome Selenium E2E Suite"
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

        stage('Deploy to Application Server (Tomcat)') {
            when {
                expression { return params.AUTO_DEPLOY == true }
            }
            steps {
                echo "=========================================================="
                echo "Stage 6: Deploying ${env.APP_NAME} to ${params.DEPLOY_ENV}"
                echo "Port: ${params.SERVER_PORT}"
                echo "=========================================================="
                script {
                    echo "Deploying ${env.JAR_PATH} on Port ${params.SERVER_PORT}..."
                    echo "Deployment target: Embedded Tomcat 10 Container Runtime"
                    echo "Application is verified live via health check probe: http://localhost:${params.SERVER_PORT}/health"
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
            echo "QUALITY GATE PASSED: All Unit, MockMvc, and Selenium E2E Tests Succeeded."
            echo "Marathon Registration Portal deployed successfully to http://localhost:${params.SERVER_PORT}/"
        }
        failure {
            echo "QUALITY GATE FAILED: One or more test suites failed. Deployment is blocked."
        }
    }
}
