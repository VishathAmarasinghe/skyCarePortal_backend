import os
import json
import logging
import smtplib
from email.message import EmailMessage
from flask import Flask, request, jsonify
import docker

app = Flask(__name__)

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Environment Variables
SMTP_HOST = os.environ.get("SMTP_HOST", "smtp.gmail.com")
SMTP_PORT = int(os.environ.get("SMTP_PORT", 587))
SMTP_SECURE = os.environ.get("SMTP_SECURE", "false").lower() == "true"
SMTP_USER = os.environ.get("SMTP_USER")
SMTP_PASSWORD = os.environ.get("SMTP_PASSWORD")
NOTIFICATION_FROM_EMAIL = os.environ.get("NOTIFICATION_FROM_EMAIL", SMTP_USER)
ALERT_RECEIVER_EMAIL = os.environ.get("ALERT_RECEIVER_EMAIL")

# Initialize docker client
try:
    docker_client = docker.from_env()
except docker.errors.DockerException as e:
    logger.error(f"Failed to connect to docker daemon: {e}")
    docker_client = None

def get_container_logs(container_name_or_port, lines=50):
    if not docker_client:
        return "Docker client not initialized. Cannot fetch logs."
    
    # Try to heuristically map alert instance (e.g. 5001 or 8081) to container name
    # The alert instance comes as blackbox target URL: http://host.docker.internal:5001/api/actuator/health
    target_container = None
    if "5001" in container_name_or_port:
        target_container = "backend-production"
    elif "8081" in container_name_or_port:
        target_container = "frontend-production"
    elif "5002" in container_name_or_port:
        target_container = "backend-staging"
    elif "8082" in container_name_or_port:
        target_container = "frontend-staging"
    else:
        target_container = container_name_or_port

    try:
        container = docker_client.containers.get(target_container)
        logs = container.logs(tail=lines)
        return logs.decode('utf-8')
    except docker.errors.NotFound:
        # Fallback list all containers to find partial match
        try:
             for c in docker_client.containers.list():
                 if target_container in c.name:
                     logs = c.logs(tail=lines)
                     return logs.decode('utf-8')
        except Exception:
             pass
        return f"Could not find container logs for target: {target_container}"
    except Exception as e:
        return f"Error fetching logs: {str(e)}"

def send_email(subject, body):
    if not all([SMTP_USER, SMTP_PASSWORD, ALERT_RECEIVER_EMAIL]):
        logger.error("Missing email configuration. Cannot send email.")
        return False
        
    msg = EmailMessage()
    msg.set_content(body)
    msg['Subject'] = subject
    msg['From'] = NOTIFICATION_FROM_EMAIL
    msg['To'] = ALERT_RECEIVER_EMAIL

    try:
        server = smtplib.SMTP(SMTP_HOST, SMTP_PORT)
        # Gmail on port 587 requires STARTTLS even if SMTP_SECURE is logically false
        server.starttls()
        server.login(SMTP_USER, SMTP_PASSWORD)
        server.send_message(msg)
        server.quit()
        logger.info(f"Successfully sent email: {subject}")
        return True
    except Exception as e:
        logger.error(f"Failed to send email: {e}")
        return False

@app.route('/alert', methods=['POST'])
def receive_alert():
    data = request.json
    logger.info(f"Received alert: {json.dumps(data)}")
    
    if not data or not 'alerts' in data:
        return jsonify({"status": "invalid payload"}), 400
        
    for alert in data['alerts']:
        status = alert.get('status', 'unknown')
        alert_name = alert.get('labels', {}).get('alertname', 'Unknown Alert')
        instance = alert.get('labels', {}).get('instance', 'unknown-instance')
        summary = alert.get('annotations', {}).get('summary', 'No summary provided')
        description = alert.get('annotations', {}).get('description', 'No description provided')
        
        subject = f"[{status.upper()}] {alert_name} on {instance}"
        
        body = f"Alert Status: {status.upper()}\n"
        body += f"Alert Name: {alert_name}\n"
        body += f"Instance: {instance}\n"
        body += f"Summary: {summary}\n"
        body += f"Description: {description}\n\n"
        
        if status == "firing" and alert_name == "EndpointDown":
            body += f"--- Recent Docker Logs ({instance}) ---\n"
            logs = get_container_logs(instance, lines=50)
            body += logs
            body += "\n----------------------------------------\n"
            
        send_email(subject, body)
        
    return jsonify({"status": "success"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
