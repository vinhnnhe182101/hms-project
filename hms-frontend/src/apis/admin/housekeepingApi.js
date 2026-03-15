import axiosInstance from '../axiosConfig';

const API_URL = '/v1/admin/housekeeping';

export const housekeepingApi = {
    // Lấy danh sách tầng
    getFloors: async () => {
        const response = await axiosInstance.get(`${API_URL}/floors`);
        return response.data;
    },

    // Lấy ma trận phòng theo tầng
    getRoomMatrix: async (floor) => {
        const response = await axiosInstance.get(`${API_URL}/rooms-matrix`, {
            params: { floor }
        });
        return response.data;
    },

    // Lấy nhân viên housekeeping đang trong ca làm việc
    getAvailableStaff: async () => {
        const response = await axiosInstance.get(`${API_URL}/staff/available`);
        return response.data;
    },

    // Lấy toàn bộ danh sách task
    getAllTasks: async () => {
        const response = await axiosInstance.get(`${API_URL}/tasks`);
        return response.data;
    },

    // Giao việc cho nhân viên
    assignTask: async (data) => {
        const response = await axiosInstance.post(`${API_URL}/tasks`, data);
        return response.data;
    },

    // Xóa/Hủy task
    deleteTask: async (id) => {
        const response = await axiosInstance.delete(`${API_URL}/tasks/${id}`);
        return response.data;
    }
};