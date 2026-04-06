const http = require('http');
const data = JSON.stringify({ email: 'loginfix6@test.com', password: 'test123' });

const options = {
  hostname: 'localhost',
  port: 8081,
  path: '/api/auth/register',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': Buffer.byteLength(data),
  },
};

const req = http.request(options, (res) => {
  console.log('status', res.statusCode);
  res.setEncoding('utf8');
  res.on('data', (chunk) => process.stdout.write(chunk));
});

req.on('error', (err) => {
  console.error('error', err.message);
});

req.write(data);
req.end();
