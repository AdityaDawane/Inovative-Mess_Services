// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';

// API Service
const API = {
    // Helper method for API calls
    async call(endpoint, options = {}) {
        const token = localStorage.getItem('token');
        
        const config = {
            method: options.method || 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` }),
                ...options.headers
            }
        };

        // Add body if present
        if (options.body) {
            config.body = options.body;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'API request failed');
        }

        return data;
    },

    // Customer APIs
    customer: {
        register: async (formData) => {
            return await API.call('/customer/register', {
                method: 'POST',
                body: JSON.stringify(formData)
            });
        },

        login: async (email, password) => {
            return await API.call('/customer/login', {
                method: 'POST',
                body: JSON.stringify({ email, password })
            });
        },

        getProfile: async (customerId) => {
            return await API.call(`/customer/profile/${customerId}`);
        },

        bookMeal: async (customerId, planId) => {
            return await API.call('/customer/book-meal', {
                method: 'POST',
                body: JSON.stringify({ customerId, planId })
            });
        },

        getQRCode: async (customerId) => {
            return await API.call(`/customer/qr-code/${customerId}`);
        },

        getActiveBookings: async (customerId) => {
            return await API.call(`/customer/active-bookings/${customerId}`);
        },

        getAttendanceHistory: async (customerId, startDate, endDate) => {
            return await API.call(`/customer/attendance-history/${customerId}?startDate=${startDate}&endDate=${endDate}`);
        }
    },

    // Vendor APIs
    vendor: {
        login: async (email, password) => {
            return await API.call('/vendor/login', {
                method: 'POST',
                body: JSON.stringify({ email, password })
            });
        },

        scanQR: async (qrCode, mealType, vendorId) => {
            return await API.call('/vendor/scan-qr', {
                method: 'POST',
                body: JSON.stringify({
                    qrCode,
                    mealType,
                    vendorId,
                    machineId: 'WEB-SCANNER'
                })
            });
        },

        getTodayAttendance: async () => {
            return await API.call('/vendor/attendance/today');
        }
    },

    // Public APIs
    public: {
        getPlans: async () => {
                return await API.call('/vendor/meal-plans');
        },

        getTodayMenu: async () => {
            return await API.call('/public/menu/today');
        }
    },

    // Payment APIs
    payment: {
        createOrder: async (subscriptionId, amount, paymentMethod) => {
            return await API.call('/payment/create-order', {
                method: 'POST',
                body: JSON.stringify({
                    subscriptionId,
                    amount,
                    paymentMethod,
                    paymentGateway: 'RAZORPAY'
                })
            });
        }
    }
};

// Auth helpers
const Auth = {
    saveToken: (token, userId, role) => {
        localStorage.setItem('token', token);
        localStorage.setItem('userId', userId);
        localStorage.setItem('userRole', role);
    },

    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('userRole');
        window.location.href = 'index.html';
    },

    isAuthenticated: () => {
        return !!localStorage.getItem('token');
    },

    getUserId: () => {
        return localStorage.getItem('userId');
    },

    getUserRole: () => {
        return localStorage.getItem('userRole');
    },

    requireAuth: (redirectUrl = 'index.html') => {
        if (!Auth.isAuthenticated()) {
            window.location.href = redirectUrl;
        }
    }
};

// UI Helpers
const UI = {
    showAlert: (message, type = 'success') => {
        const alert = document.createElement('div');
        alert.className = `alert alert-${type}`;
        alert.textContent = message;
        
        const container = document.querySelector('.container');
        if (container) {
            container.insertBefore(alert, container.firstChild);
            
            setTimeout(() => alert.remove(), 5000);
        }
    },

    showLoading: (button) => {
        button.disabled = true;
        button.innerHTML = '<span class="loading"></span> Loading...';
    },

    hideLoading: (button, originalText) => {
        button.disabled = false;
        button.innerHTML = originalText;
    }
};

// QR Code Generator (using qrcode.js library)
const QRCodeGenerator = {
    generate: (text, elementId) => {
        const qrcode = new QRCode(document.getElementById(elementId), {
            text: text,
            width: 256,
            height: 256,
            colorDark: "#667eea",
            colorLight: "#ffffff",
            correctLevel: QRCode.CorrectLevel.H
        });
        return qrcode;
    }
};
