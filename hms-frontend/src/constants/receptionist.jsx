import {BedDouble, CalendarDays, CreditCard, LogIn, LogOut, Users} from "lucide-react";
import {ReservationListPage} from "../pages/receptionist/ReservationListPage.jsx";
import {MakeReservationPage} from "../pages/receptionist/MakeReservationPage.jsx";

/** @type {Object.<string, RouteItemType>} */
const RECEPTIONIST_MAP_ROUTES = {
    RESERVATIONS: {
        path: "/receptionist/reservations",
        label: "Reservation",
        icon: CalendarDays,
        element: <ReservationListPage/>,
        inNavbar: true,
    },
    MAKE_RESERVATION: {
        path: "/receptionist/reservations/make",
        element: <MakeReservationPage/>,
    },
    ROOMS: {
        path: "/receptionist/rooms",
        label: "Room",
        icon: BedDouble,
        element: <div>Danh sách phòng</div>,
        inNavbar: true,
    },
    OCCUPIED_ROOMS: {
        path: "/receptionist/occupied-rooms",
        label: "Occupied Room",
        icon: Users,
        element: <div>Danh sách phòng ở</div>,
        inNavbar: true,
    },
    CHECK_IN: {
        path: "/receptionist/check-in",
        label: "Check-in",
        icon: LogIn,
        element: <div>Danh sách check-in</div>,
        inNavbar: true,
    },
    CHECK_OUT: {
        path: "/receptionist/check-out",
        label: "Check-out",
        icon: LogOut,
        element: <div>Danh sách check-out</div>,
        inNavbar: true,
    },
    PAYMENT: {
        path: "/receptionist/payment",
        label: "Payment",
        icon: CreditCard,
        element: <div>Danh sách payment</div>,
        inNavbar: true,
    },
};

/**
 * @type {RouteItemType[]}
 */
const RECEPTIONIST_ROUTES = Object.values(RECEPTIONIST_MAP_ROUTES);

const RECEPTIONIST_NAVBAR_ITEMS = RECEPTIONIST_ROUTES.filter((item) => item.inNavbar);

export {RECEPTIONIST_MAP_ROUTES, RECEPTIONIST_ROUTES, RECEPTIONIST_NAVBAR_ITEMS};