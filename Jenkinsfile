pipeline {
    agent any

    environment {
        // EC2 서버 정보
        EC2_HOST = 'i14d106.p.ssafy.io'
        EC2_USER = 'ubuntu'
        DEPLOY_PATH = '/home/ubuntu/squiz'

        // Docker 이미지 태그
        IMAGE_TAG = "${BUILD_NUMBER}"
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
        // 2. Backend Test
        // ===========================================
        stage('Backend Test') {
            steps {
                dir('modustudy/backend') {
                    sh '''
                        chmod +x gradlew
                        ./gradlew test --no-daemon
                    '''
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'modustudy/backend/build/test-results/test/*.xml'
                }
            }
        }

        // ===========================================
        // 3. Frontend Build Test
        // ===========================================
        stage('Frontend Build') {
            steps {
                dir('modustudy/frontend') {
                    sh '''
                        npm ci
                        npm run build
                    '''
                }
            }
        }

        // ===========================================
        // 4. Deploy to EC2
        // ===========================================
        stage('Deploy to EC2') {
            steps {
                sshagent(credentials: ['ec2-ssh-key']) {
                    sh '''
                        # 프로젝트 파일 EC2로 전송
                        rsync -avz --delete \
                            --exclude 'node_modules' \
                            --exclude '.git' \
                            --exclude 'build' \
                            --exclude '.gradle' \
                            -e "ssh -o StrictHostKeyChecking=no" \
                            ./modustudy/ ${EC2_USER}@${EC2_HOST}:${DEPLOY_PATH}/

                        # EC2에서 Docker Compose 실행
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << 'ENDSSH'
                            cd /home/ubuntu/squiz

                            # Docker Compose 빌드 및 실행
                            docker-compose down || true
                            docker-compose build --no-cache
                            docker-compose up -d

                            # 오래된 이미지 정리
                            docker image prune -f

                            # 상태 확인
                            docker-compose ps
ENDSSH
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ 배포 성공!'
        }
        failure {
            echo '❌ 배포 실패!'
        }
        always {
            cleanWs()
        }
    }
}
