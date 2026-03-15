import {createBrowserRouter} from "react-router-dom";
import {CustomerLayout} from "../layouts/customer/CustomerLayout.jsx";
import {AdminLayout} from "../layouts/admin/AdminLayout.jsx";
import {HousekeepingLayout} from "../layouts/housekeeping/HousekeepingLayout.jsx";
import {ProtectedRoute} from "./ProtectedRoute.jsx";
import HomePage from "../pages/customer/HomePage.jsx";
import RoomsPage from "../pages/customer/RoomsPage.jsx";
import RoomDetailPage from "../pages/customer/RoomDetailPage.jsx";
import ServicesPage from "../pages/customer/ServicesPage.jsx";
import BookingPage from "../pages/customer/BookingPage.jsx";
import CheckoutPage from "../pages/customer/CheckoutPage.jsx";
import ServiceCheckoutPage from "../pages/customer/ServiceCheckoutPage.jsx";
import BookingHistoryPage from "../pages/customer/BookingHistoryPage.jsx";
import PaymentCallbackPage from "../pages/customer/PaymentCallbackPage.jsx";
import LoginPage from "../pages/auth/LoginPage.jsx";
import RegisterPage from "../pages/auth/RegisterPage.jsx";
import AdminDashboardPage from "../pages/admin/DashboardPage.jsx";
import RoomManagementPage from "../pages/admin/RoomManagementPage.jsx";
import RoomTypesPage from "../pages/admin/RoomTypesPage.jsx";
import ServiceManagementPage from "../pages/admin/ServiceManagementPage.jsx";
import {AuthLayout} from "../layouts/AuthLayout.jsx";
import OAuth2RedirectPage from "../pages/auth/OAuth2RedirectPage.jsx";
import UnauthorizedPage from "../pages/error/UnauthorizedPage.jsx";
import NotFoundPage from "../pages/error/NotFoundPage.jsx";
import RecepDashboardPage from "../pages/receptionist/Dashboard.jsx";
import {StaffLayout} from "../layouts/staff/StaffLayout.jsx";
import StaffManagementPage from "../pages/admin/StaffManagementPage.jsx";
import UserManagementPage from "../pages/admin/UserManagementPage.jsx";
import {STAFF_ROUTES} from "../constants/staff.jsx";

// Import Housekeeping Pages
import DashboardPage from '../pages/housekeeping/DashboardPage.jsx';
import TasksPage from '../pages/housekeeping/TasksPage.jsx';
import TaskDetailPage from '../pages/housekeeping/TaskDetailPage.jsx';
import SchedulePage from '../pages/housekeeping/SchedulePage.jsx';
import ReportsPage from '../pages/housekeeping/ReportsPage.jsx';

import {NAV_ITEMS} from "../constants/staff.jsx";
import ScheduleManagementPage from "../pages/admin/ScheduleManagementPage.jsx";
import TaskManagementPage from "../pages/admin/TaskManagementPage.jsx";
import PaymentManagementPage from "../pages/admin/PaymentManagementPage.jsx";
export const router = createBrowserRouter([
    // ===== ROLE-SPECIFIC ROUTES (Ưu tiên cao nhất) =====
    {
        path: "/",
        element: <CustomerLayout/>,
        children: [
            {index: true, element: <HomePage/>},
            {path: "rooms", element: <RoomsPage/>},
            {path: "rooms/:id", element: <RoomDetailPage/>},
            {path: "services", element: <ServicesPage/>},
            {
                path: "services/checkout",
                element: (
                        <ProtectedRoute>
                            <ServiceCheckoutPage/>
                        </ProtectedRoute>
                ),
            },
            {
                path: "booking",
                element: (
                        <ProtectedRoute>
                            <BookingPage/>
                        </ProtectedRoute>
                ),
            },
            {
                path: "booking/checkout",
                element: (
                        <ProtectedRoute>
                            <CheckoutPage/>
                        </ProtectedRoute>
                ),
            },
            {path: "history", element: <BookingHistoryPage/>},
            {path: "payment/vnpay-callback", element: <PaymentCallbackPage/>},
        ],
    },
    {
        path: "/user",
        element: (
                <ProtectedRoute>
                    <CustomerLayout/>
                </ProtectedRoute>
        ),
        children: [
            {index: true, element: <HomePage/>},
            {path: "rooms", element: <RoomsPage/>},
            {path: "rooms/:id", element: <RoomDetailPage/>},
            {path: "services", element: <ServicesPage/>},
            {path: "services/checkout", element: <ServiceCheckoutPage/>},
            {path: "booking", element: <BookingPage/>},
            {path: "booking/checkout", element: <CheckoutPage/>},
            {path: "history", element: <BookingHistoryPage/>},
        ],
    },
    {
        path: "/",
        element: <AuthLayout/>,
        children: [
            {path: "login", element: <LoginPage/>},
            {path: "register", element: <RegisterPage/>},
            {path: "forgot-password", element: <div>Forgot Password</div>},
        ],
    },
    {
        path: "/auth",
        element: <AuthLayout/>,
        children: [
            {path: "login", element: <LoginPage/>},
            {path: "register", element: <RegisterPage/>},
            {path: "forgot-password", element: <div>Forgot Password</div>},
        ],
    },

    {
        path: "/booking",
        element: (
                <ProtectedRoute>
                    <CustomerLayout/>
                </ProtectedRoute>
        ),
        children: [],
    },
    {
        path: "/customer",
        element: (
                <ProtectedRoute>
                    <CustomerLayout/>
                </ProtectedRoute>
        ),
        children: [
            {path: "bookings", element: <BookingHistoryPage/>},
            {path: "bookings/:id", element: null},
        ],
    },
    {
        path: "/admin",
        element: (
            <ProtectedRoute requiredRole="ADMIN">
                <AdminLayout/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <AdminDashboardPage/>},
            {path: "rooms", element: <RoomManagementPage/>},
            {path: "rooms/types", element: <RoomTypesPage/>},
            {path: "rooms/service", element: <ServiceManagementPage/>},
            {path: "staff", element: <StaffManagementPage/>},
            {path: "reservations", element: <div>Reservations Management</div>},
            {path: "customers", element: <UserManagementPage/>},
            {path: 'schedules', element: <ScheduleManagementPage />},
            {path: 'housekeeping-tasks', element: <TaskManagementPage />},
            {path: 'payments', element: <PaymentManagementPage />},
            {path: "payments", element: <div>Payments Management</div>},
            {path: "reports", element: <div>Reports Management</div>},
            {path: "settings", element: <div>Settings</div>},
        ],
    },
    {
        path: '/housekeeping',
        element: (
            <ProtectedRoute requiredRole="HOUSEKEEPING">
                <HousekeepingLayout/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <MobileTasksPage/>},
            {path: "tasks", element: <MobileTasksPage/>},
        ],
    },
    {
        path: "/receptionist",
        element: (
            <ProtectedRoute requiredRole="RECEPTIONIST">
                <RecepDashboardPage/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <RecepDashboardPage/>},
            {path: 'checkin', element: <div>Check-in</div>},
            {path: 'checkout', element: <div>Check-out</div>},
            {path: 'reservations', element: <div>Reservations</div>},
        ],
    },
    {
        path: "/staff",
        element: (
            <ProtectedRoute requiredRole="STAFF">
                <StaffLayout/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <div>Staff Dashboard</div>},
            // Map tự động từ STAFF_ROUTES sang định dạng object của Router
            ...STAFF_ROUTES.map((item) => {
                const relativePath = item.path
                        .replace(/^\//, "") // Bỏ dấu / ở đầu (nếu có)
                        .replace(/^staff\//, "") // Bỏ chữ staff/ ở đầu
                        .replace(/^\//, "");
                return {
                    path: relativePath,
                    element: item.element,
                };
            }),
        ],
    },

    // ===== CUSTOMER ROUTES (Public & Protected) =====
    {
        path: '/user',
        element: <CustomerLayout/>,
        children: [
            {index: true, element: <HomePage/>},
            {path: 'rooms', element: <RoomsPage/>},
            {path: 'rooms/:id', element: <RoomDetailPage/>},
            {path: 'services', element: <ServicesPage/>},
            {path: 'services/checkout', element: <ProtectedRoute><ServiceCheckoutPage/></ProtectedRoute>},
            {path: 'booking', element: <ProtectedRoute><BookingPage/></ProtectedRoute>},
            {path: 'booking/checkout', element: <ProtectedRoute><CheckoutPage/></ProtectedRoute>},
            {path: 'history', element: <ProtectedRoute><BookingHistoryPage/></ProtectedRoute>},
            {path: 'payment/vnpay-callback', element: <PaymentCallbackPage/>},
        ],
    },
    {
        path: '/',
        element: <Navigate to="/user" replace />,
    },
    // ===== AUTH LAYOUT RIÊNG (nếu muốn layout khác cho auth) =====
    {
        path: '/',
        element: <AuthLayout/>,
        children: [
            {path: 'login', element: <LoginPage/>},
            {path: 'register', element: <RegisterPage/>},
            {path: 'forgot-password', element: <div>Forgot Password</div>},
        ],
    },

    // ===== SPECIAL ROUTES =====
    {
        path: "/oauth2/redirect",
        element: <OAuth2RedirectPage/>,
    },
    {
        path: "/unauthorized",
        element: <UnauthorizedPage/>,
    },
    {
        path: "*",
        element: <NotFoundPage/>,
    },
]);
