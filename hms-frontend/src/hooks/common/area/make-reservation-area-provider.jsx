import { createAreaContext } from "./area-factory";

/**
 * @type {CustomerRequest}
 */
const customerRequest = {
    identityCard: "",
    fullName: "",
    phoneNumber: "",
    email: "",
};

/**
 * @returns {RoomClassQuantityRequest}
 */
export const getDefaultRoomClassQuantity = () => {
    return {
        _key: Date.now(),
        roomClassId: null,
        numberOfPeople: 1,
    };
};

/**
 * @type {ReservationRequest}
 */
const reservationRequest = {
    checkInDate: null,
    checkOutDate: null,
    numberOfMembers: 1,
    customerRequest: customerRequest,
    roomClassQuantities: [getDefaultRoomClassQuantity()],
    note: "",
};

export const { useArea: useMakeReservationArea, Provider: MakeReservationAreaProvider } =
    createAreaContext(reservationRequest);
