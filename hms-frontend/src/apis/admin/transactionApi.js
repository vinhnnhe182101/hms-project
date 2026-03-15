import axiosInstance from '../axiosConfig';

const API_URL = '/v1/admin/transactions';

export const transactionApi = {
    getAllTransactions: async (params) => {
        const response = await axiosInstance.get(API_URL, { params });
        return response.data;
    },
    getTransactionById: async (id) => {
        const response = await axiosInstance.get(`${API_URL}/${id}`);
        return response.data;
    }
};