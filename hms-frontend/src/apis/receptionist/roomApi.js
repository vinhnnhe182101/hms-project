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
	}
}