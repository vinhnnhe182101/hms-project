import axiosInstance from '../axiosConfig';

export const authApi = {
    login: async (email, password) => {
        try {
            const response = await axiosInstance.post('/auth/login', { email, password });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    register: async (userData) => {
        const response = await axiosInstance.post('/auth/register', userData);
        return response.data;
    },

    forgotPassword: async (email) => {
        const response = await axiosInstance.post('/auth/forgot-password', { email });
        return response.data;
    },

    getMyProfile: async () => {
        const response = await axiosInstance.get('/auth/me');
        return response.data;
    }
};
