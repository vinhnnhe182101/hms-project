import axiosInstance from '../axiosConfig';

const API_URL = '/v1/services';

const categoryLabelToEnum = {
    Spa: 'SPA',
    Minibar: 'MINIBAR',
    'F&B': 'F_AND_B',
};

const categoryEnumToLabel = {
    SPA: 'Spa',
    MINIBAR: 'Minibar',
    F_AND_B: 'F&B',
};

function toUiService(item) {
    return {
        id: item.id,
        name: item.name,
        serviceCategory: categoryEnumToLabel[item.serviceCategory] || item.serviceCategory,
        price: Number(item.price || 0),
        isActive: item.isActive,
    };
}

export const adminServiceApi = {
    getServices: async (params = {}) => {
        const response = await axiosInstance.get(API_URL, {
            params: {
                page: 0,
                size: 200,
                sort: 'id,desc',
                ...params,
            },
        });

        const payload = response.data;
        const content = Array.isArray(payload?.content) ? payload.content : [];
        return content.map(toUiService);
    },

    createService: async (data) => {
        const payload = {
            name: data.name,
            serviceCategory: categoryLabelToEnum[data.serviceCategory] || data.serviceCategory,
            price: Number(data.price),
        };

        const response = await axiosInstance.post(API_URL, payload);
        return toUiService(response.data);
    },

    updateService: async (id, data) => {
        const payload = {
            name: data.name,
            serviceCategory: categoryLabelToEnum[data.serviceCategory] || data.serviceCategory,
            price: Number(data.price),
        };

        const response = await axiosInstance.put(`${API_URL}/${id}`, payload);
        return toUiService(response.data);
    },

    deleteService: async (id) => {
        await axiosInstance.delete(`${API_URL}/${id}`);
    },
};
