// src/apis/housekeepingApi.js
import axiosInstance from './axiosConfig';

export const housekeepingApi = {
    // ==================== TASKS API ====================
    getMyTasks: () => axiosInstance.get('/housekeeping/tasks/my-tasks'),
    getTodayTasks: () => axiosInstance.get('/housekeeping/tasks/today'),
    getTaskById: (id) => axiosInstance.get(`/housekeeping/tasks/${id}`),
    startTask: (taskId) => axiosInstance.post(`/housekeeping/tasks/${taskId}/start`),
    completeTask: (taskId) => axiosInstance.post(`/housekeeping/tasks/${taskId}/complete`),
    getTaskCounts: () => axiosInstance.get('/housekeeping/tasks/counts'),

    // ==================== MINIBAR API ====================
    getMinibarItems: (roomId) => axiosInstance.get(`/housekeeping/minibar/rooms/${roomId}`),
    reportMinibarConsumption: (data) => axiosInstance.post('/housekeeping/minibar/consume', data),
    getMinibarHistory: (reservationId) => axiosInstance.get(`/housekeeping/minibar/history/${reservationId}`),

    // ==================== DAMAGE API ====================
    reportDamage: (data) => axiosInstance.post('/housekeeping/damage/report', data),
    getMyDamageReports: () => axiosInstance.get('/housekeeping/damage/my-reports'),
    resolveDamage: (reportId) => axiosInstance.post(`/housekeeping/damage/${reportId}/resolve`),

    // ==================== SCHEDULE API ====================
    getMySchedule: (startDate, endDate) => {
        const params = new URLSearchParams();
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);
        return axiosInstance.get(`/housekeeping/schedule/my-schedule?${params.toString()}`);
    },
    getTodaySchedule: () => axiosInstance.get('/housekeeping/schedule/today'),
    getScheduleSummary: () => axiosInstance.get('/housekeeping/schedule/summary'),

    // ==================== REPORTS API ====================
    getPerformanceReport: (startDate, endDate) => {
        const params = new URLSearchParams();
        params.append('startDate', startDate);
        params.append('endDate', endDate);
        return axiosInstance.get(`/housekeeping/reports/performance?${params.toString()}`);
    }
};