const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 8080;
const APK_PATH = path.join(__dirname, 'android-app/app/build/outputs/apk/debug/app-debug.apk');
const ZIP_PATH = path.join(__dirname, 'DeviceManager.zip');

const server = http.createServer((req, res) => {
    let filePath = '';
    let contentType = 'application/vnd.android.package-archive';
    
    if (req.url === '/' || req.url === '/index.html') {
        // Serve a simple download page
        res.writeHead(200, { 'Content-Type': 'text/html' });
        res.end(`
<!DOCTYPE html>
<html>
<head>
    <title>Device Manager - Download</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body { font-family: Arial, sans-serif; text-align: center; padding: 50px; background: #f5f5f5; }
        .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; }
        .download-btn { display: inline-block; margin: 10px; padding: 15px 30px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }
        .download-btn:hover { background: #45a049; }
        .info { margin-top: 20px; color: #666; font-size: 14px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>📱 Device Manager</h1>
        <p>Download the latest version:</p>
        <a href="/download/apk" class="download-btn">Download APK</a>
        <a href="/download/zip" class="download-btn">Download ZIP</a>
        <div class="info">
            <p>File size: ~9 MB</p>
            <p>Android 7.0+ required</p>
        </div>
    </div>
</body>
</html>
        `);
        return;
    }
    
    if (req.url === '/download/apk') {
        filePath = APK_PATH;
    } else if (req.url === '/download/zip') {
        filePath = ZIP_PATH;
        contentType = 'application/zip';
    } else {
        res.writeHead(404);
        res.end('Not Found');
        return;
    }
    
    // Check if file exists
    if (!fs.existsSync(filePath)) {
        res.writeHead(404);
        res.end('File not found');
        return;
    }
    
    // Read and serve file
    const file = fs.readFileSync(filePath);
    const fileName = path.basename(filePath);
    
    res.writeHead(200, {
        'Content-Type': contentType,
        'Content-Disposition': `attachment; filename="${fileName}"`,
        'Content-Length': file.length
    });
    
    res.end(file);
});

server.listen(PORT, '0.0.0.0', () => {
    const os = require('os');
    const networkInterfaces = os.networkInterfaces();
    let localIP = 'localhost';
    
    for (const interfaceName in networkInterfaces) {
        for (const iface of networkInterfaces[interfaceName]) {
            if (iface.family === 'IPv4' && !iface.internal) {
                localIP = iface.address;
                break;
            }
        }
    }
    
    console.log('🚀 APK Hosting Server Started!');
    console.log('================================');
    console.log('');
    console.log('📱 Download Links:');
    console.log(`   Local:  http://localhost:${PORT}`);
    console.log(`   Network: http://${localIP}:${PORT}`);
    console.log('');
    console.log('📦 Direct Download Links:');
    console.log(`   APK: http://${localIP}:${PORT}/download/apk`);
    console.log(`   ZIP: http://${localIP}:${PORT}/download/zip`);
    console.log('');
    console.log('🌐 Share these links with others on your network');
    console.log('   Press Ctrl+C to stop the server');
    console.log('');
});
