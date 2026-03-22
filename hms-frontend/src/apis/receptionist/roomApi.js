import axiosInstance from "../axiosConfig";

export const roomApi = {
	/**
	 * Hàm lấy hạng phòng và số phòng còn trống dựa trên ngày check-in và check-out
	 *
	 * @param {string} checkInDate - Ngày check-in (định dạng YYYY-MM-DD)
	 * @param {string} checkOutDate - Ngày check-out (định dạng YYYY-MM-DD)
	 * @return {Promise<RoomClassAvailabilityResponse[]>} - Một Promise trả về một mảng đối tượng chứa thông tin về hạng phòng và số phòng còn trống
	 */
	getAvailableRooms: async (checkInDate, checkOutDate) => {
		const checkInTime = new Date(checkInDate).getDate();
		const checkOutTime = new Date(checkOutDate).getDate();
		const {data} = await axiosInstance.get("/v1/rooms/available", {
			params: {
				checkInDate: checkInTime,
				checkOutDate: checkOutTime,
			},
		});

		return data["roomClassAvailabilityResponses"];
	},
};
