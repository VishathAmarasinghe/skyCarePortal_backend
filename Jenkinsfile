// Multibranch: dev → backend:staging + pdf-service:staging + deploy staging stack
// main → backend:production + pdf-service:production + deploy production stack
// Compose -p isolates staging vs production projects so --remove-orphans does not delete the other env.
// Staff training certificates, behavior support registers, and compliance lookups are part of the backend JAR;
// upload dirs on the host must exist and are mounted to /root/skyCareUploads in the container.
pipeline {
    agent any
    stages {
        stage('Build backend (staging)') {
            when {
                branch 'dev'
            }
            steps {
                sh '''
                    docker build -t backend:staging .
                    docker build -t pdf-service:staging ./pdf-service
                    docker image prune -f
                    docker builder prune -f
                '''
            }
        }
        stage('Deploy staging') {
            when {
                branch 'dev'
            }
            steps {
                withCredentials([
                    string(credentialsId: 'SKYCARE_STAGING_DB_USERNAME', variable: 'SPRING_DATASOURCE_USERNAME'),
                    string(credentialsId: 'SKYCARE_STAGING_DB_PASSWORD', variable: 'SPRING_DATASOURCE_PASSWORD'),
                    string(credentialsId: 'SKYCARE_MAIL_USERNAME', variable: 'SPRING_MAIL_USERNAME'),
                    string(credentialsId: 'SKYCARE_MAIL_PASSWORD', variable: 'SPRING_MAIL_PASSWORD')
                ]) {
                    sh '''
                        export SPRING_DATASOURCE_USERNAME
                        export SPRING_DATASOURCE_PASSWORD
                        export SPRING_MAIL_USERNAME
                        export SPRING_MAIL_PASSWORD
                        export FRONTEND_URL=https://stg.skycare.au
                        export BACKEND_BASE_URL=https://stg.skycare.au/api

                        chmod +x scripts/prepare-upload-dirs.sh
                        ./scripts/prepare-upload-dirs.sh /var/lib/skycareuploads

                        # Fixed container_name values conflict with leftovers from older deploys (no -p project).
                        docker compose -p skycare-backend-staging -f docker-compose.dev.yml down --remove-orphans || true
                        docker rm -f pdf-service-staging backend-staging 2>/dev/null || true

                        docker compose -p skycare-backend-staging -f docker-compose.dev.yml up -d \
                          --force-recreate --remove-orphans \
                          pdf-service-staging backend-staging

                        docker image prune -f
                        docker container prune -f
                        docker builder prune -f
                    '''
                }
            }
        }
        stage('Build backend (production)') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    docker build -t backend:production .
                    docker build -t pdf-service:production ./pdf-service
                    docker image prune -f
                    docker builder prune -f
                '''
            }
        }
        stage('Deploy production') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([
                    string(credentialsId: 'SKYCARE_PRODUCTION_DB_USERNAME', variable: 'SPRING_DATASOURCE_USERNAME'),
                    string(credentialsId: 'SKYCARE_PRODUCTION_DB_PASSWORD', variable: 'SPRING_DATASOURCE_PASSWORD'),
                    string(credentialsId: 'SKYCARE_MAIL_USERNAME', variable: 'SPRING_MAIL_USERNAME'),
                    string(credentialsId: 'SKYCARE_MAIL_PASSWORD', variable: 'SPRING_MAIL_PASSWORD')
                ]) {
                    sh '''
                        export SPRING_DATASOURCE_USERNAME
                        export SPRING_DATASOURCE_PASSWORD
                        export SPRING_MAIL_USERNAME
                        export SPRING_MAIL_PASSWORD
                        export FRONTEND_URL=https://skycare.au
                        export BACKEND_BASE_URL=https://skycare.au/api

                        chmod +x scripts/prepare-upload-dirs.sh
                        ./scripts/prepare-upload-dirs.sh /var/lib/skycare_production/skycareuploads

                        docker compose -p skycare-backend-production -f docker-compose.prod.yml down --remove-orphans || true
                        docker rm -f pdf-service-production backend-production 2>/dev/null || true

                        docker compose -p skycare-backend-production -f docker-compose.prod.yml up -d \
                          --force-recreate --remove-orphans \
                          pdf-service-production backend-production

                        docker image prune -f
                        docker container prune -f
                        docker builder prune -f
                    '''
                }
            }
        }
    }
    post {
        success {
            echo 'Backend pipeline completed (API, staff training, behavior support, PDF service).'
        }
        failure {
            echo 'Backend pipeline failed.'
        }
    }
}
