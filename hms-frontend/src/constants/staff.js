import {BedDouble, CalendarDays, CreditCard, LogIn, LogOut, Users} from "lucide-react";

export const NAV_ITEMS = [
	{to: "/bookings", label: "Đặt phòng", icon: CalendarDays},
	{to: "/rooms", label: "Phòng", icon: BedDouble},
	{to: "/occupied-rooms", label: "Phòng đang ở", icon: Users},
	{to: "/check-in", label: "Check-in", icon: LogIn},
	{to: "/check-out", label: "Check-out", icon: LogOut},
	{to: "/payment", label: "Thanh toán", icon: CreditCard},
];