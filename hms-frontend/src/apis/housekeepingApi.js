// src/apis/housekeepingApi.js
import axiosInstance from './axiosConfig';

export const housekeepingApi = {
    getMyTasks: async () => {
        try {
            const response = await axiosInstance.get('/housekeeping/tasks/my-tasks');
            return response.data;
        } catch (error) {
            console.error('Error fetching tasks:', error);
            throw error;
        }
    },

    getTodayTasks: async () => {
        try {
            const response = await axiosInstance.get('/housekeeping/tasks/today');
            return response.data;
        } catch (error) {
            console.error('Error fetching today tasks:', error);
            throw error;
        }
    },

    getTasksByStatus: async (status) => {
        try {
            const response = await axiosInstance.get(`/housekeeping/tasks/status/${status}`);
            return response.data;
        } catch (error) {
            console.error(`Error fetching ${status} tasks:`, error);
            throw error;
        }
    },

    getTaskCounts: async () => {
        try {
            const response = await axiosInstance.get('/housekeeping/tasks/counts');
            return response.data;
        } catch (error) {
            console.error('Error fetching task counts:', error);
            throw error;
        }
    },
    // POST endpoints (mới)
    startTask: async (taskId) => {
        const response = await axiosInstance.post(`/housekeeping/tasks/${taskId}/start`);
        return response.data;
    },

    completeTask: async (taskId) => {
        const response = await axiosInstance.post(`/housekeeping/tasks/${taskId}/complete`);
        return response.data;
    },

    updateTaskStatus: async (data) => {
        const response = await axiosInstance.put('/housekeeping/tasks/status', data);
        return response.data;
    }
};