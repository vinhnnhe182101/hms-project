import axios from 'axios';

// Thay thế bằng axios instance của bạn nếu có
const API_URL = '/api/v1/admin/staff';

export const staffApi = {
    // Lấy danh sách phân trang và filter
    getStaffs: async (params) => {
        const response = await axios.get(`${API_URL}/page`, { params });
        return response.data;
    },
    // Lấy chi tiết nhân viên
    getStaffById: async (id) => {
        const response = await axios.get(`${API_URL}/${id}`);
        return response.data;
    },
    // Tạo mới tài khoản nhân viên
    createStaff: async (data) => {
        const response = await axios.post(API_URL, data);
        return response.data;
    },
    // Xóa/Vô hiệu hóa nhân viên
    deleteStaff: async (id) => {
        const response = await axios.delete(`${API_URL}/${id}`);
        return response.data;
    }
};