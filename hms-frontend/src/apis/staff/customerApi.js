import axiosInstance from "../axiosConfig";

export const customerApi = {
	/**
	 * Tìm kiếm khách hàng theo số CMND/CCCD/Hộ chiếu
	 * @param {string} identityCard - Số CMND/CCCD/Hộ chiếu của khách hàng
	 * @return {Promise<CustomerResponse>} Thông tin khách hàng nếu tìm thấy, hoặc null nếu không tìm thấy
	 */
	getCustomerByIdentityCard: async (identityCard) => {
		const { data } = await axiosInstance.get(
			`/v1/customers/search?identityCard=${encodeURIComponent(identityCard)}`,
		);

		return data;
	},
};
