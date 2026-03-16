import axiosInstance from "../axiosConfig";

export const reservationApi = {
	/**
	 * Hàm lấy danh sách đặt phòng dựa trên các tham số tìm kiếm
	 *
	 * @param {ReservationSearchParams} searchParams - Các tham số tìm kiếm để lọc danh sách đặt phòng
	 * @return {Promise<PageResponse<ReservationResponse>>} - Một Promise trả về một đối tượng chứa danh sách đặt phòng và thông tin phân trang
	 */
	getReservations: async (searchParams) => {
		const {data} = await axiosInstance.get("/v1/reservations", {
			params: searchParams,
		});
		console.log("searchParams", searchParams);
		console.log("data", data);
		return data;
	},
};
