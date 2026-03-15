import axiosInstance from '../axiosConfig';

export const dashboardApi = {
    getDashboardData: async () => {
        const response = await axiosInstance.get('/v1/admin/dashboard');
        return response.data;
    }
};