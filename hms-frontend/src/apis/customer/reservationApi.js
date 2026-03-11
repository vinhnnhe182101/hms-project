import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/home/reservations';

export const createBooking = async (bookingData) => {
    try {
        const response = await axios.post(`${API_URL}/booking`, bookingData);
        return response.data;
    } catch (error) {
        console.error('Error creating booking:', error);
        throw error;
    }
};
