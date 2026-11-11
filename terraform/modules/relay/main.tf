resource "docker_container" "relay" {
  name  = "relay-service"
  image = "python:3.9-slim"
  # Install Flask and requests, then run a simple listener
  command = [
    "sh", "-c",
    "pip install flask requests && python -c \"
import os, requests
from flask import Flask, request
app = Flask(__name__)
@app.route('/webhook', methods=['POST'])
def hook():
    # Airflow API call to trigger DAG
    # Note: You need an Airflow token or basic auth here
    requests.post('http://airflow:8080/api/v1/dags/minio_csv_trigger/dagRuns', 
                  auth=('admin', 'admin'), 
                  json={'conf': request.json})
    return 'OK', 200
app.run(host='0.0.0.0', port=5000)
\""
  ]
  networks_advanced { name = var.network_id }
}