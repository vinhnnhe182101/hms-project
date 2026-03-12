import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});


/**
 * GET /api/v1/home/services?category=SPA&page=0&size=10
 * Lấy danh sách dịch vụ (có thể lọc theo category).
 */
export const getServices = async (category = null, page = 0, size = 50) => {
    try {
        const params = { page, size };
        if (category && category !== 'all') {
            params.category = category;
        }
        const response = await api.get('/home/services', { params });
        return response.data;
    } catch (error) {
        console.error('Error fetching services:', error);
        throw error;
    }
};

export const getServiceCategories = async () => {
    try {
        const response = await api.get('/home/services/categories');
        return response.data;
    } catch (error) {
        console.error('Error fetching service categories:', error);
        throw error;
    }
};
