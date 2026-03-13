import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/home/service-bookings';

export const getActiveAllocations = async (customerId) => {
    try {
        const response = await axios.get(`${API_URL}/allocations`, { params: { customerId } });
        return response.data;
    } catch (error) {
        console.error('Error fetching allocations:', error);
        throw error;
    }
};

export const createServiceBookings = async (bookingData) => {
    try {
        const response = await axios.post(API_URL, bookingData);
        return response.data;
    } catch (error) {
        console.error('Error creating service bookings:', error);
        throw error;
    }
};
