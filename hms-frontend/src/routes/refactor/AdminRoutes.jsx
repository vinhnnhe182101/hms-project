import {Route} from "react-router-dom";
import {ProtectedRoute} from "../ProtectedRoute.jsx";
import {AdminLayout} from "../../layouts/admin/AdminLayout.jsx";
import AdminDashboardPage from "../../pages/admin/DashboardPage.jsx";

export const AdminRoutes = (
    <Route
        path="/admin"
        element={
            <ProtectedRoute>
                <AdminLayout/>
            </ProtectedRoute>
        }
    >
        <Route index element={<AdminDashboardPage/>}/>
        <Route path="rooms" element={<div>Room Management</div>}/>
        <Route path="users" element={<div>User Management</div>}/>
        <Route path="bookings" element={<div>Booking Management</div>}/>
    </Route>
);
