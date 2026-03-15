import axiosInstance from '../axiosConfig';

const API_URL = '/v1/admin/schedules';

export const scheduleApi = {
    // Truyền startDate và endDate định dạng YYYY-MM-DD
    getSchedules: async (startDate, endDate) => {
        const response = await axiosInstance.get(API_URL, {
            params: { startDate, endDate }
        });
        return response.data;
    },
    createSchedule: async (data) => {
        const response = await axiosInstance.post(`${API_URL}/assign`, data);
        return response.data;
    },
    deleteSchedule: async (id) => {
        const response = await axiosInstance.delete(`${API_URL}/${id}`);
        return response.data;
    }
};