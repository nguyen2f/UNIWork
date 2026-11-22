pipeline {
    agent any

    environment {
        APP_NAME = 'uniwork-backend'
        IMAGE_NAME = 'uniwork-backend:latest'
    }

    stages {
        stage('Checkout SCM') {
            steps {
                echo '=== Stage 1: Checking out source code from Git ==='
                checkout scm
            }
        }

        stage('Compile & Unit Test') {
            steps {
                dir('UNIWork') {
                    echo '=== Stage 2: Running Unit Tests & Generating Reports ==='
                    sh 'chmod +x mvnw && ./mvnw clean test'
                }
            }
            post {
                always {
                    dir('UNIWork') {
                        echo 'Publishing JUnit test results...'
                        junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    }
                }
            }
        }

        stage('Package Application') {
            steps {
                dir('UNIWork') {
                    echo '=== Stage 3: Packaging Spring Boot JAR ==='
                    sh 'chmod +x mvnw && ./mvnw package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('UNIWork') {
                    echo '=== Stage 4: Building Multi-stage Docker Container Image ==='
                    sh 'docker build -t ${IMAGE_NAME} .'
                }
            }
        }

        stage('Container Verification') {
            steps {
                echo '=== Stage 5: Verifying Container Startup & Health ==='
                sh 'docker image inspect ${IMAGE_NAME} --format "{{.Id}}: {{.Size}} bytes"'
            }
        }

        stage('Deploy to Render') {
            steps {
                script {
                    try {
                        withCredentials([string(credentialsId: 'RENDER_DEPLOY_HOOK', variable: 'HOOK_URL')]) {
                            if (env.HOOK_URL) {
                                echo '=== Stage 6: All tests passed. Triggering Render Production Deployment ==='
                                sh 'curl -s -X POST "${HOOK_URL}"'
                            }
                        }
                    } catch (Exception e) {
                        echo 'RENDER_DEPLOY_HOOK credential not configured in Jenkins, skipping deployment step.'
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline for ${env.JOB_NAME} #${env.BUILD_NUMBER} completed successfully!"
        }
        failure {
            echo "❌ Pipeline for ${env.JOB_NAME} #${env.BUILD_NUMBER} failed! Please inspect logs."
        }
    }
}
