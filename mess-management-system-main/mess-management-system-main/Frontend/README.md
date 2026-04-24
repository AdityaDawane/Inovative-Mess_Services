# 🍽️ Mess Management System - Frontend

Modern, responsive frontend for the Mess Management System built with HTML, CSS, and JavaScript.

## 📋 Features

- ✅ Student Registration & Login
- ✅ Meal Plan Browsing & Booking
- ✅ QR Code Generation & Display
- ✅ Vendor QR Code Scanner
- ✅ Attendance Tracking
- ✅ Responsive Design
- ✅ Modern UI with Gradients

## 🚀 Quick Start

### Prerequisites
- A web browser (Chrome, Firefox, Safari, Edge)
- Your Spring Boot backend running on `http://localhost:8081`

### Setup

1. **Download/Clone this frontend folder**

2. **Open in browser**
   - Simply open `index.html` in your web browser
   - OR use a local server:
   ```bash
   # Using Python
   python -m http.server 8000
   
   # Using Node.js
   npx serve
   
   # Using VS Code
   Install "Live Server" extension and click "Go Live"
   ```

3. **Access the application**
   - Open http://localhost:8000 (or the port your server uses)
   - Or just double-click `index.html`

## 📁 File Structure

```
mess-frontend/
├── index.html              # Landing page
├── student-register.html   # Student registration
├── student-login.html      # Student login
├── student-dashboard.html  # Student dashboard
├── my-qr-code.html        # QR code display
├── meal-plans.html        # Available plans (to be created)
├── vendor-login.html      # Vendor login
├── vendor-scanner.html    # QR code scanner
├── vendor-attendance.html # Attendance view (to be created)
├── admin-login.html       # Admin login (to be created)
├── css/
│   └── style.css          # All styles
└── js/
    └── api.js             # API service
```

## 🎯 User Flows

### Student Flow
1. Register → `student-register.html`
2. Login → `student-login.html`
3. Dashboard → `student-dashboard.html`
4. View/Book Plans → Dashboard shows available plans
5. Get QR Code → `my-qr-code.html`
6. Show QR at mess counter

### Vendor Flow
1. Login → `vendor-login.html`
2. Scanner → `vendor-scanner.html`
3. Scan student QR codes
4. Mark attendance automatically

## 🔧 Configuration

### API Base URL

To connect to your backend, update the `API_BASE_URL` in `js/api.js`:

```javascript
const API_BASE_URL = 'http://localhost:8081/api';
```

For production:
```javascript
const API_BASE_URL = 'https://your-backend.railway.app/api';
```

## 🎨 Customization

### Colors

Edit CSS variables in `css/style.css`:

```css
:root {
    --primary: #667eea;      /* Primary color */
    --secondary: #764ba2;    /* Secondary color */
    --success: #48bb78;      /* Success color */
    --danger: #f56565;       /* Danger color */
}
```

### Branding

Update the navbar brand in each HTML file:

```html
<div class="nav-brand">
    <h1>🍽️ Your College Name Mess</h1>
</div>
```

## 📱 Features by Page

### 1. Landing Page (index.html)
- Hero section with CTA
- Features showcase
- How it works section
- Responsive design

### 2. Student Dashboard
- View active subscriptions
- Display QR code
- Browse & book meal plans
- Quick stats

### 3. QR Code Display
- Full-screen QR code
- Optimized for scanning
- Clear instructions

### 4. Vendor Scanner
- QR code input
- Meal type selection
- Real-time scanning
- Recent scans list
- Sound effects on success/error
- Auto-submit on paste

## 🔐 Authentication

The frontend uses JWT tokens stored in localStorage:

```javascript
// Stored after login
localStorage.setItem('token', token);
localStorage.setItem('userId', userId);
localStorage.setItem('userRole', role);

// Retrieved for API calls
const token = localStorage.getItem('token');
```

## 🐛 Troubleshooting

### CORS Errors

If you see CORS errors in console:

1. **Backend Fix:** Update `SecurityConfig.java`:
```java
config.setAllowedOrigins(List.of(
    "http://localhost:8000",
    "http://localhost:3000",
    "http://127.0.0.1:8000"
));
```

2. **Or use browser extensions:**
   - Chrome: "Allow CORS: Access-Control-Allow-Origin"
   - Firefox: "CORS Everywhere"

### API Connection Issues

1. Verify backend is running: http://localhost:8081/api/public/menu/today
2. Check API_BASE_URL in `js/api.js`
3. Open browser console (F12) for error details

### Login Not Working

1. Check backend logs
2. Verify credentials
3. Check if password encryption is working
4. Clear localStorage: `localStorage.clear()`

## 📚 Dependencies

### External Libraries Used

1. **QRCode.js** - For QR code generation
   - CDN: https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js
   - Used in: student-dashboard.html, my-qr-code.html

### No Build Tools Needed!

This is pure HTML/CSS/JS - no npm, webpack, or build process required.

## 🚀 Deployment

### Option 1: Vercel (Recommended)

1. Install Vercel CLI:
```bash
npm install -g vercel
```

2. Deploy:
```bash
cd mess-frontend
vercel
```

3. Follow prompts

### Option 2: Netlify

1. Drag and drop the entire folder to netlify.com
2. Or use Netlify CLI

### Option 3: GitHub Pages

1. Push to GitHub repository
2. Go to Settings → Pages
3. Select branch and save

### Update API URL for Production

After deployment, update `js/api.js`:

```javascript
const API_BASE_URL = 'https://your-backend.railway.app/api';
```

## 🎯 Testing Checklist

- [ ] Student can register
- [ ] Student can login
- [ ] Dashboard loads correctly
- [ ] Can view available plans
- [ ] Can book a plan
- [ ] QR code displays correctly
- [ ] QR code can be scanned
- [ ] Vendor can login
- [ ] Vendor can scan QR codes
- [ ] Recent scans show up
- [ ] Logout works
- [ ] Responsive on mobile

## 📱 Mobile Responsiveness

The UI is fully responsive and works on:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (< 768px)

Test on different devices or use browser DevTools (F12 → Device Toolbar).

## 🎨 Screenshots

Add screenshots in a `screenshots/` folder:
- Landing page
- Student dashboard
- QR code display
- Vendor scanner

## 📞 Support

For issues or questions:
1. Check troubleshooting section
2. Review browser console (F12)
3. Check backend logs
4. Verify API endpoints in Postman

## 📄 License

College Project - Free to use and modify

## 🙏 Credits

- Design inspired by modern web applications
- QR Code generation by QRCode.js
- Built for college project

---

**Made with ❤️ for College Project**
