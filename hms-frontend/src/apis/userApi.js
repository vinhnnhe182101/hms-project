import axiosInstance from './axiosConfig';

export const userApi = {
    getCurrentUser: async () => {
        const response = await axiosInstance.get('/users/me');
        return response.data;
    },

    updateProfile: async (userData) => {
        const response = await axiosInstance.put('/users/profile', userData);
        return response.data;
    },

    changePassword: async (oldPassword, newPassword) => {
        const response = await axiosInstance.post('/users/change-password', {
            oldPassword,
            newPassword,
        });
        return response.data;
    },
};