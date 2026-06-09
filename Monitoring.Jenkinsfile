pipeline {
    agent any
    environment {
        BACKEND_REPO  = 'https://github.com/VishathAmarasinghe/skyCarePortal_backend.git'
        CREDENTIALS_ID = 'GITHUB_VISHATH_CREDENTIALS'

        // Webhook Receiver Environment Variables
        SMTP_HOST = 'smtp.gmail.com'
        SMTP_PORT = '587'
        SMTP_SECURE = 'false'
        SMTP_USER = 'projectvishath@gmail.com'
        SMTP_PASSWORD = 'xejnuzjljzvdycjq'
        NOTIFICATION_FROM_EMAIL = 'projectvishath@gmail.com'
        ALERT_RECEIVER_EMAIL = 'vishathakila2001@gmail.com'

        // MySQL Exporter (Remember to create this user in MySQL and replace `your_secure_password`)
        MYSQL_EXPORTER_PASSWORD = 'exporter#AB@123'
    }
    stages {
        stage('Checkout Repository') {
            steps {
                script {
                    dir('monitoring-deploy') {
                        git url: "${BACKEND_REPO}", branch: 'main', credentialsId: "${CREDENTIALS_ID}"
                    }
                }
            }
        }
        stage('Deploy Monitoring Stack') {
            steps {
                dir('monitoring-deploy/monitoring') {
                    script {
                        sh """
                        
                        export MYSQL_EXPORTER_PASSWORD='${MYSQL_EXPORTER_PASSWORD}'
                        export SMTP_HOST='${SMTP_HOST}'
                        export SMTP_PORT='${SMTP_PORT}'
                        export SMTP_SECURE='${SMTP_SECURE}'
                        export SMTP_USER='${SMTP_USER}'
                        export SMTP_PASSWORD='${SMTP_PASSWORD}'
                        export NOTIFICATION_FROM_EMAIL='${NOTIFICATION_FROM_EMAIL}'
                        export ALERT_RECEIVER_EMAIL='${ALERT_RECEIVER_EMAIL}'
                        
                        # Deploy the monitoring stack using docker-compose
                        docker compose up -d --build --force-recreate
                        """
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Monitoring Stack deployed successfully!'
        }
        failure {
            echo 'Monitoring Setup pipeline failed. Please check the logs.'
        }
    }
}
