import {useStaffLayout} from "../../hooks/staff/layout/use-staff-layout.jsx";
import {BedDouble, CalendarDays, CreditCard, LogIn, LogOut, Users} from "lucide-react";
import {NavLink} from "react-router-dom";

const navItems = [
    {to: '/bookings', label: 'Đặt phòng', icon: CalendarDays},
    {to: '/rooms', label: 'Phòng', icon: BedDouble},
    {to: '/occupied-rooms', label: 'Phòng đang ở', icon: Users},
    {to: '/check-in', label: 'Check-in', icon: LogIn},
    {to: '/check-out', label: 'Check-out', icon: LogOut},
    {to: '/payment', label: 'Thanh toán', icon: CreditCard},
];

export const StaffHeader = () => {
    const {isMobileOpen, setMobileOpen} = useStaffLayout();

    return (
            <header className="sticky top-0 z-50 border-b bg-card shadow-card">
                <div className="flex h-16 items-center px-4 lg:px-8">
                    <button
                            className="mr-3 rounded-md p-2 hover:bg-accent lg:hidden"
                            onClick={() => setMobileOpen(!isMobileOpen)}
                            aria-label="Menu"
                    >
                        {isMobileOpen ? <X className="h-5 w-5"/> : <Menu className="h-5 w-5"/>}
                    </button>

                    <div className="flex items-center gap-2">
                        <Hotel className="h-7 w-7 text-primary"/>
                        <span className="text-lg font-semibold text-foreground">HMS</span>
                        <span className="hidden text-sm text-muted-foreground sm:inline">| Staff Portal</span>
                    </div>

                    {/* Desktop Nav */}
                    <nav className="ml-8 hidden items-center gap-1 lg:flex">
                        {navItems.map((item) => {
                            const Icon = item.icon;

                            return (
                                    <NavLink
                                            key={item.to}
                                            to={item.to}
                                            className={({isActive}) =>
                                                    `flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium transition-colors ${
                                                            isActive
                                                                    ? 'bg-accent text-accent-foreground'
                                                                    : 'text-muted-foreground hover:bg-secondary hover:text-foreground'
                                                    }`
                                            }
                                    >
                                        <Icon className="h-4 w-4"/>
                                        {item.label}
                                    </NavLink>
                            );
                        })}
                    </nav>

                    <div className="ml-auto flex items-center gap-3">
                        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-medium text-primary-foreground">
                            S
                        </div>
                    </div>
                </div>

                {/* Mobile Nav */}
                {isMobileOpen && (
                        <nav className="border-t bg-card px-4 py-3 lg:hidden">
                            <div className="flex flex-col gap-1">
                                {navItems.map((item) => {
                                    const Icon = item.icon;

                                    return (
                                            <NavLink
                                                    key={item.to}
                                                    to={item.to}
                                                    onClick={() => setMobileOpen(false)}
                                                    className={({isActive}) =>
                                                            `flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium transition-colors ${
                                                                    isActive
                                                                            ? 'bg-accent text-accent-foreground'
                                                                            : 'text-muted-foreground hover:bg-secondary hover:text-foreground'
                                                            }`
                                                    }
                                            >
                                                <Icon className="h-4 w-4"/>
                                                {item.label}
                                            </NavLink>
                                    );
                                })}
                            </div>
                        </nav>
                )}
            </header>
    );
}
