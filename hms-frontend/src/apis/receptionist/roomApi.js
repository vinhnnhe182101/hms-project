import axiosInstance from "../axiosConfig.js";

export const roomApi = {
	/**
	 *
	 * @param {RoomSearchParams} roomSearchParams
	 * @returns {Promise<PageResponse<RoomResponse>>}
	 */
	getRooms: async (roomSearchParams) => {
		const {data} = await axiosInstance.get(
				"/v1/rooms/search",
				{
					params: roomSearchParams
				}
		);
		return data;
	},

	/**
	 * Get available physical rooms for manual assignment during check-in
	 */
	getAvailableRoomsForAssignment: async (roomClassId, checkInTime, checkOutTime) => {
		const {data} = await axiosInstance.get(`/v1/rooms/available-for-assignment/by-room-class`, {
			params: {
				roomClassId,
				checkInDate: checkInTime,
				checkOutDate: checkOutTime,
			}
		});
		return data.availableRooms;
	}
}