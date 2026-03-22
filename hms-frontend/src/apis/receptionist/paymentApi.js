import axiosInstance from "../axiosConfig.js";

export const paymentApi = {
	/**
	 * Hàm lấy tất cả Payment
	 *
	 * @param {PaymentSearchParams} searchParams
	 * @returns {Promise<PageResponse<PaymentTransactionResponse>>}
	 */
	getAll: (searchParams) => {
		const {data} = axiosInstance.get("/v1/admin/transactions", {
			params: searchParams
		});

		return data;
	}
}