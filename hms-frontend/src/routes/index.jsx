// src/routes/index.jsx
import {createBrowserRouter} from 'react-router-dom';
import {CustomerLayout} from '../layouts/customer/CustomerLayout';
import {AdminLayout} from '../layouts/admin/AdminLayout';
import {HousekeepingLayout} from '../layouts/housekeeping/HousekeepingLayout';
import {ProtectedRoute} from './ProtectedRoute';
import HomePage from '../pages/customer/HomePage.jsx';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import AdminDashboardPage from '../pages/admin/DashboardPage';
import {AuthLayout} from "../layouts/AuthLayout.jsx";
import OAuth2RedirectPage from "../pages/auth/OAuth2RedirectPage.jsx";
import UnauthorizedPage from "../pages/error/UnauthorizedPage.jsx";
import NotFoundPage from "../pages/error/NotFoundPage.jsx";
import BookingHistoryPage from "../pages/customer/BookingHistoryPage.jsx";
import RoomsPage from "../pages/customer/RoomsPage.jsx";
import RecepDashboardPage from "../pages/receptionist/Dashboard.jsx";
import {StaffLayout} from "../layouts/staff/StaffLayout.jsx";

// Import Housekeeping Pages
import DashboardPage from '../pages/housekeeping/DashboardPage.jsx';
import TasksPage from '../pages/housekeeping/TasksPage.jsx';
import TaskDetailPage from '../pages/housekeeping/TaskDetailPage.jsx';
import SchedulePage from '../pages/housekeeping/SchedulePage.jsx';
import ReportsPage from '../pages/housekeeping/ReportsPage.jsx';

export const router = createBrowserRouter([
    // Public Customer Routes
    {
        path: '/',
        element: <CustomerLayout/>,
        children: [
            {index: true, element: <HomePage/>},
            {path: 'rooms', element: <RoomsPage/>},
        ],
    },

    // Auth Routes
    {
        path: '/',
        element: <AuthLayout/>,
        children: [
            {path: 'login', element: <LoginPage/>},
            {path: 'register', element: <RegisterPage/>},
            {path: 'forgot-password', element: <div>Forgot Password</div>},
        ],
    },

    // OAuth2 Redirect
    {
        path: '/oauth2/redirect',
        element: <OAuth2RedirectPage/>,
    },

    // Booking Routes (Protected)
    {
        path: '/booking',
        element: (
            <ProtectedRoute>
                <CustomerLayout/>
            </ProtectedRoute>
        ),
        children: [
            {path: ':roomId', element: <div>Booking Page</div>},
        ],
    },

    // Customer Protected Routes
    {
        path: '/customer',
        element: (
            <ProtectedRoute>
                <CustomerLayout/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <HomePage/>},
            {path: 'bookings', element: <BookingHistoryPage/>},
            {path: 'profile', element: <div>Profile Page</div>},
        ],
    },

    // Admin Routes
    {
        path: '/admin',
        element: (
            <ProtectedRoute requiredRole="ADMIN">
                <AdminLayout/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <AdminDashboardPage/>},
            {path: 'rooms', element: <div>Room Management</div>},
            {path: 'users', element: <div>User Management</div>},
            {path: 'bookings', element: <div>Booking Management</div>},
            {path: 'staff', element: <div>Staff Management</div>},
            {path: 'reports', element: <div>Reports</div>},
        ],
    },

    // Housekeeping Routes
    {
        path: '/housekeeping',
        element: (
            <ProtectedRoute requiredRole="HOUSEKEEPING">
                <HousekeepingLayout/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <DashboardPage/>},
            {path: 'dashboard', element: <DashboardPage/>},
            {path: 'tasks', element: <TasksPage/>},
            {path: 'tasks/:taskId', element: <TaskDetailPage/>},
            {path: 'schedule', element: <SchedulePage/>},
            {path: 'reports', element: <ReportsPage/>},
        ],
    },

    // Receptionist Routes
    {
        path: '/receptionist',
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

    // Staff Routes
    {
        path: '/staff',
        element: (
            <ProtectedRoute requiredRole="STAFF">
                <StaffLayout/>
            </ProtectedRoute>
        ),
        children: [
            {index: true, element: <div>Staff Dashboard</div>},
            {path: 'tasks', element: <div>Staff Tasks</div>},
        ],
    },

    // Error Routes
    {
        path: '/unauthorized',
        element: <UnauthorizedPage/>,
    },
    {
        path: '/404',
        element: <NotFoundPage/>,
    },
    {
        path: '*',
        element: <NotFoundPage/>,
    },
]);