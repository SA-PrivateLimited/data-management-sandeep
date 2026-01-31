#!/bin/bash

echo "Starting Data Collector Backend Server..."
echo "=========================================="
echo ""

cd backend-server

if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

echo "Starting server on http://0.0.0.0:3000"
echo "Dashboard will be available at http://localhost:3000"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

npm start
