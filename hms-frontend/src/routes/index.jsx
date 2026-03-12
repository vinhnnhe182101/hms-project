import { createBrowserRouter } from 'react-router-dom';
import CustomerLayout from '../layouts/customer/CustomerLayout.jsx';
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
import {AuthLayout} from "../layouts/AuthLayout.jsx";
import OAuth2RedirectPage from "../pages/auth/OAuth2RedirectPage.jsx";
import UnauthorizedPage from "../pages/error/UnauthorizedPage.jsx";
import NotFoundPage from "../pages/error/NotFoundPage.jsx";

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
        path: '/auth',
        element: <AuthLayout />,
        children: [
            { path: 'login', element: <LoginPage /> },
            { path: 'register', element: <RegisterPage /> },
            { path: 'forgot-password', element: <div>Forgot Password</div> },
        ],
    },
    {
        path: '/login',
        element: <LoginPage />,
    },
    {
        path: '/admin',
        element: (
            <ProtectedRoute requiredRole="ADMIN">
                <AdminLayout />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <AdminDashboardPage /> },
            { path: 'rooms', element: <div>Room Management</div> },
            { path: 'reservations', element: <div>Reservation Management</div> },
        ],
    },
    {
        path: '/housekeeping',
        element: (
            <ProtectedRoute requiredRole="HOUSEKEEPING">
                <HousekeepingLayout />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <HousekeepingDashboardPage /> },
        ],
    },
    {
        path: '/oauth2/redirect',
        element: <OAuth2RedirectPage />,
    },
    {
        path: '/unauthorized',
        element: <UnauthorizedPage />,
    },
    {
        path: '*',
        element: <NotFoundPage />,
    },
]);
