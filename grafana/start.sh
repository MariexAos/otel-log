#!/bin/bash

# Create data directory if it doesn't exist
mkdir -p data

# Start Grafana with docker-compose
docker-compose up -d

echo "Grafana started successfully! Access it at http://localhost:3000"
echo "Username: admin"
echo "Password: admin" 