// Multibranch: dev → backend:staging + deploy backend-staging only
// main → backend:production + deploy backend-production only
// Compose uses default project network (no external app-network). Prune after build/deploy.
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
                        docker compose -f docker-compose.dev.yml up -d --no-deps --force-recreate --remove-orphans backend-staging
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
                        docker compose -f docker-compose.prod.yml up -d --no-deps --force-recreate --remove-orphans backend-production
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
            echo 'Backend pipeline completed.'
        }
        failure {
            echo 'Backend pipeline failed.'
        }
    }
}
