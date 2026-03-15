import axiosInstance from '../axiosConfig';

export const shiftApi = {
    getAllShifts: async () => {
        const response = await axiosInstance.get('/v1/admin/shifts');
        return response.data;
    }
};