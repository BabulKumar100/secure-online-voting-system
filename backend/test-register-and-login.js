const http = require('http');
const registerData = JSON.stringify({ email: 'test2@test.com', password: 'test123' });
const loginData = JSON.stringify({ email: 'test2@test.com', password: 'test123' });
function request(path, data, cb) {
  const options = {
    hostname: 'localhost',
    port: 8081,
    path,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(data),
    },
  };
  const req = http.request(options, (res) => {
    console.log(path, 'status', res.statusCode);
    let body='';
    res.setEncoding('utf8');
    res.on('data', (chunk) => body += chunk);
    res.on('end', () => cb(null, body));
  });
  req.on('error', cb);
  req.write(data);
  req.end();
}
request('/api/auth/register', registerData, (err, body) => {
  if (err) return console.error('register error', err);
  console.log('register body', body);
  request('/api/auth/login', loginData, (err2, body2) => {
    if (err2) return console.error('login error', err2);
    console.log('login body', body2);
  });
});
