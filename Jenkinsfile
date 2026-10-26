pipeline {
    agent any

    tools {
        jdk 'JDK17'
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
                sh 'git log -1 --pretty="Commit: %h by %an - %s"'
            }
        }
        stage('Build') {
            steps { sh 'mvn -B clean compile' }
        }
        stage('Test') {
            steps { sh 'mvn -B test' }
            post {
                always { junit 'target/surefire-reports/*.xml' }
            }
        }
        stage('Package WAR') {
            steps {
                sh 'mvn -B package -DskipTests'
                archiveArtifacts artifacts: 'target/exam-system.war', fingerprint: true
            }
        }
        stage('Deploy to Tomcat') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'tomcat-deployer',
                    usernameVariable: 'TC_USER', passwordVariable: 'TC_PASS')]) {
                    sh '''
                        set -eu
                        curl --fail --silent --show-error -u "$TC_USER:$TC_PASS"                           -T "$WAR_FILE"                           "$TOMCAT_URL/manager/text/deploy?path=$APP_PATH&update=true"                           -o deploy.log
                        cat deploy.log
                        grep -q '^OK' deploy.log
                    '''
                }
            }
        }
        stage('Verify Deployment') {
            steps {
                retry(5) {
                    sleep time: 5, unit: 'SECONDS'
                    sh 'curl --fail --silent --show-error "$TOMCAT_URL$APP_PATH/health" | grep -q OK'
                }
                sh 'curl --fail --silent --show-error -o /dev/null -w "Exam page HTTP status: %{http_code}\\n" "$TOMCAT_URL$APP_PATH/exam"'
            }
        }
    }
    post {
        success { echo 'Pipeline successful. Open http://localhost:8080/exam/' }
        failure { echo 'Pipeline failed. Review the failed stage and console output.' }
        always { cleanWs() }
    }
}
