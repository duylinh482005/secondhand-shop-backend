// Cấu hình API Base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Các endpoint API
const API_ENDPOINTS = {
    // Categories
    CATEGORIES: `${API_BASE_URL}/categories`,
    CATEGORIES_ACTIVE: `${API_BASE_URL}/categories/active`,

    USERS: `${API_BASE_URL}/users`,
    
    // Products
    PRODUCTS: `${API_BASE_URL}/products`,
    PRODUCTS_AVAILABLE: `${API_BASE_URL}/products/available`,
    PRODUCTS_LATEST: `${API_BASE_URL}/products/latest`,
    PRODUCTS_BY_CATEGORY: (categoryId) => `${API_BASE_URL}/products/category/${categoryId}`,
    PRODUCTS_SEARCH: (keyword) => `${API_BASE_URL}/products/search?keyword=${keyword}`,
    
    // Orders
    ORDERS: `${API_BASE_URL}/orders`,
    ORDERS_BY_CUSTOMER: (customerId) => `${API_BASE_URL}/orders/customer/${customerId}`,
    ORDERS_BY_STATUS: (status) => `${API_BASE_URL}/orders/status/${status}`,
    ORDER_UPDATE_STATUS: (id, status) => `${API_BASE_URL}/orders/${id}/status?status=${status}`,
    ORDER_CANCEL: (id) => `${API_BASE_URL}/orders/${id}/cancel`,
    
    // Customers
    CUSTOMERS: `${API_BASE_URL}/customers`,
    CUSTOMER_BY_USER: (userId) => `${API_BASE_URL}/customers/user/${userId}`
};

// Các trạng thái
const STATUS = {
    PRODUCT: {
        AVAILABLE: 'AVAILABLE',
        SOLD: 'SOLD',
        RESERVED: 'RESERVED',
        DELETED: 'DELETED'
    },
    ORDER: {
        PENDING: 'PENDING',
        CONFIRMED: 'CONFIRMED',
        SHIPPING: 'SHIPPING',
        DELIVERED: 'DELIVERED',
        CANCELLED: 'CANCELLED'
    },
    PRODUCT_CONDITION: {
        NEW: 'NEW',
        LIKE_NEW: 'LIKE_NEW',
        GOOD: 'GOOD',
        FAIR: 'FAIR',
        POOR: 'POOR'
    }
};

// Format tiền VND
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDate(dateString) {
    if (!dateString) return '-';

    const date = new Date(dateString);

    if (isNaN(date.getTime())) {
        console.warn('Invalid date:', dateString);
        return '-';
    }

    return new Intl.DateTimeFormat('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
}
