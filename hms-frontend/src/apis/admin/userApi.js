import axiosInstance from '../axiosConfig'; // Sửa lại đường dẫn tương đối cho đúng thư mục của bạn

const API_URL = '/v1/admin/users';

export const userApi = {
    // Lấy danh sách khách hàng phân trang + filter
    getCustomersPage: async (params) => {
        const response = await axiosInstance.get(`${API_URL}/customers/page`, { params });
        return response.data;
    },

    // Cập nhật trạng thái Active/Disable của user
    updateUserStatus: async (id, isActive) => {
        const response = await axiosInstance.patch(`${API_URL}/${id}/status`, { isActive });
        return response.data;
    },

    // Xóa user (nếu cần)
    deleteUser: async (id) => {
        const response = await axiosInstance.delete(`${API_URL}/${id}`);
        return response.data;
    }
};