export const dateUtils = {
	/**
	 * Tính số ngày giữa 2 date, làm tròn lên và trả về tối thiểu là 0
	 *
	 * @param {string | Date} from
	 * @param {string | Date} to
	 * @returns {number}
	 */
	dateDiff: (from, to) => {
		if (!from || !to) return 0;
		if (typeof from === "string") from = new Date(from);
		if (typeof to === "string") to = new Date(to);
		return Math.max(0, Math.round((to - from) / 86400000));
	},
};
