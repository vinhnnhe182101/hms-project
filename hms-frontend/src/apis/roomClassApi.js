import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * GET /api/v1/room-classes?page=0&size=10
 * Lấy danh sách loại phòng có phân trang.
 *
 * Response: {
 *   data: RoomClassResponse[],  // id, name, standardCapacity, basePrice, primaryImage, totalRooms
 *   currentPage, totalItems, totalPages, pageSize, isLast
 * }
 */
export const getRoomClassList = async (page = 0, size = 9) => {
    try {
        const response = await api.get('/room-classes', { params: { page, size } });
        return response.data;
    } catch (error) {
        console.error('Error fetching room class list:', error);
        throw error;
    }
};

/**
 * GET /api/v1/room-classes/{id}
 * Lấy chi tiết một loại phòng.
 *
 * Response: RoomClassDetailResponse {
 *   id, name, standardCapacity, maxCapacity, basePrice, extraPersonFee,
 *   totalRooms, images: RoomImgResponse[], assets: AssetResponse[]
 * }
 */
export const getRoomClassDetail = async (id) => {
    try {
        const response = await api.get(`/room-classes/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching room class detail (id=${id}):`, error);
        throw error;
    }
};

/**
 * GET /api/v1/room-classes/{id}/others
 * Lấy danh sách các loại phòng khác (trừ id hiện tại).
 * Dùng cho phần gợi ý "Loại phòng khác" trên trang detail.
 *
 * Response: RoomClassResponse[]
 */
export const getOtherRoomClasses = async (id) => {
    try {
        const response = await api.get(`/room-classes/${id}/others`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching other room classes (exclude id=${id}):`, error);
        throw error;
    }
};
