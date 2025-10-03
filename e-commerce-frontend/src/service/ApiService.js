import axios from "axios";

export default class ApiService {
    static BASE_URL = "http://localhost:8080"; // API Gateway URL

    static getHeader(contentType = "application/json") {
        const token = localStorage.getItem("token");
        return {
            Authorization: token ? `Bearer ${token}` : "",
            "Content-Type": contentType
        };
    }

    static async makeRequest(method, url, data = null, params = null, headers = null) {
        try {
            const config = {
                method,
                url: `${this.BASE_URL}${url}`,
                headers: headers || this.getHeader(),
                data,
                params,
                withCredentials: true
            };

            if (data instanceof FormData) {
                config.headers = {
                    ...config.headers,
                    "Content-Type": "multipart/form-data"
                };
            }

            const response = await axios(config);
            return response.data;
        } catch (error) {
    
            if (error.message === "Network Error") {
                console.error(`Network Error ${method} ${url}: Server might be down or unreachable`);
                return {
                    status: 503,
                    message: "Server is currently unavailable. Please try again later."
                };
            }
            
            const errorMessage = error.response?.data?.message || error.message;
            console.error(`Error ${method} ${url}:`, errorMessage);
            
            return {
                status: error.response?.status || 500,
                message: errorMessage,
                error: true
            };
        }
    }

    // --- CATEGORY METHODS --- (Product Service)
    static async createCategory(body) {
        return this.makeRequest("post", "/api/categories/create", body);
    }

    static async getAllCategory() {
        return this.makeRequest("get", "/api/categories/get-all");
    }

    static async getCategoryById(categoryId) {
        return this.makeRequest("get", `/api/categories/get-category-by-id/${categoryId}`);
    }

    static async updateCategory(categoryId, body) {
        return this.makeRequest("put", `/api/categories/update/${categoryId}`, body);
    }

    static async deleteCategory(categoryId) {
        return this.makeRequest("delete", `/api/categories/delete/${categoryId}`);
    }

    // --- PRODUCT METHODS --- (Product Service)
    static async createProduct(formData) {
        return this.makeRequest(
            "post", 
            "/api/products/create",
            formData,
            null,  
            {
                ...this.getHeader("multipart/form-data"),
                Authorization: this.getHeader().Authorization
            }
        );
    }

    static async updateProduct(productId, formData) {
        return this.makeRequest(
            "put", 
            `/api/products/update/${productId}`,
            formData,
            null,  
            {
                ...this.getHeader("multipart/form-data"),
                Authorization: this.getHeader().Authorization
            }
        );
    }

    static async deleteProduct(productId) {
        return this.makeRequest("delete", `/api/products/delete/${productId}`);
    }

    static async getAllProducts(page = 0, size = 10) {
        return this.makeRequest("get", "/api/products/get-all", null, { page, size });
    }

    static async searchProducts(searchValue) {
        return this.makeRequest("get", "/api/products/search", null, { searchValue });
    }

    static async getAllProductsByCategoryId(categoryId) {
        return this.makeRequest("get", `/api/products/get-by-category-id/${categoryId}`);
    }

    static async getProductById(productId) {
        return this.makeRequest("get", `/api/products/get-by-product-id/${productId}`);
    }

    // --- ORDER METHODS --- (Order Service)
    static async createOrder(orderRequest) {
        return this.makeRequest("post", "/api/orders/create", orderRequest);
    }

    static async getAllOrders() {
        return this.makeRequest("get", "/api/orders/filter");
    }

    static async getAllOrderItemsByStatus(status) {
        return this.makeRequest("get", "/api/orders/filter", null, { status });
    }
    
    static async getOrderItemById(itemId) {
        return this.makeRequest("get", "/api/orders/filter", null, { itemId });
    }
    
    static async updateOrderItemsByStatus(orderItemId, status) {
        return this.makeRequest("put", `/api/orders/update-item-status/${orderItemId}`, null, { status });
    }

    // --- Email Notification --- (Notification Service)
    static async sendStatusUpdateEmail(orderItemId, status, userData, productData) {
        return this.makeRequest("post", "/api/notifications/send-status-email", {
            orderItemId,
            status,
            user: userData,
            product: productData
        });
    }

    // --- PAYMENT METHODS --- (Payment Service)
    static async createCheckoutSession(orderRequest) {
        return this.makeRequest(
            "post", 
            "/api/payments/create-checkout-session",
            orderRequest,
            null,
            {
                ...this.getHeader("application/json"),
                Authorization: this.getHeader().Authorization
            }
        );
    }

    // --- AUTHENTICATION --- (User Service)
    static async registerUser(registration) {
        return this.makeRequest("post", "/api/auth/register", registration);
    }

    static async loginUser(loginDetails) {
        return this.makeRequest("post", "/api/auth/login", loginDetails);
    }

    static async getLoggedInUserInfo() {
        return this.makeRequest("get", "/api/users/my-info");
    }

    static logout() {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        window.location.href = "/login";
    }

    static isAuthenticated() {
        return !!localStorage.getItem("token");
    }

    static isAdmin() {
        return localStorage.getItem("role") === "ADMIN";
    }

    static async googleLogin(googleToken) {
        console.log("Sending Google token to backend:", googleToken);
        return this.makeRequest("post", "/api/auth/google", { token: googleToken });
    }

    static async sendOtp(email) {
        return this.makeRequest("post", "/api/auth/send-otp", { email });
    }
    
    static async verifyOtp(email, otp) {
        return this.makeRequest("post", "/api/auth/verify-otp", { email, otp });
    }
    
    static async resetPassword(email, newPassword) {
        return this.makeRequest("post", "/api/auth/reset-password", { email, newPassword });
    }

    // --- USER PROFILE METHODS --- (User Service)
    static async updateUserProfile(userData) {
        return this.makeRequest("put", "/api/users/update-profile", userData);
    }

    static async getUserProfile() {
        return this.makeRequest("get", "/api/users/profile");
    }

    static async getAllUsers() {
        return this.makeRequest("get", "/api/users/get-all");
    }
}