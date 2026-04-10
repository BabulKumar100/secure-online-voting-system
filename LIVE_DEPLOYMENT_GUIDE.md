# Live Server Deployment Guide - Secure Online Voting System

## 🌐 Expected Live Server URLs

### Backend (Render)
**URL**: `https://secure-voting-system.onrender.com`  
**API Base**: `https://secure-voting-system.onrender.com/api`  
**Health Check**: `https://secure-voting-system.onrender.com/api/`

### Frontend (Vercel/Netlify)
**Primary URL**: `https://secure-voting-system.vercel.app`  
**Alternative**: `https://secure-voting-system.netlify.app`  

---

## 🚀 Quick Deployment Steps

### Step 1: Deploy Backend to Render

1. **Go to Render Dashboard**: https://dashboard.render.com
2. **Create New Web Service**
3. **Connect GitHub Repository**: `BabulKumar100/secure-online-voting-system`
4. **Configure Settings**:
   - **Name**: `secure-voting-system`
   - **Environment**: Docker
   - **Docker Context**: `./backend`
   - **Dockerfile Path**: `./backend/Dockerfile`
   - **Plan**: Free

5. **Environment Variables**:
   ```
   DATABASE_URL=jdbc:postgresql://<render-postgres-url>
   DATABASE_USERNAME=<render-db-user>
   DATABASE_PASSWORD=<render-db-password>
   DATABASE_DRIVER=org.postgresql.Driver
   DATABASE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
   JWT_SECRET=<generate-random-secret>
   ENCRYPTION_SECRET=<generate-32-char-secret>
   ENCRYPTION_IV=<generate-16-char-iv>
   ```

6. **Create PostgreSQL Database**:
   - Name: `secure-voting-db`
   - Plan: Free
   - Copy connection details to environment variables

7. **Deploy**: Click "Create Web Service"

**Backend will be live at**: `https://secure-voting-system.onrender.com`

---

### Step 2: Deploy Frontend to Vercel

1. **Go to Vercel**: https://vercel.com
2. **Import Project** → Import Git Repository
3. **Select Repository**: `BabulKumar100/secure-online-voting-system`
4. **Configure**:
   - **Framework Preset**: Vite
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`

5. **Environment Variables**:
   ```
   VITE_API_URL=https://secure-voting-system.onrender.com/api
   ```

6. **Deploy**: Click "Deploy"

**Frontend will be live at**: `https://secure-voting-system.vercel.app`

---

### Alternative: Deploy Frontend to Netlify

1. **Go to Netlify**: https://netlify.com
2. **Add New Site** → Import from Git
3. **Select Repository**: `BabulKumar100/secure-online-voting-system`
4. **Build Settings**:
   - **Base Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Publish Directory**: `dist`

5. **Environment Variables**:
   ```
   VITE_API_URL=https://secure-voting-system.onrender.com/api
   ```

6. **Deploy Site**

**Frontend will be live at**: `https://secure-voting-system.netlify.app`

---

## 📋 Pre-Deployment Checklist

### Backend (Render)
- [ ] Repository is public
- [ ] `render.yaml` is configured
- [ ] `Dockerfile` exists in backend folder
- [ ] Database service created
- [ ] Environment variables set
- [ ] CORS configured for frontend URL

### Frontend (Vercel/Netlify)
- [ ] `netlify.toml` configured (for Netlify)
- [ ] `vite.config.js` updated for production
- [ ] Environment variable `VITE_API_URL` set
- [ ] Build command configured
- [ ] Output directory set to `dist`

---

## 🔧 Post-Deployment Verification

### Test Backend APIs
```bash
# Health Check
curl https://secure-voting-system.onrender.com/api/

# System Status
curl https://secure-voting-system.onrender.com/api/status

# Candidates List
curl https://secure-voting-system.onrender.com/api/candidates

# Admin - Results (requires auth)
curl -H "Authorization: Bearer <token>" https://secure-voting-system.onrender.com/api/admin/results
```

### Test Frontend
1. Open `https://secure-voting-system.vercel.app`
2. Register a new user
3. Login with email/password
4. Enter OTP (check console for OTP code)
5. Select candidate and vote
6. Verify encryption message appears

---

## 🐛 Troubleshooting

### CORS Issues
Update `SecurityConfig.java` to include production frontend URL:
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175",
    "https://secure-voting-system.vercel.app",  // Add this
    "https://secure-voting-system.netlify.app"  // Add this
));
```

### Database Connection Issues
- Verify `DATABASE_URL` format: `jdbc:postgresql://host:port/dbname`
- Check PostgreSQL service is running on Render
- Verify username/password match

### Frontend API Connection
- Verify `VITE_API_URL` points to correct backend URL
- Check browser console for CORS errors
- Ensure HTTPS is used for production

---

## 📊 Live System Features

### Security (All Enabled)
- ✅ **Confidentiality**: AES-256 vote encryption
- ✅ **Integrity**: RSA-2048 digital signatures
- ✅ **Authentication**: OTP + JWT two-factor auth

### Available Endpoints
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/` | GET | Health check |
| `/api/status` | GET | System status |
| `/api/candidates` | GET | List candidates |
| `/api/auth/register` | POST | User registration |
| `/api/auth/login` | POST | Login (sends OTP) |
| `/api/auth/verify-otp` | POST | Verify OTP |
| `/api/vote` | POST | Submit encrypted vote |
| `/api/admin/results` | GET | Decrypted results |

---

## 🎯 Next Steps After Deployment

1. **Test Registration Flow**: Create test user
2. **Test Voting Flow**: Submit encrypted vote
3. **Verify Results**: Check admin panel decryption
4. **Monitor Logs**: Check Render dashboard for errors
5. **Performance Test**: Load test with multiple users

---

## 📞 Support

If deployment fails:
1. Check Render logs for backend errors
2. Check Vercel/Netlify build logs
3. Verify environment variables are correct
4. Test APIs locally before deploying
5. Check CORS configuration

---

**System Status**: Ready for Deployment 🚀
**Last Updated**: April 11, 2026
**Version**: 2.0 - Secure Java Cryptography Edition
