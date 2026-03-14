import axiosInstance from '../axiosConfig';

const ROOMS_API_URL = '/v1/rooms';

const roomStatusLabelMap = {
    AVAILABLE: 'Available',
    RESERVED: 'Reserved',
    CLEAN: 'Clean',
    DIRTY: 'Dirty',
    OCCUPIED: 'Occupied',
    MAINTENANCE: 'Maintenance',
};

const roomStatusValueMap = {
    Available: 'AVAILABLE',
    Reserved: 'RESERVED',
    Clean: 'CLEAN',
    Dirty: 'DIRTY',
    Occupied: 'OCCUPIED',
    Maintenance: 'MAINTENANCE',
};

function toUiRoom(item) {
    return {
        id: item.id,
        roomNumber: item.roomNumber,
        roomClassId: item.roomClassId,
        roomClassName: item.roomClassName,
        status: roomStatusLabelMap[item.status] || item.status,
        description: item.description || '',
        isActive: item.isActive,
    };
}

export const roomApi = {
    getRooms: async (params = {}) => {
        const response = await axiosInstance.get(`${ROOMS_API_URL}/search`, {
            params: {
                page: 0,
                size: 200,
                sort: 'id,desc',
                ...params,
            },
        });

        const content = Array.isArray(response.data?.content) ? response.data.content : [];
        return content.map(toUiRoom);
    },

    getRoomClasses: async () => {
        // Derive room classes from rooms data to avoid dependency on heavy room-class endpoint.
        const rooms = await roomApi.getRooms();
        const map = new Map();
        rooms.forEach((room) => {
            if (room.roomClassId != null && room.roomClassName) {
                map.set(String(room.roomClassId), room.roomClassName);
            }
        });
        return Array.from(map.entries()).map(([value, label]) => ({ value, label }));
    },

    createRoom: async (data) => {
        const payload = {
            roomNumber: data.roomNumber,
            roomClassId: Number(data.roomClassId),
            status: roomStatusValueMap[data.status] || data.status,
            description: data.description || null,
        };

        const response = await axiosInstance.post(ROOMS_API_URL, payload);
        return toUiRoom(response.data);
    },

    updateRoom: async (id, data) => {
        const payload = {
            roomNumber: data.roomNumber,
            roomClassId: Number(data.roomClassId),
            status: roomStatusValueMap[data.status] || data.status,
            description: data.description || null,
        };

        const response = await axiosInstance.put(`${ROOMS_API_URL}/${id}`, payload);
        return toUiRoom(response.data);
    },

    deleteRoom: async (id) => {
        await axiosInstance.delete(`${ROOMS_API_URL}/${id}`);
    },
};
