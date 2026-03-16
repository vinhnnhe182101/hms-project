// src/apis/customerApi.js
import axiosInstance from './axiosConfig';

export const customerApi = {
    // Bookings
    getBookingHistory: async () => {
        const response = await axiosInstance.get('/customer/bookings/history');
        return response.data;
    },

    getBookingDetails: async (bookingId) => {
        const response = await axiosInstance.get(`/customer/bookings/${bookingId}`);
        return response.data;
    },

    cancelBooking: async (bookingId, data) => {
        const response = await axiosInstance.post(`/customer/bookings/${bookingId}/cancel`, data);
        return response.data;
    },

    checkBookingReviewed: async (bookingId) => {
        const response = await axiosInstance.get(`/customer/reviews/booking/${bookingId}`);
        return response.data;
    },

    // Submit review
    submitReview: async (data) => {
        const response = await axiosInstance.post('/customer/reviews', data);
        return response.data;
    },

    // Get my reviews
    getMyReviews: async () => {
        const response = await axiosInstance.get('/customer/reviews');
        return response.data;
    },

    updateReview: async (reviewId, data) => {
        const response = await axiosInstance.put(`/customer/reviews/${reviewId}`, data);
        return response.data;
    },

    deleteReview: async (reviewId) => {
        const response = await axiosInstance.delete(`/customer/reviews/${reviewId}`);
        return response.data;
    }
};