import {createListContext} from "./list-factory";

/**
 * @type {ListFactoryType<PageResponse<ReservationResponse>, ReservationSearchParams>}
 */
export const {useList: useReservationList, Provider: ReservationProvider} = createListContext({});
