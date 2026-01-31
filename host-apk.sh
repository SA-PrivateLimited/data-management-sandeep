#!/bin/bash

# Simple HTTP Server to Host APK
# Run this script to start a local server

PORT=8080
APK_FILE="android-app/app/build/outputs/apk/debug/app-debug.apk"
ZIP_FILE="DeviceManager.zip"

echo "🚀 Starting APK Hosting Server..."
echo "=================================="
echo ""
echo "Server will run on:"
echo "  http://localhost:$PORT"
echo "  http://$(hostname -I | awk '{print $1}'):$PORT"
echo ""
echo "Download links:"
echo "  APK: http://YOUR_IP:$PORT/download/app-debug.apk"
echo "  ZIP: http://YOUR_IP:$PORT/download/DeviceManager.zip"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

# Create download directory
mkdir -p downloads
cp "$APK_FILE" downloads/app-debug.apk 2>/dev/null || true
cp "$ZIP_FILE" downloads/DeviceManager.zip 2>/dev/null || true

# Start Python HTTP server
cd downloads
python3 -m http.server $PORT
