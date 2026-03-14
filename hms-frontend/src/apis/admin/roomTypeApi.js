import axiosInstance from '../axiosConfig';

const API_URL = '/v1/room-types';

function toUiRoomType(item) {
    return {
        id: item.id,
        name: item.typeName,
        standardOccupancy: Number(item.standardOccupancy || 0),
        maxOccupancy: Number(item.maxOccupancy || 0),
        baseRate: Number(item.baseRatePerNight || 0),
    };
}

function toApiPayload(data) {
    return {
        typeName: data.name,
        standardOccupancy: Number(data.standardOccupancy),
        maxOccupancy: Number(data.maxOccupancy),
        baseRatePerNight: Number(data.baseRate),
    };
}

export const roomTypeApi = {
    getRoomTypes: async (params = {}) => {
        const response = await axiosInstance.get(API_URL, {
            params: {
                page: 0,
                size: 200,
                ...params,
            },
        });

        const payload = response.data;
        const content = Array.isArray(payload?.content) ? payload.content : [];
        return content.map(toUiRoomType);
    },

    createRoomType: async (data) => {
        const response = await axiosInstance.post(API_URL, toApiPayload(data));
        return toUiRoomType(response.data);
    },

    updateRoomType: async (id, data) => {
        const response = await axiosInstance.put(`${API_URL}/${id}`, toApiPayload(data));
        return toUiRoomType(response.data);
    },

    deleteRoomType: async (id) => {
        await axiosInstance.delete(`${API_URL}/${id}`);
    },
};
