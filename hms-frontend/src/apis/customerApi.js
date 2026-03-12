import axiosInstance from './axiosConfig';

export const customerApi = {
    getBookingHistory: async () => {
        const response = await axiosInstance.get('/customer/bookings/history');
        return response.data;
    },

    getBookingDetails: async (bookingId) => {
        const response = await axiosInstance.get(`/customer/bookings/${bookingId}`);
        return response.data;
    },

    cancelBooking: async (bookingId) => {
        const response = await axiosInstance.post(`/customer/bookings/${bookingId}/cancel`);
        return response.data;
    },

    getUpcomingBookings: async () => {
        const response = await axiosInstance.get('/customer/bookings/upcoming');
        return response.data;
    }
};