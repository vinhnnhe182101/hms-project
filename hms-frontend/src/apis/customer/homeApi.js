import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/home';

export const getHomeData = async () => {
    try {
        const response = await axios.get(`${API_URL}`);
        return response.data;
    } catch (error) {
        console.error('Error fetching home data:', error);
        throw error;
    }
};
