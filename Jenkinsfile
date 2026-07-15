pipeline {

    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    environment {
        IMAGE_NAME     = "student-ecom"
        DOCKERHUB_REPO = "kanchanashashank123/student-ecom"
        IMAGE_TAG      = "${BUILD_NUMBER}"

        MYSQL_DATABASE = "student_ecom_db"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/kanchana-prog/e-commerce-web.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }

            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([
                        string(
                            credentialsId: 'sonar',
                            variable: 'SONAR_TOKEN'
                        )
                    ]) {

                        sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=student-ecom \
                        -Dsonar.projectName=student-ecom \
                        -Dsonar.token=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {

                sh """
                docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .

                docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_REPO}:${IMAGE_TAG}

                docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${DOCKERHUB_REPO}:latest
                """
            }
        }

        stage('Push Docker Image') {

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {

                    sh '''
                    echo "$DOCKER_PASS" | docker login \
                    -u "$DOCKER_USER" \
                    --password-stdin

                    docker push $DOCKERHUB_REPO:$IMAGE_TAG
                    docker push $DOCKERHUB_REPO:latest

                    docker logout
                    '''
                }
            }
        }

        stage('Deploy using Docker Compose') {

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'mysql-creds',
                        usernameVariable: 'MYSQL_USER',
                        passwordVariable: 'MYSQL_PASSWORD'
                    )
                ]) {

                    sh '''
                    export MYSQL_DATABASE=student_ecom_db
                    export MYSQL_USER=$MYSQL_USER
                    export MYSQL_PASSWORD=$MYSQL_PASSWORD
                    export MYSQL_ROOT_PASSWORD=$MYSQL_PASSWORD

                    # Stop and remove old containers if they exist
                    docker rm -f student-ecom || true
                    docker rm -f mysql || true

                    # Remove old compose project
                    docker-compose down --remove-orphans || true

                    # Pull latest Docker Hub image
                    docker-compose pull

                    # Deploy latest containers
                    docker-compose up -d --force-recreate

                    # Show running containers
                    docker ps
                    '''
                }
            }
        }

        stage('Health Check') {

            steps {

                sh '''
                echo "Waiting for application..."
                sleep 30

                curl --fail http://localhost:8087/
                '''
            }
        }

        stage('Docker Cleanup') {

            steps {

                sh '''
                docker image prune -f
                docker system df
                '''
            }
        }
    }

    post {

        success {

            echo "===================================="
            echo "BUILD SUCCESSFUL"
            echo "===================================="

            sh 'docker ps'
        }

        failure {

            echo "===================================="
            echo "BUILD FAILED"
            echo "===================================="

            sh 'docker ps -a'
        }

        always {
            cleanWs()
        }
    }
}
