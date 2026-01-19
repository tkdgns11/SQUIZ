pipeline {
    agent any

    environment {
        EC2_HOST = 'i14d106.p.ssafy.io'
        EC2_USER = 'ubuntu'
        DEPLOY_PATH = '/home/ubuntu/squiz'
    }

    stages {
        // ===========================================
        // 1. Checkout
        // ===========================================
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // ===========================================
        // 2. Backend Build
        // ===========================================
        stage('Backend Build') {
            steps {
                dir('modustudy/backend') {
                    sh '''
                        chmod +x gradlew
                        ./gradlew build -x test --no-daemon
                    '''
                }
            }
        }

        // ===========================================
        // 3. Deploy to EC2
        // ===========================================
        stage('Deploy to EC2') {
            steps {
                withCredentials([file(credentialsId: 'ec2-pem', variable: 'SSH_KEY')]) {
                    sh '''
                        chmod 600 $SSH_KEY

                        # 프로젝트 파일 EC2로 전송
                        rsync -avz --delete \
                            --exclude 'node_modules' \
                            --exclude '.git' \
                            --exclude 'build' \
                            --exclude '.gradle' \
                            -e "ssh -o StrictHostKeyChecking=no -i $SSH_KEY" \
                            ./modustudy/ ${EC2_USER}@${EC2_HOST}:${DEPLOY_PATH}/

                        # EC2에서 Docker Compose 실행
                        ssh -o StrictHostKeyChecking=no -i $SSH_KEY ${EC2_USER}@${EC2_HOST} << 'ENDSSH'
                            cd /home/ubuntu/squiz
                            docker-compose down || true
                            docker-compose build --no-cache
                            docker-compose up -d
                            docker image prune -f
                            docker-compose ps
ENDSSH
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '배포 성공!'
        }
        failure {
            echo '배포 실패!'
        }
        always {
            cleanWs()
        }
    }
}
