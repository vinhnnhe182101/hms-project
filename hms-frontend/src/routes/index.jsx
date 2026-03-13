import { createBrowserRouter } from 'react-router-dom';
import { CustomerLayout } from '../layouts/customer/CustomerLayout.jsx';
import { AdminLayout } from '../layouts/admin/AdminLayout.jsx';
import { HousekeepingLayout } from '../layouts/housekeeping/HousekeepingLayout.jsx';
import { ProtectedRoute } from './ProtectedRoute.jsx';
import HomePage from '../pages/customer/HomePage.jsx';
import RoomsPage from '../pages/customer/RoomsPage.jsx';
import RoomDetailPage from '../pages/customer/RoomDetailPage.jsx';
import ServicesPage from '../pages/customer/ServicesPage.jsx';
import BookingPage from '../pages/customer/BookingPage.jsx';
import CheckoutPage from '../pages/customer/CheckoutPage.jsx';
import ServiceCheckoutPage from '../pages/customer/ServiceCheckoutPage.jsx';
import BookingHistoryPage from '../pages/customer/BookingHistoryPage.jsx';
import LoginPage from '../pages/auth/LoginPage.jsx';
import RegisterPage from '../pages/auth/RegisterPage.jsx';
import AdminDashboardPage from '../pages/admin/DashboardPage.jsx';
import HousekeepingDashboardPage from '../pages/housekeeping/DashboardPage.jsx';
import { AuthLayout } from "../layouts/AuthLayout.jsx";
import OAuth2RedirectPage from "../pages/auth/OAuth2RedirectPage.jsx";
import UnauthorizedPage from "../pages/error/UnauthorizedPage.jsx";
import NotFoundPage from "../pages/error/NotFoundPage.jsx";
import MobileTasksPage from "../pages/housekeeping/MobileTasksPage.jsx";
import RecepDashboardPage from "../pages/receptionist/Dashboard.jsx";
import { StaffLayout } from "../layouts/staff/StaffLayout.jsx";

function BookingDetailPage() {
    return null;
}

export const router = createBrowserRouter([
    {
        path: '/',
        element: <CustomerLayout />,
        children: [
            { index: true, element: <HomePage /> },
            { path: 'rooms', element: <RoomsPage /> },
            { path: 'rooms/:id', element: <RoomDetailPage /> },
            { path: 'services', element: <ServicesPage /> },
            { path: 'services/checkout', element: <ProtectedRoute><ServiceCheckoutPage /></ProtectedRoute> },
            { path: 'booking', element: <ProtectedRoute><BookingPage /></ProtectedRoute> },
            { path: 'booking/checkout', element: <ProtectedRoute><CheckoutPage /></ProtectedRoute> },
            { path: 'history', element: <BookingHistoryPage /> },
        ],
    },
    {
        path: '/',
        element: <AuthLayout/>,
        children: [
            {path: 'login', element: <LoginPage/>},
            {path: 'register', element: <RegisterPage/>},
            {path: 'forgot-password', element: <div>Forgot Password</div>},
        ],
    },

    {
        path: '/booking',
        element: (
                <ProtectedRoute>
                    <CustomerLayout/>
                </ProtectedRoute>
        ),
        children: [],
    },
    {
        path: '/customer',
        element: (
                <ProtectedRoute>
                    <CustomerLayout/>
                </ProtectedRoute>
        ),
        children: [
            {path: 'bookings', element: <BookingHistoryPage/>},
            {path: 'bookings/:id', element: <BookingDetailPage/>},
        ],
    },
    {
        path: '/admin',
        element: (
                <ProtectedRoute>
                    <AdminLayout/>
                </ProtectedRoute>
        ),
        children: [
            {index: true, element: <AdminDashboardPage/>},
            {path: 'rooms', element: <div>Room Management</div>},
            {path: 'users', element: <div>User Management</div>},
            {path: 'bookings', element: <div>Booking Management</div>},
        ],
    },
    {
        path: '/housekeeping',
        element: (
                <ProtectedRoute>
                    <HousekeepingLayout/>
                </ProtectedRoute>
        ),
        children: [
            {index: true, element: <MobileTasksPage/>},
            {path: 'tasks', element: <MobileTasksPage/>},
        ],
    },
    {
        path: '/receptionist',
        element: (
                <ProtectedRoute>
                    <RecepDashboardPage/>
                </ProtectedRoute>
        ),
        children: [],
    },
    {
        path: '/staff',
        element: (
                <ProtectedRoute>
                    <StaffLayout/>
                </ProtectedRoute>
        )
    },
    {
        path: '/oauth2/redirect',
        element: <OAuth2RedirectPage/>,
    },
    {
        path: '/unauthorized',
        element: <UnauthorizedPage/>,
    },
    {
        path: '*',
        element: <NotFoundPage/>,
    },
]);