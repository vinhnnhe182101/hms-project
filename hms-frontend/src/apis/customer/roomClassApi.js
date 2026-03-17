import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});


export const getRoomClassList = async (page = 0, size = 9, checkIn = null, checkOut = null, sortBy = null) => {
    try {
        const params = { page, size };
        if (checkIn) params.checkIn = checkIn;
        if (checkOut) params.checkOut = checkOut;
        if (sortBy) params.sortBy = sortBy;
        const response = await api.get('/home/room-classes', { params });
        return response.data;
    } catch (error) {
        console.error('Error fetching room class list:', error);
        throw error;
    }
};

export const getRoomClassDetail = async (id) => {
    try {
        const response = await api.get(`/home/room-classes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching room class detail (id=${id}):`, error);
        throw error;
    }
};


export const getOtherRoomClasses = async (id) => {
    try {
        const response = await api.get(`/home/room-classes/${id}/others`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching other room classes (exclude id=${id}):`, error);
        throw error;
    }
};
