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
		return data;
	},

	/**
	 * Get reservation details by ID
	 */
	getReservationById: async (id) => {
		const {data} = await axiosInstance.get(`/v1/reservations/${id}`);
		return data;
	},

	/**
	 * Hàm tạo một đặt phòng mới dựa trên yêu cầu đặt phòng
	 *
	 * @param {ReservationRequest} reservationRequest - Đối tượng chứa thông tin yêu cầu đặt phòng, bao gồm thông tin khách hàng, phòng đã chọn, ngày check-in/check-out, v.v.
	 * @return {Promise<ReservationResponse>} - Một Promise trả về đối tượng chứa thông tin chi tiết của đặt phòng vừa được tạo
	 */
	makeReservation: async (reservationRequest) => {
		const {data} = await axiosInstance.post("/v1/reservations", reservationRequest);
		return data;
	},

	/**
	 * Perform check-in for a reservation
	 */
	checkInReservation: async (reservationId, checkInRequest) => {
		const {data} = await axiosInstance.post(`/v1/reservations/${reservationId}/check-in`, checkInRequest);
		return data;
	},
};
