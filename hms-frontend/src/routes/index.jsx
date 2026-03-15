import { createBrowserRouter, Navigate } from "react-router-dom";
import { CustomerLayout } from "../layouts/customer/CustomerLayout.jsx";
import { AdminLayout } from "../layouts/admin/AdminLayout.jsx";
import { HousekeepingLayout } from "../layouts/housekeeping/HousekeepingLayout.jsx";
import { ProtectedRoute } from "./ProtectedRoute.jsx";
import { AuthLayout } from "../layouts/AuthLayout.jsx";
import { StaffLayout } from "../layouts/staff/StaffLayout.jsx";
import { STAFF_ROUTES } from "../constants/staff.jsx";

// Import Pages
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
import OAuth2RedirectPage from "../pages/auth/OAuth2RedirectPage.jsx";
import AdminDashboardPage from "../pages/admin/DashboardPage.jsx";
import RoomManagementPage from "../pages/admin/RoomManagementPage.jsx";
import RoomTypesPage from "../pages/admin/RoomTypesPage.jsx";
import ServiceManagementPage from "../pages/admin/ServiceManagementPage.jsx";
import StaffManagementPage from "../pages/admin/StaffManagementPage.jsx";
import UserManagementPage from "../pages/admin/UserManagementPage.jsx";
import ScheduleManagementPage from "../pages/admin/ScheduleManagementPage.jsx";
import TaskManagementPage from "../pages/admin/TaskManagementPage.jsx";
import PaymentManagementPage from "../pages/admin/PaymentManagementPage.jsx";
import RecepDashboardPage from "../pages/receptionist/Dashboard.jsx";
import UnauthorizedPage from "../pages/error/UnauthorizedPage.jsx";
import NotFoundPage from "../pages/error/NotFoundPage.jsx";

// Import Housekeeping Pages (Nếu cần dùng thì uncomment children bên dưới)
// import DashboardPage from '../pages/housekeeping/DashboardPage.jsx';
// import TasksPage from '../pages/housekeeping/TasksPage.jsx';

export const router = createBrowserRouter([
    // 1. REDIRECT TRANG CHỦ
    {
        path: "/",
        element: <Navigate to="/user" replace />,
    },

    // 2. CUSTOMER ROUTES (Gộp tất cả public & protected của user vào đây)
    {
        path: "/user",
        element: <CustomerLayout />,
        children: [
            { index: true, element: <HomePage /> },
            { path: "rooms", element: <RoomsPage /> },
            { path: "rooms/:id", element: <RoomDetailPage /> },
            { path: "services", element: <ServicesPage /> },
            { path: "payment/vnpay-callback", element: <PaymentCallbackPage /> },

            // Các trang yêu cầu đăng nhập
            { path: "services/checkout", element: <ProtectedRoute><ServiceCheckoutPage /></ProtectedRoute> },
            { path: "booking", element: <ProtectedRoute><BookingPage /></ProtectedRoute> },
            { path: "booking/checkout", element: <ProtectedRoute><CheckoutPage /></ProtectedRoute> },
            { path: "history", element: <ProtectedRoute><BookingHistoryPage /></ProtectedRoute> },
        ],
    },

    // 3. AUTH ROUTES (Gộp authLayout)
    {
        element: <AuthLayout />,
        children: [
            // Hỗ trợ truy cập trực tiếp /login
            { path: "/login", element: <LoginPage /> },
            { path: "/register", element: <RegisterPage /> },
            { path: "/forgot-password", element: <div>Forgot Password</div> },
            // Hỗ trợ truy cập qua /auth/login
            { path: "/auth/login", element: <LoginPage /> },
            { path: "/auth/register", element: <RegisterPage /> },
        ],
    },

    // 4. OAUTH2 REDIRECT (Đã được tách ra rõ ràng, không bị ghi đè)
    {
        path: "/oauth2/redirect",
        element: <OAuth2RedirectPage />,
    },

    // 5. ADMIN ROUTES
    {
        path: "/admin",
        element: (
            <ProtectedRoute requiredRole="ADMIN">
                <AdminLayout />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <AdminDashboardPage /> },
            { path: "rooms", element: <RoomManagementPage /> },
            { path: "rooms/types", element: <RoomTypesPage /> },
            { path: "rooms/service", element: <ServiceManagementPage /> },
            { path: "staff", element: <StaffManagementPage /> },
            { path: "reservations", element: <div>Reservations Management</div> },
            { path: "customers", element: <UserManagementPage /> },
            { path: "schedules", element: <ScheduleManagementPage /> },
            { path: "housekeeping-tasks", element: <TaskManagementPage /> },
            { path: "payments", element: <PaymentManagementPage /> },
            { path: "reports", element: <div>Reports Management</div> },
            { path: "settings", element: <div>Settings</div> },
        ],
    },

    // 6. HOUSEKEEPING ROUTES
    {
        path: "/housekeeping",
        element: (
            <ProtectedRoute requiredRole="HOUSEKEEPING">
                <HousekeepingLayout />
            </ProtectedRoute>
        ),
        // children: [...]
    },

    // 7. RECEPTIONIST ROUTES
    {
        path: "/receptionist",
        element: (
            <ProtectedRoute requiredRole="RECEPTIONIST">
                <RecepDashboardPage />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <RecepDashboardPage /> },
            { path: "checkin", element: <div>Check-in</div> },
            { path: "checkout", element: <div>Check-out</div> },
            { path: "reservations", element: <div>Reservations</div> },
        ],
    },

    // 8. STAFF ROUTES
    {
        path: "/staff",
        element: (
            <ProtectedRoute requiredRole="STAFF">
                <StaffLayout />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <div>Staff Dashboard</div> },
            ...STAFF_ROUTES.map((item) => {
                const relativePath = item.path
                    .replace(/^\//, "")
                    .replace(/^staff\//, "")
                    .replace(/^\//, "");
                return {
                    path: relativePath,
                    element: item.element,
                };
            }),
        ],
    },

    // 9. SPECIAL / ERROR ROUTES
    {
        path: "/unauthorized",
        element: <UnauthorizedPage />,
    },
    {
        path: "*",
        element: <NotFoundPage />,
    },
]);