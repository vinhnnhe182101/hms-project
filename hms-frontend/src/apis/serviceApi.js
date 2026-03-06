import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});


export const getAllServices = async (page = 0, size = 50) => {
    try {
        const response = await api.get('/services', { params: { page, size } });
        return response.data;
    } catch (error) {
        console.error('Error fetching all services:', error);
        throw error;
    }
};

/**
 * GET /api/v1/services/by-category?category=SPA&page=0&size=10
 * Lấy dịch vụ theo danh mục.
 *
 * @param {string} category - Giá trị enum: 'SPA' | 'MINIBAR'
 */
export const getServicesByCategory = async (category, page = 0, size = 50) => {
    try {
        const response = await api.get('/services/by-category', {
            params: { category, page, size }
        });
        return response.data;
    } catch (error) {
        console.error(`Error fetching services by category (${category}):`, error);
        throw error;
    }
};
