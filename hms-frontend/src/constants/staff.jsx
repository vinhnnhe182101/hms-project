import {BedDouble, CalendarDays, CreditCard, LogIn, LogOut, Users} from "lucide-react";

/**
 * @type {NavItemType[]}
 */
export const NAV_ITEMS = [
	{to: "/staff/bookings", label: "Đặt phòng", icon: CalendarDays, element: <div>Đặt phòng</div>},
	{to: "/staff/rooms", label: "Phòng", icon: BedDouble, element: <div>Danh sách phòng</div>},
	{to: "/staff/occupied-rooms", label: "Phòng đang ở", icon: Users, element: <div>Danh sách phòng ở</div>},
	{to: "/staff/check-in", label: "Check-in", icon: LogIn, element: <div>Danh sách check-in</div>},
	{to: "/staff/check-out", label: "Check-out", icon: LogOut, element: <div>Danh sách check-out</div>},
	{to: "/staff/payment", label: "Thanh toán", icon: CreditCard, element: <div>Danh sách payment</div>},
];