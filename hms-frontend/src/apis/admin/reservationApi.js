import axiosInstance from '../axiosConfig';

const API_URL = '/v1/reservations';

export const reservationApi = {
    getReservations: async (params = {}) => {
        const response = await axiosInstance.get(API_URL, {
            params: {
                page: 0,
                size: 10,
                ...params,
            },
        });

        const payload = response.data || {};
        return {
            content: Array.isArray(payload.content) ? payload.content : [],
            totalPages: payload.totalPages || 1,
            totalElements: payload.totalElements || 0,
            number: payload.number || 0,
        };
    },

    cancelReservation: async (reservationId) => {
        const response = await axiosInstance.delete(`${API_URL}/${reservationId}`);
        return response.data;
    },
};
