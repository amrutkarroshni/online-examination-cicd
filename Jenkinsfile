pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        TOMCAT_URL = 'http://localhost:8080'
        APP_PATH = '/exam'
        WAR_FILE = 'target/exam-system.war'
    }

    options {
        timestamps()
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm

                bat 'git log -1 --pretty="Commit: %h by %an - %s"'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn -B test'
            }

            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package WAR') {
            steps {
                bat 'mvn -B package -DskipTests'

                archiveArtifacts(
                    artifacts: 'target/exam-system.war',
                    fingerprint: true
                )
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'tomcat-deployer',
                        usernameVariable: 'TC_USER',
                        passwordVariable: 'TC_PASS'
                    )
                ]) {
                    bat '''
                        @echo off

                        curl.exe --fail --silent --show-error ^
                          -u "%TC_USER%:%TC_PASS%" ^
                          -T "%WAR_FILE%" ^
                          "%TOMCAT_URL%/manager/text/deploy?path=%APP_PATH%&update=true" ^
                          -o deploy.log

                        if errorlevel 1 exit /b 1

                        type deploy.log

                        findstr /b /c:"OK" deploy.log >nul

                        if errorlevel 1 exit /b 1
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                retry(5) {
                    sleep time: 5, unit: 'SECONDS'

                    bat '''
                        @echo off

                        curl.exe --fail --silent --show-error ^
                          "%TOMCAT_URL%%APP_PATH%/health" ^
                          | findstr /c:"OK" >nul

                        if errorlevel 1 exit /b 1
                    '''
                }

                bat '''
                    @echo off

                    curl.exe --fail --silent --show-error ^
                      -o NUL ^
                      -w "Exam page HTTP status: %%{http_code}\\n" ^
                      "%TOMCAT_URL%%APP_PATH%/exam"

                    if errorlevel 1 exit /b 1
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline successful. Open http://localhost:8080/exam/'
        }

        failure {
            echo 'Pipeline failed. Review the failed stage and console output.'
        }

        always {
            cleanWs()
        }
    }
}