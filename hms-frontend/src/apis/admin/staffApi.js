import axiosInstance from '../axiosConfig';

// Vì axiosInstance đã có baseURL là '.../api', nên ta chỉ cần phần còn lại
const API_URL = '/v1/admin/staff';

export const staffApi = {
    // Lấy danh sách phân trang và filter
    getStaffs: async (params) => {
        // Đổi axios.get thành axiosInstance.get
        const response = await axiosInstance.get(`${API_URL}/page`, { params });
        return response.data;
    },
    // Lấy chi tiết nhân viên
    getStaffById: async (id) => {
        const response = await axiosInstance.get(`${API_URL}/${id}`);
        return response.data;
    },
    // Tạo mới tài khoản nhân viên
    createStaff: async (data) => {
        const response = await axiosInstance.post(API_URL, data);
        return response.data;
    },
    // Xóa/Vô hiệu hóa nhân viên
    deleteStaff: async (id) => {
        const response = await axiosInstance.delete(`${API_URL}/${id}`);
        return response.data;
    }
};