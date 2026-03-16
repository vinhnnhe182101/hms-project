import { BedDouble, CalendarDays, CreditCard, LogIn, LogOut, Users } from "lucide-react";
import { ReservationListPage } from "../pages/staff/ReservationListPage.jsx";

/** @type {Object.<string, RouteItemType>} */
const STAFF_MAP_ROUTES = {
    RESERVATIONS: {
        path: "/staff/reservations",
        label: "Reservation",
        icon: CalendarDays,
        element: <ReservationListPage />,
        inNavbar: true,
    },
    MAKE_RESERVATION: {
        path: "/staff/reservations/make",
        element: <div> Tạo đặt phòng</div>,
    },
    ROOMS: {
        path: "/staff/rooms",
        label: "Room",
        icon: BedDouble,
        element: <div>Danh sách phòng</div>,
        inNavbar: true,
    },
    OCCUPIED_ROOMS: {
        path: "/staff/occupied-rooms",
        label: "Occupied Room",
        icon: Users,
        element: <div>Danh sách phòng ở</div>,
        inNavbar: true,
    },
    CHECK_IN: {
        path: "/staff/check-in",
        label: "Check-in",
        icon: LogIn,
        element: <div>Danh sách check-in</div>,
        inNavbar: true,
    },
    CHECK_OUT: {
        path: "/staff/check-out",
        label: "Check-out",
        icon: LogOut,
        element: <div>Danh sách check-out</div>,
        inNavbar: true,
    },
    PAYMENT: {
        path: "/staff/payment",
        label: "Payment",
        icon: CreditCard,
        element: <div>Danh sách payment</div>,
        inNavbar: true,
    },
};

/**
 * @type {RouteItemType[]}
 */
const STAFF_ROUTES = Object.values(STAFF_MAP_ROUTES);

const STAFF_NAVBAR_ITEMS = STAFF_ROUTES.filter((item) => item.inNavbar);

export { STAFF_MAP_ROUTES, STAFF_ROUTES, STAFF_NAVBAR_ITEMS };
