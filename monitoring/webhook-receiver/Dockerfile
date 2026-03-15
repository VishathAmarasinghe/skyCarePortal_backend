FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY receiver.py .

EXPOSE 5000

# Using gunicorn for production grade deployment
CMD ["gunicorn", "-b", "0.0.0.0:5000", "receiver:app"]
