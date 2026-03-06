import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const searchRooms = async (params) => {
    try {
        const response = await api.get('/rooms', { params });
        return response.data;
    } catch (error) {
        console.error("Error searching rooms:", error);
        throw error;
    }
};

export const getRoomById = async (id) => {
    try {
        const response = await api.get(`/rooms/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error getting room by id:", error);
        throw error;
    }
};

export const checkRoomNumberExists = async (roomNumber) => {
    try {
        const response = await api.get(`/rooms/exists/${roomNumber}`);
        return response.data;
    } catch (error) {
        console.error("Error checking room number:", error);
        throw error;
    }
};

export const getAllRoomTypes = async () => {
    try {
        const response = await api.get('/roomtypes');
        return response.data;
    } catch (error) {
        console.error("Error getting room types:", error);
        throw error;
    }
};

export const getSimilarRooms = async (roomId, limit = 4) => {
    try {
        const response = await api.get(`/roomdetails/${roomId}/similar`, {
            params: { limit }
        });
        return response.data;
    } catch (error) {
        console.error("Error getting similar rooms:", error);
        throw error;
    }
};
