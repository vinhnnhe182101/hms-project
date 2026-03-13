import axiosInstance from './axiosConfig';

export const authApi = {
    login: async (email, password) => {
        try {
            const response = await axiosInstance.post('/auth/login', { email, password });
            console.log('Login API response:', response.data); // Debug log
            return response.data;
        } catch (error) {
            console.error('Login API error:', error.response?.data || error.message);
            throw error;
        }
    },

    // Register new user (customer)
    register: async (userData) => {
        const response = await axiosInstance.post('/auth/register', userData);
        return response.data;
    },


    // Forgot password
    forgotPassword: async (email) => {
        const response = await axiosInstance.post('/auth/forgot-password', { email });
        return response.data;
    },

};
