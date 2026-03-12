import { createBrowserRouter } from 'react-router-dom';
import { CustomerLayout } from '../layouts/customer/CustomerLayout';
import { AdminLayout } from '../layouts/admin/AdminLayout';
import { HousekeepingLayout } from '../layouts/housekeeping/HousekeepingLayout';
import { ProtectedRoute } from './ProtectedRoute';
import HomePage from '../pages/customer/HomePage.jsx';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import AdminDashboardPage from '../pages/admin/DashboardPage';
import HousekeepingDashboardPage from '../pages/housekeeping/DashboardPage';
import {AuthLayout} from "../layouts/AuthLayout.jsx";
import SchedulePage from "../pages/housekeeping/SchedulePage.jsx";
import TasksPage from "../pages/housekeeping/TasksPage.jsx";
import OAuth2RedirectPage from "../pages/auth/OAuth2RedirectPage.jsx";
import UnauthorizedPage from "../pages/error/UnauthorizedPage.jsx";
import NotFoundPage from "../pages/error/NotFoundPage.jsx";

export const router = createBrowserRouter([
    {
        path: '/',
        element: <CustomerLayout />,
        children: [
            { index: true, element: <HomePage /> },
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
        path: '/booking',
        element: (
            <ProtectedRoute>
                <CustomerLayout />
            </ProtectedRoute>
        ),
        children: [
        ],
    },
    {
        path: '/customer',
        element: (
            <ProtectedRoute>
                <CustomerLayout />
            </ProtectedRoute>
        ),
        children: [
        ],
    },
    {
        path: '/admin',
        element: (
            <ProtectedRoute>
                <AdminLayout />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <AdminDashboardPage /> },
            { path: 'rooms', element: <div>Room Management</div> },
            { path: 'users', element: <div>User Management</div> },
            { path: 'bookings', element: <div>Booking Management</div> },
        ],
    },
    {
        path: '/housekeeping',
        element: (
            <ProtectedRoute>
                <HousekeepingLayout />
            </ProtectedRoute>
        ),
        children: [
            { index: true, element: <HousekeepingDashboardPage /> },
            { path: 'tasks', element: <TasksPage /> },
            { path: 'schedule', element: <SchedulePage /> },
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