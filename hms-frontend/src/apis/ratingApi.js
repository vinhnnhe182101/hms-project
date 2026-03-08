import axios from 'axios';

const API_URL = 'http://localhost:8080/api/ratings';

export const getRoomClassRatings = async (roomClassId, page = 0, size = 5, rating = null) => {
    try {
        const params = {
            page,
            size
        };

        if (rating !== null && rating !== 'all') {
            params.rating = rating;
        }

        const response = await axios.get(`${API_URL}/room-class/${roomClassId}`, { params });
        return response.data;
    } catch (error) {
        console.error('Error fetching ratings:', error);
        throw error;
    }
};

export const getLatestRatings = async (page = 0, size = 3) => {
    try {
        const response = await axios.get(`${API_URL}/latest`, {
            params: { page, size }
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching latest ratings:', error);
        throw error;
    }
};
